package se.tmconnect.husloggen.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.PostFel
import se.tmconnect.husloggen.regler.UppgiftFel
import se.tmconnect.husloggen.regler.beloppOreTillFaltText
import se.tmconnect.husloggen.regler.sorteraUppgifter
import se.tmconnect.husloggen.regler.tolkaBeloppTillOre
import se.tmconnect.husloggen.regler.valideraPost
import se.tmconnect.husloggen.regler.valideraUppgift

data class UppgiftFormularTillstand(
    val redigerarId: Long = 0L,
    val namn: String = "",
    val intervallText: String = "",
    val startDatum: LocalDate = LocalDate.now(),
    val paminnelse: Boolean = true,
    val anteckning: String = "",
    val valideringsFel: UppgiftFel? = null,
)

data class PostFormularTillstand(
    val redigerarId: Long = 0L,
    val vad: String = "",
    val datum: LocalDate = LocalDate.now(),
    val typ: PostTyp = PostTyp.UNDERHALL,
    val beloppText: String = "",
    val utforare: String = "",
    val uppgiftId: Long? = null,
    val anteckning: String = "",
    val bildFilnamn: List<String> = emptyList(),
    val valideringsFel: PostFel? = null,
)

sealed class HusUiMeddelande {
    data object UppgiftMarkeradGjord : HusUiMeddelande()
}

