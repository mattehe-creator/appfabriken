package se.tmconnect.husloggen.ui

import android.app.Activity
import android.content.Context
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import se.tmconnect.karna.tema.TemaLage
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.MarkeraGjordResult
import se.tmconnect.husloggen.data.SparaPostResult
import se.tmconnect.husloggen.export.HusHistorikTexter
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult
import se.tmconnect.husloggen.export.SakerhetskopiaPlanerare
import se.tmconnect.husloggen.export.byggHusHistorik
import se.tmconnect.husloggen.export.KommandeUnderhallCsvTexter
import se.tmconnect.husloggen.export.husHistorikCsv
import se.tmconnect.husloggen.export.kommandeUnderhallCsv
import se.tmconnect.husloggen.regler.kommandeUnderhall
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr
import se.tmconnect.husloggen.export.skrivHusHistorikTillPdf
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.standardForsaljningsar
import se.tmconnect.karna.export.PdfExportResult
import se.tmconnect.karna.export.skrivCsvMedBom
import java.util.Locale
import se.tmconnect.husloggen.regler.kanLaggaTillBostad
import se.tmconnect.husloggen.regler.kanLaggaTillPost
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.sakerhetskopia.AutoBackupTillstand
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.data.Avlasning
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.PlaneradKostnad
import se.tmconnect.husloggen.data.Matare
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.SparaBostadResult
import se.tmconnect.husloggen.data.TaBortBostadResult
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.export.AvlasningCsvTexter
import se.tmconnect.husloggen.export.avlasningCsv
import se.tmconnect.husloggen.regler.AvlasningFel
import se.tmconnect.husloggen.regler.MatareFel
import se.tmconnect.husloggen.regler.PostFel
import se.tmconnect.husloggen.regler.UppgiftFel
import se.tmconnect.husloggen.regler.beraknaAvlasningarMedForbrukning
import se.tmconnect.husloggen.regler.foregaendeAvlasning
import se.tmconnect.husloggen.regler.tolkaMatareVardeTillTusendel
import se.tmconnect.husloggen.regler.valideraAvlasning
import se.tmconnect.husloggen.regler.valideraMatare
import se.tmconnect.husloggen.regler.beloppOreTillFaltText
import se.tmconnect.husloggen.regler.sorteraUppgifter
import se.tmconnect.husloggen.regler.tolkaBeloppTillOre
import se.tmconnect.husloggen.regler.tolkaValfrittOreHeltal
import se.tmconnect.husloggen.regler.valideraPost
import se.tmconnect.husloggen.regler.valideraUppgift
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.husloggen.paminnelse.PaminnelseWorker
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.paminnelse.PaminnelsePlanerare
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori
import se.tmconnect.husloggen.data.KopieraDokumentResult
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.data.skapaHusDokumentLager
import se.tmconnect.husloggen.regler.DokumentFel
import se.tmconnect.husloggen.regler.valideraDokument
import se.tmconnect.husloggen.regler.PlaneradKostnadFel
import se.tmconnect.husloggen.regler.valideraPlaneradKostnad

data class UppgiftFormularTillstand(
    val redigerarId: Long = 0L,
    val namn: String = "",
    val intervallText: String = "",
    val startDatum: LocalDate = LocalDate.now(),
    val paminnelse: Boolean = true,
    val anteckning: String = "",
    val valideringsFel: UppgiftFel? = null,
)

data class MatareFormularTillstand(
    val redigerarId: Long = 0L,
    val namn: String = "",
    val enhet: String = "",
    val prisOrePerEnhetText: String = "",
    val anteckning: String = "",
    val valideringsFel: MatareFel? = null,
)

data class AvlasningFormularTillstand(
    val matareId: Long,
    val redigerarId: Long = 0L,
    val datum: LocalDate = LocalDate.now(),
    val vardeText: String = "",
    val valideringsFel: AvlasningFel? = null,
    val visarLagreVardeBekraftelse: Boolean = false,
)

data class DokumentFormularTillstand(
    val redigerarId: Long = 0L,
    val titel: String = "",
    val kategori: DokumentKategori? = null,
    val anteckning: String = "",
    val filnamn: String? = null,
    val mimeTyp: String? = null,
    val valideringsFel: DokumentFel? = null,
)

data class PostFormularTillstand(
    val redigerarId: Long = 0L,
    val vad: String = "",
    val datum: LocalDate = LocalDate.now(),
    val typ: PostTyp = PostTyp.UNDERHALL,
    val beloppText: String = "",
    val rotRutAvdragText: String = "",
    val utforare: String = "",
    val uppgiftId: Long? = null,
    val anteckning: String = "",
    val bildFilnamn: List<String> = emptyList(),
    val valideringsFel: PostFel? = null,
    /** Planerad kostnad tas bort först när posten sparats. */
    val planeradKostnadIdEfterSparad: Long? = null,
)

/** Formulär för en ny post som kopierar [post]: dagens datum, inga bilder, inte kopplad till en uppgift. */
fun kopieraPostTillFormular(post: Post, idag: LocalDate): PostFormularTillstand =
    PostFormularTillstand(
        vad = post.vad,
        datum = idag,
        typ = post.typ,
        beloppText = post.beloppOre?.let { beloppOreTillFaltText(it) }.orEmpty(),
        rotRutAvdragText = post.rotRutAvdragOre?.let { beloppOreTillFaltText(it) }.orEmpty(),
        utforare = post.utforare.orEmpty(),
        anteckning = post.anteckning.orEmpty(),
    )

