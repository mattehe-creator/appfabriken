package se.tmconnect.garantivalvet.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.data.KvittoLager
import se.tmconnect.garantivalvet.regler.sorteraEfterGaranti

class KopViewModel(
    private val repository: KopRepository,
    private val kvittoLager: KvittoLager,
    private val idag: LocalDate = LocalDate.now(),
) : ViewModel() {
    val kopLista: StateFlow<List<Kop>> =
        repository.alla
            .map { lista -> sorteraEfterGaranti(lista, idag) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    private val _aktivSkarm = MutableStateFlow<KopSkarm>(KopSkarm.Lista)
    val aktivSkarm: StateFlow<KopSkarm> = _aktivSkarm.asStateFlow()

    private val _valtKop = MutableStateFlow<Kop?>(null)
    val valtKop: StateFlow<Kop?> = _valtKop.asStateFlow()

    private val _tillfalligtKvittoUri = MutableStateFlow<Uri?>(null)

    /** Vald eller nytt kamerafoto i formuläret, innan köpet sparas. */
    val tillfalligtKvittoUri: StateFlow<Uri?> = _tillfalligtKvittoUri.asStateFlow()

    private var kvittoSkaTasBortVidSpara = false

    fun visaLista() {
        _aktivSkarm.value = KopSkarm.Lista
        _valtKop.value = null
        rensaKvittoFormularTillstand()
    }

    fun visaLaggTill() {
        _aktivSkarm.value = KopSkarm.LaggTill
        _valtKop.value = null
        rensaKvittoFormularTillstand()
    }

    fun visaDetalj(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Detalj(kopId)
            rensaKvittoFormularTillstand()
        }
    }

    fun visaAndra(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Andra(kopId)
            rensaKvittoFormularTillstand()
        }
    }

    /** Öppnar helskärmsvy för köpets kvitto. */
    fun visaKvittoHelskarm(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.KvittoHelskarm(kopId)
        }
    }

    /**
     * URI för kvittoförhandsvisning i formuläret: tillfällig bild om sådan finns,
     * annars sparat kvitto för [kop] om användaren inte markerat borttagning.
     */
    fun kvittoUriForFormular(kop: Kop?): Uri? {
        _tillfalligtKvittoUri.value?.let { return it }
        if (kvittoSkaTasBortVidSpara) {
            return null
        }
        val filnamn = kop?.kvittoFil ?: return null
        return kvittoLager.innehallUriForFilnamn(filnamn)
    }

    /**
     * FileProvider-URI för ett sparat kvitto, eller null om köpet saknar kvitto.
     */
    fun sparatKvittoUri(kop: Kop): Uri? {
        val filnamn = kop.kvittoFil ?: return null
        return kvittoLager.innehallUriForFilnamn(filnamn)
    }

    /** Anropas när användaren valt bild eller tagit foto i formuläret. */
    fun sattTillfalligtKvitto(uri: Uri) {
        kvittoSkaTasBortVidSpara = false
        _tillfalligtKvittoUri.value = uri
    }

    /** Tar bort kvittot i formuläret (sparat kvitto tas bort vid Spara). */
    fun markeraKvittoForBorttagning() {
        _tillfalligtKvittoUri.value = null
        kvittoSkaTasBortVidSpara = true
    }

    /**
     * URI för kamera-intent. Anropa [sattTillfalligtKvitto] med samma URI när fotot är klart.
     */
    fun skapaKameraKvittoUri(): Uri = kvittoLager.skapaKameraFotoUri()

    /**
     * Byter eller lägger till kvitto på ett befintligt köp och sparar direkt i databasen.
     */
    fun uppdateraKvittoPaKop(kopId: Long, kallaUri: Uri) {
        viewModelScope.launch {
            val kop = repository.hamta(kopId) ?: return@launch
            kvittoLager.taBort(kop.kvittoFil)
            val filnamn = kvittoLager.kopieraFranUri(kallaUri) ?: return@launch
            val uppdaterat = kop.copy(kvittoFil = filnamn)
            repository.spara(uppdaterat)
            if (_valtKop.value?.id == kopId) {
                _valtKop.value = uppdaterat
            }
        }
    }

    /** Tar bort kvittofilen från ett köp och sparar direkt. */
    fun taBortKvittoFranKop(kopId: Long) {
        viewModelScope.launch {
            val kop = repository.hamta(kopId) ?: return@launch
            if (kop.kvittoFil == null) {
                return@launch
            }
            kvittoLager.taBort(kop.kvittoFil)
            val uppdaterat = kop.copy(kvittoFil = null)
            repository.spara(uppdaterat)
            if (_valtKop.value?.id == kopId) {
                _valtKop.value = uppdaterat
            }
        }
    }

    fun sparaNyttKop(
        vad: String,
        varKopt: String?,
        kopdatum: LocalDate,
        garantiManader: Int,
        prisOre: Long?,
        anteckning: String?,
    ) {
        viewModelScope.launch {
            var kop = Kop(
                vad = vad.trim(),
                varKopt = varKopt?.trim()?.takeIf { it.isNotEmpty() },
                kopdatum = kopdatum,
                garantiManader = garantiManader,
                prisOre = prisOre,
                anteckning = anteckning?.trim()?.takeIf { it.isNotEmpty() },
            )
            kop = kop.copy(id = repository.spara(kop))
            kop = appliceraKvittoAndringar(kop)
            if (kop.kvittoFil != null) {
                repository.spara(kop)
            }
            visaLista()
        }
    }

    fun uppdateraKop(kop: Kop) {
        viewModelScope.launch {
            val medKvitto = appliceraKvittoAndringar(kop)
            repository.spara(medKvitto)
            visaLista()
        }
    }

    fun taBortValtKop() {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            repository.taBort(kop)
            visaLista()
        }
    }

    private suspend fun appliceraKvittoAndringar(kop: Kop): Kop {
        when {
            kvittoSkaTasBortVidSpara -> {
                kvittoLager.taBort(kop.kvittoFil)
                rensaKvittoFormularTillstand()
                return kop.copy(kvittoFil = null)
            }
            _tillfalligtKvittoUri.value != null -> {
                kvittoLager.taBort(kop.kvittoFil)
                val filnamn = kvittoLager.kopieraFranUri(_tillfalligtKvittoUri.value!!)
                    ?: return kop
                rensaKvittoFormularTillstand()
                return kop.copy(kvittoFil = filnamn)
            }
            else -> return kop
        }
    }

    private fun rensaKvittoFormularTillstand() {
        _tillfalligtKvittoUri.value = null
        kvittoSkaTasBortVidSpara = false
    }
}

class KopViewModelFactory(
    private val repository: KopRepository,
    private val kvittoLager: KvittoLager,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KopViewModel::class.java)) {
            return KopViewModel(repository, kvittoLager) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
