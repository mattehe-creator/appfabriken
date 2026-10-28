package se.tmconnect.rostfangst.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.rostfangst.data.FangstEntitet
import se.tmconnect.rostfangst.data.InstallningarLager
import se.tmconnect.rostfangst.data.RostfangstRepository
import se.tmconnect.rostfangst.handling.HandlingUtforare
import se.tmconnect.rostfangst.handling.UtforResultat
import se.tmconnect.rostfangst.ha.HaKonfig
import se.tmconnect.rostfangst.ha.HaKlient
import se.tmconnect.rostfangst.ha.HaKlientFabrik
import se.tmconnect.rostfangst.ha.HaHttpKlient
import se.tmconnect.rostfangst.ha.haSteg
import se.tmconnect.rostfangst.ha.korHaSteg
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import se.tmconnect.rostfangst.regler.byggSystemprompt
import se.tmconnect.rostfangst.tolking.AnthropicTolkare
import se.tmconnect.rostfangst.tolking.Tolkare

enum class RostfangstFlik {
    START,
    ATT_GORA,
    ANTECKNINGAR,
    HISTORIK,
    INSTALLNINGAR,
}

data class InspelningsTillstand(
    val lyssnar: Boolean = false,
    val deltext: String = "",
    val fangstId: Long? = null,
    val text: String = "",
    val forslag: Forslag? = null,
    val fel: String? = null,
    val offlineSparad: Boolean = false,
)