class HusViewModel(
    private val repository: HusRepository,
    private val idag: LocalDate = LocalDate.now(),
) : ViewModel() {
    val uppgifter: StateFlow<List<Uppgift>> = repository.uppgifter
        .map { sorteraUppgifter(it, idag) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val poster: StateFlow<List<Post>> = repository.poster
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formular = MutableStateFlow<UppgiftFormularTillstand?>(null)
    val formular: StateFlow<UppgiftFormularTillstand?> = _formular.asStateFlow()

    private val _postFormular = MutableStateFlow<PostFormularTillstand?>(null)
    val postFormular: StateFlow<PostFormularTillstand?> = _postFormular.asStateFlow()

    private val _postDetaljId = MutableStateFlow<Long?>(null)
    val postDetaljId: StateFlow<Long?> = _postDetaljId.asStateFlow()

    private val _meddelanden = MutableSharedFlow<HusUiMeddelande>()
    val meddelanden: SharedFlow<HusUiMeddelande> = _meddelanden.asSharedFlow()

    /** Filer tillagda under pågående formulärsession som ska raderas vid avbryt utan spara. */
    private var bilderTillagdaISession: Set<String> = emptySet()

    /** Bilder som fanns när formuläret öppnades (för att veta vad som ska städas vid avbryt). */
    private var bilderVidOppning: Set<String> = emptySet()

    fun visaNyUppgift() {
        _formular.value = UppgiftFormularTillstand(startDatum = idag)
    }

    fun visaAndraUppgift(uppgiftId: Long) {
        viewModelScope.launch {
            val uppgift = repository.hamtaUppgift(uppgiftId) ?: return@launch
            _formular.value = UppgiftFormularTillstand(
                redigerarId = uppgift.id,
                namn = uppgift.namn,
                intervallText = uppgift.intervallManader.toString(),
                startDatum = uppgift.startDatum,
                paminnelse = uppgift.paminnelse,
                anteckning = uppgift.anteckning.orEmpty(),
            )
        }
    }

    fun stangFormular() {
        _formular.value = null
    }

    fun uppdateraFormularNamn(namn: String) {
        uppdateraFormular { it.copy(namn = namn, valideringsFel = null) }
    }

    fun uppdateraFormularIntervall(text: String) {
        uppdateraFormular { it.copy(intervallText = text, valideringsFel = null) }
    }

    fun uppdateraFormularStartdatum(datum: LocalDate) {
        uppdateraFormular { it.copy(startDatum = datum) }
    }

    fun uppdateraFormularPaminnelse(pa: Boolean) {
        uppdateraFormular { it.copy(paminnelse = pa) }
    }

    fun uppdateraFormularAnteckning(text: String) {
        uppdateraFormular { it.copy(anteckning = text) }
    }

    fun appliceraForslag(namn: String, intervallManader: Int) {
        uppdateraFormular {
            it.copy(
                namn = namn,
                intervallText = intervallManader.toString(),
                valideringsFel = null,
            )
        }
    }

    fun sparaFormular(): Boolean {
        val tillstand = _formular.value ?: return false
        val intervall = tillstand.intervallText.trim().toIntOrNull()
        val fel = valideraUppgift(tillstand.namn, intervall)
        if (fel != null) {
            _formular.value = tillstand.copy(valideringsFel = fel)
            return false
        }
        viewModelScope.launch {
            val befintlig = if (tillstand.redigerarId != 0L) {
                repository.hamtaUppgift(tillstand.redigerarId)
            } else {
                null
            }
            val uppgift = Uppgift(
                id = tillstand.redigerarId,
                namn = tillstand.namn.trim(),
                intervallManader = intervall!!,
                startDatum = tillstand.startDatum,
                senastGjord = befintlig?.senastGjord,
                paminnelse = tillstand.paminnelse,
                anteckning = tillstand.anteckning.trim().ifEmpty { null },
                skapad = befintlig?.skapad ?: System.currentTimeMillis(),
            )
            repository.sparaUppgift(uppgift)
            _formular.value = null
        }
        return true
    }

    fun taBortFormularUppgift() {
        val tillstand = _formular.value ?: return
        if (tillstand.redigerarId == 0L) return
        viewModelScope.launch {
            repository.hamtaUppgift(tillstand.redigerarId)?.let { repository.taBortUppgift(it) }
            _formular.value = null
        }
    }

    fun markeraSomGjord(uppgiftId: Long) {
        viewModelScope.launch {
            val uppgift = repository.hamtaUppgift(uppgiftId) ?: return@launch
            repository.markeraSomGjord(uppgift, idag)
            _meddelanden.emit(HusUiMeddelande.UppgiftMarkeradGjord)
        }
    }

    fun visaNyPost() {
        _postDetaljId.value = null
        nollstallBildSession(emptyList())
        _postFormular.value = PostFormularTillstand(datum = idag)
    }

    fun visaPostDetalj(postId: Long) {
        _postFormular.value = null
        _postDetaljId.value = postId
    }

    fun stangPostDetalj() {
        _postDetaljId.value = null
    }

    fun visaAndraPost(postId: Long) {
        viewModelScope.launch {
            val post = repository.hamtaPost(postId) ?: return@launch
            _postDetaljId.value = null
            val bilder = repository.lasBilder(post)
            nollstallBildSession(bilder)
            _postFormular.value = PostFormularTillstand(
                redigerarId = post.id,
                vad = post.vad,
                datum = post.datum,
                typ = post.typ,
                beloppText = post.beloppOre?.let { beloppOreTillFaltText(it) }.orEmpty(),
                utforare = post.utforare.orEmpty(),
                uppgiftId = post.uppgiftId,
                anteckning = post.anteckning.orEmpty(),
                bildFilnamn = bilder,
            )
        }
    }

    fun stangPostFormular() {
        rensaOsparadeBilder()
        _postFormular.value = null
    }

    fun uppdateraPostVad(vad: String) {
        uppdateraPostFormular { it.copy(vad = vad, valideringsFel = null) }
    }

    fun uppdateraPostDatum(datum: LocalDate) {
        uppdateraPostFormular { it.copy(datum = datum, valideringsFel = null) }
    }

    fun uppdateraPostTyp(typ: PostTyp) {
        uppdateraPostFormular { it.copy(typ = typ) }
    }

    fun uppdateraPostBelopp(text: String) {
        uppdateraPostFormular { it.copy(beloppText = text, valideringsFel = null) }
    }

    fun uppdateraPostUtforare(text: String) {
        uppdateraPostFormular { it.copy(utforare = text) }
    }

    fun uppdateraPostUppgift(uppgiftId: Long?) {
        uppdateraPostFormular { it.copy(uppgiftId = uppgiftId) }
    }

    fun uppdateraPostAnteckning(text: String) {
        uppdateraPostFormular { it.copy(anteckning = text) }
    }

    fun sparaPostFormular(): Boolean {
        val tillstand = _postFormular.value ?: return false
        val fel = valideraPost(tillstand.vad, tillstand.datum, idag, tillstand.beloppText)
        if (fel != null) {
            _postFormular.value = tillstand.copy(valideringsFel = fel)
            return false
        }
        viewModelScope.launch {
            val befintlig = if (tillstand.redigerarId != 0L) {
                repository.hamtaPost(tillstand.redigerarId)
            } else {
                null
            }
            val beloppOre = tillstand.beloppText.trim().takeIf { it.isNotEmpty() }
                ?.let { tolkaBeloppTillOre(it) }
            val gamlaBilder = befintlig?.let { repository.lasBilder(it) }.orEmpty()
            val nyaBilder = tillstand.bildFilnamn
            gamlaBilder.filter { it !in nyaBilder }.forEach { repository.taBortBildfil(it) }

            val post = Post(
                id = tillstand.redigerarId,
                datum = tillstand.datum,
                vad = tillstand.vad.trim(),
                typ = tillstand.typ,
                beloppOre = beloppOre,
                utforare = tillstand.utforare.trim().ifEmpty { null },
                anteckning = tillstand.anteckning.trim().ifEmpty { null },
                uppgiftId = tillstand.uppgiftId,
                kvittoFil = befintlig?.kvittoFil,
                bilder = formateraBilder(nyaBilder),
                skapad = befintlig?.skapad ?: System.currentTimeMillis(),
            )
            repository.sparaPost(post)
            bilderTillagdaISession = emptySet()
            _postFormular.value = null
        }
        return true
    }

    fun kanLaggaTillBildIFormular(): Boolean {
        val antal = _postFormular.value?.bildFilnamn?.size ?: 0
        return antal < HusRepository.MAX_BILDER_PER_POST
    }

    fun laggTillBildUri(uri: Uri, onFel: () -> Unit = {}) {
        val tillstand = _postFormular.value ?: return
        if (!kanLaggaTillBildIFormular()) {
            onFel()
            return
        }
        viewModelScope.launch {
            if (tillstand.redigerarId != 0L) {
                val ok = withContext(Dispatchers.IO) {
                    repository.laggTillBild(tillstand.redigerarId, uri)
                }
                if (!ok) {
                    onFel()
                    return@launch
                }
                val post = repository.hamtaPost(tillstand.redigerarId) ?: return@launch
                uppdateraPostFormular { it.copy(bildFilnamn = repository.lasBilder(post)) }
            } else {
                val filnamn = withContext(Dispatchers.IO) { repository.kopieraBildTillLagring(uri) }
                if (filnamn == null) {
                    onFel()
                    return@launch
                }
                bilderTillagdaISession = bilderTillagdaISession + filnamn
                uppdateraPostFormular { it.copy(bildFilnamn = it.bildFilnamn + filnamn) }
            }
        }
    }

    fun taBortBildFranFormular(filnamn: String) {
        val tillstand = _postFormular.value ?: return
        viewModelScope.launch {
            if (tillstand.redigerarId != 0L) {
                repository.taBortBild(tillstand.redigerarId, filnamn)
            } else {
                repository.taBortBildfil(filnamn)
                bilderTillagdaISession = bilderTillagdaISession - filnamn
            }
            uppdateraPostFormular { it.copy(bildFilnamn = it.bildFilnamn - filnamn) }
        }
    }

    fun bildUri(filnamn: String): Uri = repository.bildUri(filnamn)

    fun skapaKameraFotoUri(): Uri = repository.skapaKameraFotoUri()

    fun lasBilderForPost(post: Post): List<String> = repository.lasBilder(post)

    fun taBortPost(postId: Long) {
        viewModelScope.launch {
            repository.hamtaPost(postId)?.let { repository.taBortPost(it) }
            _postDetaljId.value = null
            _postFormular.value = null
        }
    }

    fun taBortPostFormularPost() {
        val tillstand = _postFormular.value ?: return
        if (tillstand.redigerarId == 0L) return
        taBortPost(tillstand.redigerarId)
    }

    private fun uppdateraFormular(block: (UppgiftFormularTillstand) -> UppgiftFormularTillstand) {
        _formular.value = _formular.value?.let(block)
    }

    private fun uppdateraPostFormular(block: (PostFormularTillstand) -> PostFormularTillstand) {
        _postFormular.value = _postFormular.value?.let(block)
    }

    private fun nollstallBildSession(bilder: List<String>) {
        bilderVidOppning = bilder.toSet()
        bilderTillagdaISession = emptySet()
    }

    private fun rensaOsparadeBilder() {
        val tillstand = _postFormular.value ?: return
        val nu = tillstand.bildFilnamn.toSet()
        val skaBehalla = bilderVidOppning
        (nu - skaBehalla).forEach { repository.taBortBildfil(it) }
        bilderTillagdaISession = emptySet()
    }
}

class HusViewModelFactory(
    private val repository: HusRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HusViewModel::class.java)) {
            return HusViewModel(repository) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