data class PlanFormularTillstand(
    val redigerarId: Long = 0L,
    val titel: String = "",
    val beloppText: String = "",
    val planeratArText: String = "",
    val anteckning: String = "",
    val valideringsFel: PlaneradKostnadFel? = null,
)

sealed class HusUiMeddelande {
    data object UppgiftMarkeradGjord : HusUiMeddelande()

    data object UppgiftMarkeradGjordPostEjSparad : HusUiMeddelande()

    data object HistorikPdfSparad : HusUiMeddelande()

    data object HistorikPdfMisslyckades : HusUiMeddelande()

    data object HistorikCsvSparad : HusUiMeddelande()

    data object HistorikCsvMisslyckades : HusUiMeddelande()

    data object AvlasningCsvSparad : HusUiMeddelande()

    data object AvlasningCsvMisslyckades : HusUiMeddelande()
    
    data object SakerhetskopiaSparad : HusUiMeddelande()

    data object SakerhetskopiaKundeInteLasas : HusUiMeddelande()

    data object SakerhetskopiaNyareVersion : HusUiMeddelande()

    data class SakerhetskopiaImporterad(val tillagda: Int, val hoppadeOver: Int) : HusUiMeddelande()

    data object DokumentForStort : HusUiMeddelande()

    data object DokumentOgiltigTyp : HusUiMeddelande()

    data object DokumentKundeInteLaggasTill : HusUiMeddelande()
}