class RostfangstViewModel(
    private val repository: RostfangstRepository,
    private val installningar: InstallningarLager,
    private val appContext: Context,
    private val tolkareFabrik: (String, String) -> Tolkare = { nyckel, modell ->
        AnthropicTolkare(nyckel, modell)
    },
    private val haKlientFabrik: (HaKonfig) -> HaKlient = { HaHttpKlient(it.adress, it.token) },
    private val zon: ZoneId = ZoneId.of("Europe/Stockholm"),
) : ViewModel() {
    private val _inspelning = MutableStateFlow(InspelningsTillstand())
    val inspelning: StateFlow<InspelningsTillstand> = _inspelning.asStateFlow()

    private val _flik = MutableStateFlow(RostfangstFlik.START)
    val flik: StateFlow<RostfangstFlik> = _flik.asStateFlow()

    private val _meddelande = MutableStateFlow<String?>(null)
    val meddelande: StateFlow<String?> = _meddelande.asStateFlow()

    val attGora = repository.attGora.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val anteckningar = repository.anteckningar.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val historik = repository.historik.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val installningarUi = combine(
        installningar.apiNyckel,
        installningar.modell,
        installningar.haAdress,
        installningar.haToken,
        installningar.haTodo
    ) { nyckel, modell, haAdress, haToken, haTodo ->
        InstallningarUi(nyckel, modell, haAdress, haToken, haTodo)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InstallningarUi("", InstallningarLager.DEFAULT_MODELL))

    private val _fangsterPerDag = MutableStateFlow<List<Pair<LocalDate, Int>>>(emptyList())
    val fangsterPerDag: StateFlow<List<Pair<LocalDate, Int>>> = _fangsterPerDag.asStateFlow()

    init {
        uppdateraFangsterPerDag()
    }

    fun valjFlik(flik: RostfangstFlik) {
        _flik.value = flik
    }

    fun rensaMeddelande() {
        _meddelande.value = null
    }

    fun borjaLyssna() {
        _inspelning.value = InspelningsTillstand(lyssnar = true)
    }

    fun uppdateraDeltext(text: String) {
        _inspelning.value = _inspelning.value.copy(deltext = text)
    }

    fun avslutaLyssning(text: String) {
        viewModelScope.launch {
            val trimmed = text.trim()
            if (trimmed.isEmpty()) {
                _inspelning.value = InspelningsTillstand()
                _meddelande.value = "tom"
                return@launch
            }
            val id = repository.sparaInnanTolkning(trimmed)
            _inspelning.value = InspelningsTillstand(text = trimmed, fangstId = id)
            tolkaFangst(id, trimmed)
            uppdateraFangsterPerDag()
        }
    }

    fun avbrytLyssning() {
        _inspelning.value = InspelningsTillstand()
    }

    fun rostaOmTolkning(fangst: FangstEntitet) {
        viewModelScope.launch {
            _inspelning.value = InspelningsTillstand(
                text = fangst.originalText,
                fangstId = fangst.id,
            )
            tolkaFangst(fangst.id, fangst.originalText)
        }
    }

    private suspend fun tolkaFangst(id: Long, text: String) {
        if (!harNat()) {
            repository.sparaUtanNat(id)
            _inspelning.value = _inspelning.value.copy(
                forslag = Forslag(ForslagTyp.ANTECKNING, text),
                offlineSparad = true,
            )
            return
        }
        val ui = installningarUi.value
        val tolkare = tolkareFabrik(ui.apiNyckel, ui.modell)
        val prompt = byggSystemprompt(ZonedDateTime.now(zon))
        val result = tolkare.tolka(text, prompt)
        result.fold(
            onSuccess = { forslag ->
                repository.uppdateraEfterTolkning(id, forslag, tolkad = true)
                _inspelning.value = _inspelning.value.copy(forslag = forslag, offlineSparad = false)
            },
            onFailure = {
                repository.sparaUtanNat(id)
                _inspelning.value = _inspelning.value.copy(
                    forslag = Forslag(ForslagTyp.ANTECKNING, text),
                    offlineSparad = true,
                    fel = it.message,
                )
            },
        )
    }

    fun uppdateraForslag(forslag: Forslag) {
        val id = _inspelning.value.fangstId ?: return
        viewModelScope.launch {
            repository.uppdateraForslag(id, forslag)
            _inspelning.value = _inspelning.value.copy(forslag = forslag)
        }
    }

    fun utforForslag(onKalender: (android.content.Intent) -> Unit) {
        val tillstand = _inspelning.value
        val forslag = tillstand.forslag ?: return
        val id = tillstand.fangstId ?: return
        val original = tillstand.text
        viewModelScope.launch {
            when (val result = HandlingUtforare.utfor(appContext, id, forslag)) {
                UtforResultat.Klar -> {
                    val meddelandeText = skickaTillHa(forslag, original) ?: "klar"
                    if (forslag.typ == ForslagTyp.ATT_GORA) {
                        repository.markeraKlar(id, false)
                    }
                    _meddelande.value = meddelandeText
                    _inspelning.value = InspelningsTillstand()
                }
                UtforResultat.IngenHandling -> _meddelande.value = "saknar_tid"
                is UtforResultat.Kalender -> {
                    onKalender(result.intent)
                    val meddelandeText = skickaTillHa(forslag, original)
                    _meddelande.value = meddelandeText ?: "kalender"
                    _inspelning.value = InspelningsTillstand()
                }
            }
        }
    }

    fun togglaAttGora(id: Long, klar: Boolean) {
        viewModelScope.launch {
            repository.markeraKlar(id, klar)
        }
    }

    fun sparaInstallningar(nyckel: String, modell: String) {
        viewModelScope.launch {
            installningar.sparaApiNyckel(nyckel)
            installningar.sparaModell(modell)
            _meddelande.value = "sparad"
            repository.vantarTolkning().forEach { fangst ->
                tolkaFangst(fangst.id, fangst.originalText)
            }
        }
    }

    fun sparaHaInstallningar(adress: String, token: String, todo: String) {
        viewModelScope.launch {
            if (adress.isBlank() && token.isBlank() && todo.isBlank()) {
                installningar.sparaHa("", "", "")
                _meddelande.value = "sparad"
                return@launch
            }
            
            val konfig = haKonfig(adress, token, todo)
            if (konfig == null) {
                _meddelande.value = "ha_adress"
                return@launch
            }
            
            installningar.sparaHa(konfig.adress, konfig.token, konfig.todoEntity)
            _meddelande.value = "sparad"
        }
    }

    private suspend fun skickaTillHa(forslag: Forslag, original: String): String? {
        val adress = installningar.haAdress.first()
        val token = installningar.haToken.first()
        val todo = installningar.haTodo.first()
        
        val konfig = haKonfig(adress, token, todo)
        
        if (forslag.typ == ForslagTyp.HEM && konfig == null) {
            return "ha_saknas"
        }
        
        val steg = haSteg(forslag.typ, konfig)
        if (steg.isEmpty()) {
            return null
        }
        
        val res = withContext(Dispatchers.IO) {
            korHaSteg(steg, haKlientFabrik(konfig!!), konfig, forslag, original)
        }
        
        return res.svar ?: res.fel.firstOrNull()
    }

    fun uppdateraFangsterPerDag() {
        viewModelScope.launch {
            _fangsterPerDag.value = repository.fangsterPerDagSenaste30(LocalDate.now(zon))
        }
    }

    suspend fun hamtaHistorikCsv(): String {
        val lista = historik.first()
        return se.tmconnect.rostfangst.export.historikSomCsv(repository.historikRader(lista))
    }

    private fun harNat(): Boolean {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nat = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nat) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

data class InstallningarUi(
    val apiNyckel: String, 
    val modell: String,
    val haAdress: String = "",
    val haToken: String = "",
    val haTodo: String = ""
)

class RostfangstViewModelFactory(
    private val repository: RostfangstRepository,
    private val installningar: InstallningarLager,
    private val appContext: Context,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RostfangstViewModel(repository, installningar, appContext) as T
    }
}