class HusViewModel(
    private val repository: HusRepository,
    private val paminnelseInstallningar: PaminnelseInstallningarLager,
    private val installningarLager: InstallningarLager,
    private val appContext: Context,
    private val proKalla: ProKalla,
    /** Dagens datum. Läses varje gång, så att appen inte fastnar på dagen den startade. */
    private val klocka: () -> LocalDate = { LocalDate.now() },
) : ViewModel() {
    private val idag: LocalDate get() = klocka()

    val arPro: StateFlow<Boolean> = proKalla.arPro

    private val _proDialogSynlig = MutableStateFlow(false)
    val proDialogSynlig: StateFlow<Boolean> = _proDialogSynlig.asStateFlow()

    private val _proDialogVisaBostadsGrans = MutableStateFlow(false)
    val proDialogVisaBostadsGrans: StateFlow<Boolean> = _proDialogVisaBostadsGrans.asStateFlow()

    private val _visaValkommen = MutableStateFlow(false)
    val visaValkommen: StateFlow<Boolean> = _visaValkommen.asStateFlow()

    init {
        viewModelScope.launch {
            proKalla.kontrollera()
        }
        viewModelScope.launch {
            synkaValdBostad()
        }
        viewModelScope.launch {
            if (!installningarLager.valkommenVisad.first()) {
                if (repository.harData()) {
                    installningarLager.sattValkommenVisad()
                } else {
                    _visaValkommen.value = true
                }
            }
        }
    }

    private suspend fun synkaValdBostad() {
        val sparad = installningarLager.valdBostadId.first()
        val bostader = repository.allaBostader()
        if (bostader.isEmpty()) return
        val giltig = sparad != null && bostader.any { it.id == sparad }
        if (!giltig) {
            installningarLager.sattValdBostadId(bostader.first().id)
        }
    }

    fun avslutaValkommen() {
        _visaValkommen.value = false
        viewModelScope.launch { installningarLager.sattValkommenVisad() }
    }

    val temaLage: StateFlow<TemaLage> =
        installningarLager.temaLage.map { TemaLage.franNamn(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TemaLage.SYSTEM,
        )

    fun sattTemaLage(lage: TemaLage) {
        viewModelScope.launch { installningarLager.sattTemaLage(lage.name) }
    }
    val bostader: StateFlow<List<Bostad>> =
        repository.bostader.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val valdBostadId: StateFlow<Long?> =
        installningarLager.valdBostadId.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val valdBostad: StateFlow<Bostad?> =
        combine(bostader, valdBostadId) { lista, id ->
            id?.let { sokt -> lista.firstOrNull { it.id == sokt } } ?: lista.firstOrNull()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val uppgifter: StateFlow<List<Uppgift>> =
        valdBostadId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.uppgifterForBostad(id)
            }
            .map { sorteraUppgifter(it, idag) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Widgeten på hemskärmen visar de närmaste uppgifterna; rita om den när de ändras.
        viewModelScope.launch {
            uppgifter.collect {
                withContext(Dispatchers.IO) {
                    runCatching { se.tmconnect.husloggen.widget.KommandeWidget.uppdateraAlla(appContext) }
                }
            }
        }
    }

    val poster: StateFlow<List<Post>> =
        valdBostadId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.posterForBostad(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val matare: StateFlow<List<Matare>> =
        valdBostadId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.matareForBostad(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dokument: StateFlow<List<Dokument>> =
        valdBostadId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.dokumentForBostad(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val planeradeKostnader: StateFlow<List<PlaneradKostnad>> =
        valdBostadId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.planeradeKostnaderForBostad(id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _forsaljningsAr = MutableStateFlow(standardForsaljningsar(idag))
    val forsaljningsAr: StateFlow<Int> = _forsaljningsAr.asStateFlow()

    fun sattForsaljningsAr(ar: Int) {
        _forsaljningsAr.value = ar
    }

    /** Om globala dagliga påminnelser för uppgifter är på. */
    val paminnelserPa: StateFlow<Boolean> = paminnelseInstallningar.paminnelserPa
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val autoBackup: StateFlow<AutoBackupTillstand> =
        combine(
            installningarLager.autoBackupMapp,
            installningarLager.autoBackupMappNamn,
            installningarLager.autoBackupIntervallDagar,
            installningarLager.autoBackupSenaste,
            installningarLager.autoBackupFel,
        ) { mapp, namn, intervall, senaste, fel ->
            AutoBackupTillstand(
                pa = mapp != null,
                mappNamn = namn,
                intervallDagar = intervall,
                senaste = senaste,
                fel = fel,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AutoBackupTillstand())

    private val _formular = MutableStateFlow<UppgiftFormularTillstand?>(null)
    val formular: StateFlow<UppgiftFormularTillstand?> = _formular.asStateFlow()

    private val _postFormular = MutableStateFlow<PostFormularTillstand?>(null)
    val postFormular: StateFlow<PostFormularTillstand?> = _postFormular.asStateFlow()

    private val _postDetaljId = MutableStateFlow<Long?>(null)
    val postDetaljId: StateFlow<Long?> = _postDetaljId.asStateFlow()

    private val _matareDetaljId = MutableStateFlow<Long?>(null)
    val matareDetaljId: StateFlow<Long?> = _matareDetaljId.asStateFlow()

    private val _dokumentDetaljId = MutableStateFlow<Long?>(null)
    val dokumentDetaljId: StateFlow<Long?> = _dokumentDetaljId.asStateFlow()

    private val _dokumentFormular = MutableStateFlow<DokumentFormularTillstand?>(null)
    val dokumentFormular: StateFlow<DokumentFormularTillstand?> = _dokumentFormular.asStateFlow()

    private val _matareFormular = MutableStateFlow<MatareFormularTillstand?>(null)
    val matareFormular: StateFlow<MatareFormularTillstand?> = _matareFormular.asStateFlow()

    private val _avlasningFormular = MutableStateFlow<AvlasningFormularTillstand?>(null)
    val avlasningFormular: StateFlow<AvlasningFormularTillstand?> = _avlasningFormular.asStateFlow()

    private val _planFormular = MutableStateFlow<PlanFormularTillstand?>(null)
    val planFormular: StateFlow<PlanFormularTillstand?> = _planFormular.asStateFlow()

    val valdMatareAvlasningar: StateFlow<List<Avlasning>> =
        _matareDetaljId
            .flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.avlasningarForMatare(id)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
            val bostadId = valdBostadId.value ?: repository.hamtaForvaldBostadId()
            val uppgift = Uppgift(
                id = tillstand.redigerarId,
                bostadId = befintlig?.bostadId ?: bostadId,
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
            when (
                repository.markeraSomGjord(uppgift, idag, proKalla.arPro.value)
            ) {
                MarkeraGjordResult.MedPost ->
                    _meddelanden.emit(HusUiMeddelande.UppgiftMarkeradGjord)
                MarkeraGjordResult.UtanPostGransNadd ->
                    _meddelanden.emit(HusUiMeddelande.UppgiftMarkeradGjordPostEjSparad)
            }
        }
    }

    /**
     * Slår på eller av dagliga påminnelser och synkar WorkManager-jobbet.
     * Notisbehörighet ska vara hanterad i UI innan [pa] sätts till true.
     */
    fun sattPaminnelser(pa: Boolean) {
        viewModelScope.launch {
            paminnelseInstallningar.sattPaminnelserPa(pa)
            PaminnelsePlanerare.synka<PaminnelseWorker>(
                appContext,
                pa,
                HusPaminnelseKalla.UNIKT_JOBB_NAMN,
            )
        }
    }

    /** Schemalägger påminnelsejobb vid appstart om inställningen är på. */
    fun synkaPaminnelseJobb() {
        viewModelScope.launch {
            val pa = paminnelseInstallningar.paminnelserPa.first()
            PaminnelsePlanerare.synka<PaminnelseWorker>(
                appContext,
                pa,
                HusPaminnelseKalla.UNIKT_JOBB_NAMN,
            )
        }
    }

    private val totaltAntalPoster: StateFlow<Int> =
        repository.totaltAntalPosterFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun kanLaggaTillNyPost(): Boolean =
        kanLaggaTillPost(totaltAntalPoster.value, proKalla.arPro.value)

    suspend fun kanLaggaTillPostNu(): Boolean =
        kanLaggaTillPost(repository.antalPoster(), proKalla.arPro.value)

    fun visaProDialog(visaBostadsGrans: Boolean = false) {
        _proDialogVisaBostadsGrans.value = visaBostadsGrans
        _proDialogSynlig.value = true
    }

    fun stangProDialog() {
        _proDialogSynlig.value = false
        _proDialogVisaBostadsGrans.value = false
    }

    fun valjBostad(bostadId: Long) {
        viewModelScope.launch { installningarLager.sattValdBostadId(bostadId) }
    }

    fun kanLaggaTillNyBostad(): Boolean =
        kanLaggaTillBostad(bostader.value.size, proKalla.arPro.value)

    fun sparaNyBostad(namn: String, adress: String, anteckning: String, onResultat: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (
                val resultat =
                    repository.sparaBostad(
                        Bostad(
                            namn = namn,
                            adress = adress.ifBlank { null },
                            anteckning = anteckning.ifBlank { null },
                        ),
                        proKalla.arPro.value,
                    )
            ) {
                is SparaBostadResult.Sparad -> {
                    installningarLager.sattValdBostadId(resultat.id)
                    onResultat(true)
                }
                SparaBostadResult.GransBostaderUtanPro -> {
                    visaProDialog(visaBostadsGrans = true)
                    onResultat(false)
                }
            }
        }
    }

    fun uppdateraBostad(bostad: Bostad) {
        viewModelScope.launch {
            repository.sparaBostad(bostad, proKalla.arPro.value)
        }
    }

    fun taBortBostad(bostad: Bostad, onSista: () -> Unit, onBorttagen: () -> Unit) {
        viewModelScope.launch {
            when (repository.taBortBostad(bostad)) {
                TaBortBostadResult.SistaBostaden -> onSista()
                TaBortBostadResult.Borttagen -> {
                    synkaValdBostad()
                    onBorttagen()
                }
            }
        }
    }

    suspend fun bostadRaderingInfo(bostadId: Long) = repository.bostadRaderingInfo(bostadId)

    fun kopPro(activity: Activity) {
        proKalla.kop(activity)
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
                rotRutAvdragText = post.rotRutAvdragOre?.let { beloppOreTillFaltText(it) }.orEmpty(),
                utforare = post.utforare.orEmpty(),
                uppgiftId = post.uppgiftId,
                anteckning = post.anteckning.orEmpty(),
                bildFilnamn = bilder,
            )
        }
    }

    /** Ny post med samma innehåll som en befintlig, men dagens datum och utan bilder. */
    fun visaKopieraPost(postId: Long) {
        viewModelScope.launch {
            val post = repository.hamtaPost(postId) ?: return@launch
            _postDetaljId.value = null
            nollstallBildSession(emptyList())
            _postFormular.value = kopieraPostTillFormular(post, idag)
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

    fun uppdateraPostRotRutAvdrag(text: String) {
        uppdateraPostFormular { it.copy(rotRutAvdragText = text, valideringsFel = null) }
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
        val fel = valideraPost(
            tillstand.vad,
            tillstand.datum,
            idag,
            tillstand.beloppText,
            tillstand.rotRutAvdragText,
        )
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
            val rotRutAvdragOre = tillstand.rotRutAvdragText.trim().takeIf { it.isNotEmpty() }
                ?.let { tolkaBeloppTillOre(it) }
            val gamlaBilder = befintlig?.let { repository.lasBilder(it) }.orEmpty()
            val nyaBilder = tillstand.bildFilnamn
            gamlaBilder.filter { it !in nyaBilder }.forEach { repository.taBortBildfil(it) }

            val bostadId = valdBostadId.value ?: befintlig?.bostadId ?: repository.hamtaForvaldBostadId()
            val post = Post(
                id = tillstand.redigerarId,
                bostadId = befintlig?.bostadId ?: bostadId,
                datum = tillstand.datum,
                vad = tillstand.vad.trim(),
                typ = tillstand.typ,
                beloppOre = beloppOre,
                rotRutAvdragOre = rotRutAvdragOre,
                utforare = tillstand.utforare.trim().ifEmpty { null },
                anteckning = tillstand.anteckning.trim().ifEmpty { null },
                uppgiftId = tillstand.uppgiftId,
                kvittoFil = befintlig?.kvittoFil,
                bilder = formateraBilder(nyaBilder),
                skapad = befintlig?.skapad ?: System.currentTimeMillis(),
            )
            when (repository.sparaPost(post, proKalla.arPro.value)) {
                SparaPostResult.GransNadd -> return@launch
                is SparaPostResult.Sparad -> {
                    tillstand.planeradKostnadIdEfterSparad?.let { planId ->
                        repository.hamtaPlaneradKostnad(planId)?.let { repository.taBortPlaneradKostnad(it) }
                    }
                    bilderTillagdaISession = emptySet()
                    _postFormular.value = null
                }
            }
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

    fun valtAutoBackupMapp(uri: Uri, namn: String?) {
        viewModelScope.launch {
            val tidigare = installningarLager.autoBackupMapp.first()
            installningarLager.sattAutoBackupMapp(uri.toString(), namn)
            if (tidigare == null) {
                SakerhetskopiaPlanerare.korNu(appContext)
            }
            synkaAutoBackupJobb()
        }
    }

    fun stangAvAutoBackup() {
        viewModelScope.launch {
            installningarLager.sattAutoBackupMapp(null, null)
            synkaAutoBackupJobb()
        }
    }

    fun sattAutoBackupIntervall(dagar: Int) {
        viewModelScope.launch { installningarLager.sattAutoBackupIntervall(dagar) }
    }

    fun korNuAutoBackup() {
        SakerhetskopiaPlanerare.korNu(appContext)
    }

    fun synkaAutoBackupJobb() {
        viewModelScope.launch {
            SakerhetskopiaPlanerare.synka(
                appContext,
                installningarLager.autoBackupMapp.first() != null,
            )
        }
    }

    private fun rensaOsparadeBilder() {
        val tillstand = _postFormular.value ?: return
        // Endast radera bilder i nyskapa-läge (redigerarId == 0)
        if (tillstand.redigerarId == 0L) {
            val nu = tillstand.bildFilnamn.toSet()
            val skaBehalla = bilderVidOppning
            (nu - skaBehalla).forEach { repository.taBortBildfil(it) }
        }
        bilderTillagdaISession = emptySet()
    }
    
    /**
     * Sparar säkerhetskopia till angiven URI
     */
    fun sparaSakerhetskopia(uri: Uri) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val bostaderLista = repository.allaBostader()
                    val uppgifter = repository.allaUppgifter()
                    val poster = repository.allaPoster()
                    val matare = repository.allaMatare()
                    val avlasningar = repository.allaAvlasningar()
                    val dokumentLista = repository.allaDokument()
                    val planLista = repository.allaPlaneradeKostnader()
                    val bildLager = skapaHusBildLager(appContext)
                    val dokumentLager = skapaHusDokumentLager(appContext)
                    val outputStream = appContext.contentResolver.openOutputStream(uri)
                        ?: throw java.io.IOException()
                    outputStream.use {
                        Sakerhetskopia.skriv(
                            it,
                            bostaderLista,
                            uppgifter,
                            poster,
                            matare,
                            avlasningar,
                            dokumentLista,
                            planLista,
                            bildLager,
                            dokumentLager,
                        )
                    }
                }
                _meddelanden.emit(HusUiMeddelande.SakerhetskopiaSparad)
            } catch (_: Exception) {
                _meddelanden.emit(HusUiMeddelande.SakerhetskopiaKundeInteLasas)
            }
        }
    }

    fun lasInSakerhetskopia(uri: Uri) {
        viewModelScope.launch {
            try {
                val resultat = withContext(Dispatchers.IO) {
                    val inputStream = appContext.contentResolver.openInputStream(uri)
                        ?: throw java.io.IOException()
                    inputStream.use { stream ->
                        Sakerhetskopia.lasIn(stream)
                    }
                }
                when (resultat) {
                    is SakerhetskopiaLasResult.Ok -> importeraSakerhetskopia(resultat)
                    SakerhetskopiaLasResult.Fel.OkandVersion -> {
                        _meddelanden.emit(HusUiMeddelande.SakerhetskopiaNyareVersion)
                    }
                    SakerhetskopiaLasResult.Fel.TrasigFil,
                    SakerhetskopiaLasResult.Fel.SaknarLoggJson -> {
                        _meddelanden.emit(HusUiMeddelande.SakerhetskopiaKundeInteLasas)
                    }
                }
            } catch (_: Exception) {
                _meddelanden.emit(HusUiMeddelande.SakerhetskopiaKundeInteLasas)
            }
        }
    }

    fun sparaHusHistorikPdf(
        uri: Uri,
        texter: HusHistorikTexter,
        locale: Locale,
        typEtikett: (PostTyp) -> String,
        forbattringEtikett: (ForbattringMojlighet) -> String,
        datumEtikett: (Post) -> String,
        beloppEtikett: (Post) -> String,
    ) {
        viewModelScope.launch {
            try {
                val data = byggHusHistorik(
                    poster = poster.value,
                    forsaljningsar = _forsaljningsAr.value,
                    localeLand = locale.country,
                    typEtikett = typEtikett,
                    datumEtikett = datumEtikett,
                    beloppEtikett = beloppEtikett,
                    forbattringEtikett = forbattringEtikett,
                    gransJa = texter.gransJa,
                    gransNej = texter.gransNej,
                )
                val ok = withContext(Dispatchers.IO) {
                    val stream = appContext.contentResolver.openOutputStream(uri)
                        ?: return@withContext false
                    stream.use {
                        skrivHusHistorikTillPdf(data, texter, locale, it) is PdfExportResult.Ok
                    }
                }
                _meddelanden.emit(
                    if (ok) HusUiMeddelande.HistorikPdfSparad else HusUiMeddelande.HistorikPdfMisslyckades,
                )
            } catch (_: Exception) {
                _meddelanden.emit(HusUiMeddelande.HistorikPdfMisslyckades)
            }
        }
    }

    fun sparaHusHistorikCsv(
        uri: Uri,
        texter: HusHistorikTexter,
        locale: Locale,
        typEtikett: (PostTyp) -> String,
        forbattringEtikett: (ForbattringMojlighet) -> String,
        datumEtikett: (Post) -> String,
        beloppEtikett: (Post) -> String,
    ) {
        viewModelScope.launch {
            try {
                val data = byggHusHistorik(
                    poster = poster.value,
                    forsaljningsar = _forsaljningsAr.value,
                    localeLand = locale.country,
                    typEtikett = typEtikett,
                    datumEtikett = datumEtikett,
                    beloppEtikett = beloppEtikett,
                    forbattringEtikett = forbattringEtikett,
                    gransJa = texter.gransJa,
                    gransNej = texter.gransNej,
                )
                val csv = husHistorikCsv(data, texter)
                withContext(Dispatchers.IO) {
                    val stream = appContext.contentResolver.openOutputStream(uri)
                        ?: throw java.io.IOException()
                    stream.use { skrivCsvMedBom(it, csv) }
                }
                _meddelanden.emit(HusUiMeddelande.HistorikCsvSparad)
            } catch (_: Exception) {
                _meddelanden.emit(HusUiMeddelande.HistorikCsvMisslyckades)
            }
        }
    }

    fun sparaKommandeUnderhallCsv(
        uri: Uri,
        texter: KommandeUnderhallCsvTexter,
        locale: Locale,
    ) {
        viewModelScope.launch {
            try {
                val grupper =
                    kommandeUnderhall(
                        uppgifter = uppgifter.value,
                        loggposter = poster.value,
                        idag = LocalDate.now(),
                    )
                val csv =
                    kommandeUnderhallCsv(
                        grupper = grupper,
                        texter = texter,
                        datumEtikett = { datum -> formateraDatum(datum, locale) },
                        kostnadEtikett = { ore -> formateraPrisKr(ore, locale) },
                    )
                withContext(Dispatchers.IO) {
                    val stream = appContext.contentResolver.openOutputStream(uri)
                        ?: throw java.io.IOException()
                    stream.use { skrivCsvMedBom(it, csv) }
                }
                _meddelanden.emit(HusUiMeddelande.HistorikCsvSparad)
            } catch (_: Exception) {
                _meddelanden.emit(HusUiMeddelande.HistorikCsvMisslyckades)
            }
        }
    }

    fun visaMatareDetalj(matareId: Long) {
        _matareFormular.value = null
        _avlasningFormular.value = null
        _matareDetaljId.value = matareId
    }

    fun stangMatareDetalj() {
        _matareDetaljId.value = null
    }

    fun visaNyMatare() {
        _matareDetaljId.value = null
        _matareFormular.value = MatareFormularTillstand()
    }

    fun stangMatareFormular() {
        _matareFormular.value = null
    }

    fun uppdateraMatareNamn(namn: String) {
        uppdateraMatareFormular { it.copy(namn = namn, valideringsFel = null) }
    }

    fun uppdateraMatareEnhet(enhet: String) {
        uppdateraMatareFormular { it.copy(enhet = enhet, valideringsFel = null) }
    }

    fun uppdateraMatareAnteckning(text: String) {
        uppdateraMatareFormular { it.copy(anteckning = text) }
    }

    fun uppdateraMatarePrisOrePerEnhet(text: String) {
        uppdateraMatareFormular { it.copy(prisOrePerEnhetText = text, valideringsFel = null) }
    }

    fun visaAndraMatare(matareId: Long) {
        viewModelScope.launch {
            val rad = repository.hamtaMatare(matareId) ?: return@launch
            _matareDetaljId.value = null
            _matareFormular.value =
                MatareFormularTillstand(
                    redigerarId = rad.id,
                    namn = rad.namn,
                    enhet = rad.enhet,
                    prisOrePerEnhetText = rad.prisOrePerEnhet?.toString().orEmpty(),
                    anteckning = rad.anteckning.orEmpty(),
                )
        }
    }

    fun appliceraMatareForslag(namn: String, enhet: String) {
        uppdateraMatareFormular {
            it.copy(namn = namn, enhet = enhet, valideringsFel = null)
        }
    }

    fun sparaMatareFormular() {
        val tillstand = _matareFormular.value ?: return
        val fel = valideraMatare(tillstand.namn, tillstand.enhet)
        if (fel != null) {
            _matareFormular.value = tillstand.copy(valideringsFel = fel)
            return
        }
        val prisOgiltigt =
            tillstand.prisOrePerEnhetText.trim().isNotEmpty() &&
                tolkaValfrittOreHeltal(tillstand.prisOrePerEnhetText) == null
        if (prisOgiltigt) {
            _matareFormular.value = tillstand.copy(valideringsFel = MatareFel.PrisOgiltigt)
            return
        }
        viewModelScope.launch {
            val befintlig =
                if (tillstand.redigerarId != 0L) repository.hamtaMatare(tillstand.redigerarId) else null
            val bostadId = valdBostadId.value ?: befintlig?.bostadId ?: repository.hamtaForvaldBostadId()
            repository.sparaMatare(
                Matare(
                    id = tillstand.redigerarId,
                    bostadId = befintlig?.bostadId ?: bostadId,
                    namn = tillstand.namn.trim(),
                    enhet = tillstand.enhet.trim(),
                    anteckning = tillstand.anteckning.trim().ifEmpty { null },
                    prisOrePerEnhet = tolkaValfrittOreHeltal(tillstand.prisOrePerEnhetText),
                    skapad = befintlig?.skapad ?: System.currentTimeMillis(),
                ),
            )
            _matareFormular.value = null
        }
    }

    fun taBortMatareFormular() {
        val tillstand = _matareFormular.value ?: return
        if (tillstand.redigerarId == 0L) return
        viewModelScope.launch {
            repository.hamtaMatare(tillstand.redigerarId)?.let { repository.taBortMatare(it) }
            _matareFormular.value = null
            stangMatareDetalj()
        }
    }

    fun taBortMatare(matareId: Long) {
        viewModelScope.launch {
            repository.hamtaMatare(matareId)?.let { repository.taBortMatare(it) }
            stangMatareDetalj()
        }
    }

    fun visaNyAvlasning(matareId: Long) {
        _avlasningFormular.value =
            AvlasningFormularTillstand(matareId = matareId, datum = idag)
    }

    fun stangAvlasningFormular() {
        _avlasningFormular.value = null
    }

    fun uppdateraAvlasningDatum(datum: LocalDate) {
        uppdateraAvlasningFormular { it.copy(datum = datum) }
    }

    fun uppdateraAvlasningVarde(text: String) {
        uppdateraAvlasningFormular { it.copy(vardeText = text, valideringsFel = null) }
    }

    fun avbrytLagreVardeBekraftelse() {
        uppdateraAvlasningFormular { it.copy(visarLagreVardeBekraftelse = false) }
    }

    fun sparaAvlasningFormular(bekraftatLagreVarde: Boolean = false) {
        val tillstand = _avlasningFormular.value ?: return
        viewModelScope.launch {
            val befintliga = repository.avlasningarForMatareLista(tillstand.matareId)
            val fel =
                valideraAvlasning(
                    tillstand.vardeText,
                    tillstand.datum,
                    befintliga,
                    tillstand.redigerarId,
                )
            if (fel != null) {
                _avlasningFormular.value = tillstand.copy(valideringsFel = fel)
                return@launch
            }
            val varde = tolkaMatareVardeTillTusendel(tillstand.vardeText)!!
            val foregaende =
                foregaendeAvlasning(tillstand.datum, befintliga, tillstand.redigerarId)
            if (!bekraftatLagreVarde &&
                foregaende != null &&
                varde < foregaende.varde
            ) {
                _avlasningFormular.value = tillstand.copy(visarLagreVardeBekraftelse = true)
                return@launch
            }
            val befintlig =
                if (tillstand.redigerarId != 0L) {
                    befintliga.find { it.id == tillstand.redigerarId }
                } else {
                    null
                }
            repository.sparaAvlasning(
                Avlasning(
                    id = tillstand.redigerarId,
                    matareId = tillstand.matareId,
                    datum = tillstand.datum,
                    varde = varde,
                    skapad = befintlig?.skapad ?: System.currentTimeMillis(),
                ),
            )
            _avlasningFormular.value = null
        }
    }

    fun sparaAvlasningCsv(
        uri: Uri,
        matareId: Long,
        texter: AvlasningCsvTexter,
        locale: Locale,
    ) {
        viewModelScope.launch {
            try {
                val matare = repository.hamtaMatare(matareId) ?: return@launch
                val avlasningar = repository.avlasningarForMatareLista(matareId)
                val rader = beraknaAvlasningarMedForbrukning(avlasningar)
                val csv =
                    avlasningCsv(
                        matareNamn = matare.namn,
                        enhet = matare.enhet,
                        rader = rader,
                        texter = texter,
                        datumEtikett = { rad ->
                            se.tmconnect.karna.format.formateraDatum(rad.avlasning.datum, locale)
                        },
                    )
                withContext(Dispatchers.IO) {
                    val stream = appContext.contentResolver.openOutputStream(uri)
                        ?: throw java.io.IOException()
                    stream.use { skrivCsvMedBom(it, csv) }
                }
                _meddelanden.emit(HusUiMeddelande.AvlasningCsvSparad)
            } catch (_: Exception) {
                _meddelanden.emit(HusUiMeddelande.AvlasningCsvMisslyckades)
            }
        }
    }

    private fun uppdateraMatareFormular(block: (MatareFormularTillstand) -> MatareFormularTillstand) {
        _matareFormular.value = _matareFormular.value?.let(block)
    }

    fun visaNyttDokument() {
        _dokumentDetaljId.value = null
        _dokumentFormular.value = DokumentFormularTillstand()
    }

    fun stangDokumentFormular() {
        val tillstand = _dokumentFormular.value
        if (tillstand?.redigerarId == 0L) {
            tillstand.filnamn?.let { repository.taBortDokumentfil(it) }
        }
        _dokumentFormular.value = null
    }

    fun visaDokumentDetalj(dokumentId: Long) {
        _dokumentFormular.value = null
        _dokumentDetaljId.value = dokumentId
    }

    fun stangDokumentDetalj() {
        _dokumentDetaljId.value = null
    }

    fun visaAndraDokument(dokumentId: Long) {
        viewModelScope.launch {
            val rad = repository.hamtaDokument(dokumentId) ?: return@launch
            _dokumentDetaljId.value = null
            _dokumentFormular.value =
                DokumentFormularTillstand(
                    redigerarId = rad.id,
                    titel = rad.titel,
                    kategori = rad.kategori,
                    anteckning = rad.anteckning.orEmpty(),
                    filnamn = rad.filnamn,
                    mimeTyp = rad.mimeTyp,
                )
        }
    }

    fun uppdateraDokumentTitel(titel: String) {
        uppdateraDokumentFormular { it.copy(titel = titel, valideringsFel = null) }
    }

    fun uppdateraDokumentKategori(kategori: DokumentKategori?) {
        uppdateraDokumentFormular { it.copy(kategori = kategori) }
    }

    fun uppdateraDokumentAnteckning(text: String) {
        uppdateraDokumentFormular { it.copy(anteckning = text) }
    }

    fun valjDokumentFil(uri: Uri) {
        viewModelScope.launch {
            val tillstand = _dokumentFormular.value ?: return@launch
            if (tillstand.redigerarId != 0L) {
                return@launch
            }
            tillstand.filnamn?.let { repository.taBortDokumentfil(it) }
            when (val resultat = withContext(Dispatchers.IO) { repository.kopieraDokumentFranUri(uri) }) {
                is KopieraDokumentResult.Ok ->
                    _dokumentFormular.value =
                        tillstand.copy(
                            filnamn = resultat.filnamn,
                            mimeTyp = resultat.mimeTyp,
                            valideringsFel = null,
                        )
                KopieraDokumentResult.ForStor -> {
                    _meddelanden.emit(HusUiMeddelande.DokumentForStort)
                }
                KopieraDokumentResult.OgiltigTyp -> {
                    _meddelanden.emit(HusUiMeddelande.DokumentOgiltigTyp)
                }
                KopieraDokumentResult.Misslyckades -> {
                    _meddelanden.emit(HusUiMeddelande.DokumentKundeInteLaggasTill)
                }
            }
        }
    }

    fun sparaDokumentFormular() {
        val tillstand = _dokumentFormular.value ?: return
        val fel = valideraDokument(tillstand.titel, tillstand.filnamn != null)
        if (fel != null) {
            _dokumentFormular.value = tillstand.copy(valideringsFel = fel)
            return
        }
        viewModelScope.launch {
            if (tillstand.redigerarId == 0L) {
                val bostadId = valdBostadId.value ?: repository.hamtaForvaldBostadId()
                repository.sparaDokument(
                    Dokument(
                        bostadId = bostadId,
                        titel = tillstand.titel.trim(),
                        kategori = tillstand.kategori,
                        anteckning = tillstand.anteckning.trim().takeIf { it.isNotEmpty() },
                        filnamn = tillstand.filnamn!!,
                        mimeTyp = tillstand.mimeTyp!!,
                        tillagd = idag,
                    ),
                )
            } else {
                val befintlig = repository.hamtaDokument(tillstand.redigerarId) ?: return@launch
                repository.sparaDokument(
                    befintlig.copy(
                        titel = tillstand.titel.trim(),
                        kategori = tillstand.kategori,
                        anteckning = tillstand.anteckning.trim().takeIf { it.isNotEmpty() },
                    ),
                )
            }
            _dokumentFormular.value = null
        }
    }

    fun taBortDokument(dokumentId: Long) {
        viewModelScope.launch {
            repository.hamtaDokument(dokumentId)?.let { repository.taBortDokument(it) }
            stangDokumentDetalj()
        }
    }

    fun dokumentUri(filnamn: String): Uri = repository.dokumentUri(filnamn)

    private fun uppdateraDokumentFormular(block: (DokumentFormularTillstand) -> DokumentFormularTillstand) {
        _dokumentFormular.value = _dokumentFormular.value?.let(block)
    }

    fun visaNyPlaneradKostnad() {
        _planFormular.value =
            PlanFormularTillstand(planeratArText = idag.year.toString())
    }

    fun stangPlanFormular() {
        _planFormular.value = null
    }

    fun visaAndraPlaneradKostnad(id: Long) {
        viewModelScope.launch {
            val plan = repository.hamtaPlaneradKostnad(id) ?: return@launch
            _planFormular.value =
                PlanFormularTillstand(
                    redigerarId = plan.id,
                    titel = plan.titel,
                    beloppText = beloppOreTillFaltText(plan.beloppOre),
                    planeratArText = plan.planeratAr.toString(),
                    anteckning = plan.anteckning.orEmpty(),
                )
        }
    }

    fun genomforPlaneradKostnad(id: Long) {
        viewModelScope.launch {
            val plan = repository.hamtaPlaneradKostnad(id) ?: return@launch
            nollstallBildSession(emptyList())
            _postDetaljId.value = null
            _postFormular.value =
                PostFormularTillstand(
                    vad = plan.titel,
                    datum = idag,
                    typ = PostTyp.REPARATION,
                    beloppText = beloppOreTillFaltText(plan.beloppOre),
                    anteckning = plan.anteckning.orEmpty(),
                    planeradKostnadIdEfterSparad = plan.id,
                )
        }
    }

    fun uppdateraPlanTitel(text: String) {
        uppdateraPlanFormular { it.copy(titel = text, valideringsFel = null) }
    }

    fun uppdateraPlanBelopp(text: String) {
        uppdateraPlanFormular { it.copy(beloppText = text, valideringsFel = null) }
    }

    fun uppdateraPlaneratAr(text: String) {
        uppdateraPlanFormular { it.copy(planeratArText = text, valideringsFel = null) }
    }

    fun uppdateraPlanAnteckning(text: String) {
        uppdateraPlanFormular { it.copy(anteckning = text) }
    }

    fun sparaPlanFormular() {
        val tillstand = _planFormular.value ?: return
        val beloppOre = tillstand.beloppText.trim().takeIf { it.isNotEmpty() }?.let { tolkaBeloppTillOre(it) }
        val planeratAr = tillstand.planeratArText.trim().toIntOrNull()
        val fel = valideraPlaneradKostnad(tillstand.titel, beloppOre, planeratAr, idag.year)
        if (fel != null) {
            _planFormular.value = tillstand.copy(valideringsFel = fel)
            return
        }
        viewModelScope.launch {
            val bostadId = valdBostadId.value ?: repository.hamtaForvaldBostadId()
            repository.sparaPlaneradKostnad(
                PlaneradKostnad(
                    id = tillstand.redigerarId,
                    bostadId = repository.hamtaPlaneradKostnad(tillstand.redigerarId)?.bostadId ?: bostadId,
                    titel = tillstand.titel.trim(),
                    beloppOre = beloppOre!!,
                    planeratAr = planeratAr!!,
                    anteckning = tillstand.anteckning.trim().ifEmpty { null },
                ),
            )
            _planFormular.value = null
        }
    }

    fun taBortPlanFormular() {
        val tillstand = _planFormular.value ?: return
        if (tillstand.redigerarId == 0L) return
        viewModelScope.launch {
            repository.hamtaPlaneradKostnad(tillstand.redigerarId)?.let { repository.taBortPlaneradKostnad(it) }
            _planFormular.value = null
        }
    }

    private fun uppdateraPlanFormular(block: (PlanFormularTillstand) -> PlanFormularTillstand) {
        _planFormular.value = _planFormular.value?.let(block)
    }

    private fun uppdateraAvlasningFormular(block: (AvlasningFormularTillstand) -> AvlasningFormularTillstand) {
        _avlasningFormular.value = _avlasningFormular.value?.let(block)
    }

    private suspend fun importeraSakerhetskopia(resultat: SakerhetskopiaLasResult.Ok) {
        try {
            val importResultat = withContext(Dispatchers.IO) {
                repository.importeraSakerhetskopia(
                    resultat.innehall,
                    resultat.bildFiler,
                    resultat.dokumentFiler,
                )
            }
            if (paminnelseInstallningar.paminnelserPa.first()) {
                PaminnelsePlanerare.synka<PaminnelseWorker>(
                    appContext,
                    true,
                    HusPaminnelseKalla.UNIKT_JOBB_NAMN,
                )
            }
            _meddelanden.emit(
                HusUiMeddelande.SakerhetskopiaImporterad(
                    tillagda = importResultat.tillagda,
                    hoppadeOver = importResultat.hoppadeOver,
                ),
            )
        } catch (_: Exception) {
            _meddelanden.emit(HusUiMeddelande.SakerhetskopiaKundeInteLasas)
        }
    }
}

class HusViewModelFactory(
    private val repository: HusRepository,
    private val paminnelseInstallningar: PaminnelseInstallningarLager,
    private val installningarLager: InstallningarLager,
    private val appContext: Context,
    private val proKalla: ProKalla,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HusViewModel::class.java)) {
            return HusViewModel(
                repository,
                paminnelseInstallningar,
                installningarLager,
                appContext,
                proKalla,
            ) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
