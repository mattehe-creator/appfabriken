package se.tmconnect.flextid.ui

import se.tmconnect.flextid.regler.ManadsRad
import se.tmconnect.flextid.regler.saldoVidArssluten
import se.tmconnect.flextid.regler.arsOversikt
import se.tmconnect.flextid.regler.arsOversiktCsv
import se.tmconnect.flextid.regler.senasteArbetsdagFore
import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.flextid.data.AutoBackupInstallningarLager
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.FlextidRepository
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.export.AutoSakerhetskopiaPlanerare
import se.tmconnect.flextid.export.ManadsCsvTexter
import se.tmconnect.flextid.export.SakerhetskopiaJsonLasResult
import se.tmconnect.flextid.export.skapaFlextidSakerhetskopiaJson
import se.tmconnect.flextid.export.byggManadsCsv
import se.tmconnect.flextid.regler.ManadsRapportData
import se.tmconnect.flextid.regler.ManadsRapportTexter
import se.tmconnect.flextid.regler.byggManadsRapport
import se.tmconnect.flextid.export.lasSakerhetskopiaJson
import se.tmconnect.flextid.export.sakerhetskopiaDagTillDag
import se.tmconnect.flextid.export.sakerhetskopiaInstallningTillInstallning
import se.tmconnect.flextid.paminnelse.FlextidPaminnelseInstallningarLager
import se.tmconnect.flextid.paminnelse.FlextidPaminnelseKalla
import se.tmconnect.flextid.paminnelse.PaminnelseWorker
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.TidValideringFel
import se.tmconnect.flextid.regler.arbetadeMinuter
import se.tmconnect.flextid.regler.dagensUtfall
import se.tmconnect.flextid.regler.SEMESTERAR_STARTMANAD_STANDARD
import se.tmconnect.flextid.regler.SEMESTERDAGAR_STANDARD
import se.tmconnect.flextid.regler.semesterSammanfattning
import se.tmconnect.flextid.regler.periodSammanfattning
import se.tmconnect.flextid.regler.kanLaggaTillDagPost
import se.tmconnect.flextid.regler.ledighetsutrymme
import se.tmconnect.flextid.regler.mandagISammaVecka
import se.tmconnect.flextid.regler.sondagISammaVecka
import se.tmconnect.flextid.regler.tidigasteLedigDag
import se.tmconnect.flextid.regler.dagsForslagVanligDag
import se.tmconnect.flextid.regler.localTimeTillMinuter
import se.tmconnect.flextid.regler.minuterTillLocalTime
import se.tmconnect.flextid.regler.STANDARD_VANLIG_RAST_MIN
import se.tmconnect.flextid.regler.STANDARD_VANLIG_START_MIN
import se.tmconnect.flextid.regler.STANDARD_VANLIG_SLUT_MIN
import se.tmconnect.flextid.regler.tolkaTidText
import se.tmconnect.karna.export.skrivCsvMedBom
import se.tmconnect.karna.paminnelse.PaminnelsePlanerare
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.sakerhetskopia.AutoBackupTillstand
import se.tmconnect.karna.tema.TemaLage
import java.util.Locale

sealed interface FlextidMeddelande {
    data object Sparat : FlextidMeddelande

    data object Borttaget : FlextidMeddelande

    data object Exporterat : FlextidMeddelande

    data object Importerat : FlextidMeddelande

    data object SlutForeStart : FlextidMeddelande

    data object ImportFel : FlextidMeddelande

    data object GransUtanPro : FlextidMeddelande

    data class RedanInstamplad(val start: LocalTime) : FlextidMeddelande

    data object IngenInstempling : FlextidMeddelande
}

data class VeckaDagRad(
    val datum: LocalDate,
    val dag: Dag?,
    val arbetadeMin: Int,
    val utfallMin: Int,
)

data class ArsOversiktTillstand(
    val ar: Int,
    val rader: List<ManadsRad>,
    val prognosMin: Int?,
    val arArInnevarandeAr: Boolean,
)

data class DagFormularTillstand(
    val datum: LocalDate,
    val startText: String = "",
    val slutText: String = "",
    val rastText: String = "",
    val typ: DagTyp = DagTyp.ARBETE,
    val anteckning: String = "",
    val fel: TidValideringFel? = null,
    /** Tider från senaste arbetsdagen, för knappen "Samma tider som …". Bara för nya dagar. */
    val forslagDatum: LocalDate? = null,
    val forslagStart: String = "",
    val forslagSlut: String = "",
    val forslagRast: String = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
class FlextidViewModel(
    private val repository: FlextidRepository,
    private val uiInstallningarLager: UiInstallningarLager,
    private val paminnelseInstallningar: FlextidPaminnelseInstallningarLager,
    private val proKalla: ProKalla,
    private val appContext: Context,
) : ViewModel() {
    private val autoBackupInstallningar = AutoBackupInstallningarLager(appContext)
    private val _veckaAnkar = MutableStateFlow(mandagISammaVecka(LocalDate.now()))
    val veckaAnkar = _veckaAnkar.asStateFlow()

    private val _dagFormular = MutableStateFlow<DagFormularTillstand?>(null)
    val dagFormular = _dagFormular.asStateFlow()

    private val _meddelanden = MutableSharedFlow<FlextidMeddelande>(extraBufferCapacity = 1)
    val meddelanden = _meddelanden.asSharedFlow()

    private val _proDialogSynlig = MutableStateFlow(false)
    val proDialogSynlig = _proDialogSynlig.asStateFlow()

    private val _visaValkommen = MutableStateFlow(false)
    val visaValkommen = _visaValkommen.asStateFlow()

    val arPro = proKalla.arPro

    val stamplaUtPaminnelsePa =
        paminnelseInstallningar.stamplaUtPa.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            false,
        )

    val stamplaInPaminnelsePa =
        paminnelseInstallningar.stamplaInPa.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            false,
        )

    val autoBackup =
        combine(
            autoBackupInstallningar.autoBackupMapp,
            autoBackupInstallningar.autoBackupMappNamn,
            autoBackupInstallningar.autoBackupIntervallDagar,
            autoBackupInstallningar.autoBackupSenaste,
            autoBackupInstallningar.autoBackupFel,
        ) { mapp, namn, intervall, senaste, fel ->
            AutoBackupTillstand(
                pa = mapp != null,
                mappNamn = namn,
                intervallDagar = intervall,
                senaste = senaste,
                fel = fel,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AutoBackupTillstand())

    val stamplaInPaminnelseMinEfterMidnatt =
        paminnelseInstallningar.stamplaInPaminnelseMinEfterMidnatt.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            se.tmconnect.flextid.regler.STANDARD_STAMPLA_IN_PAMINNELSE_MIN_EFTER_MIDNATT,
        )

    val antalDagar =
        repository.observeraAntalDagar().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0,
        )

    val temaLage =
        uiInstallningarLager.temaLage.map { TemaLage.franNamn(it) }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TemaLage.SYSTEM,
        )

    val installning =
        repository.observeraInstallning().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            Installning(),
        )

    val saldo =
        repository.observeraSaldo(uiInstallningarLager.aftnarLediga).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0,
        )

    /** Dagens datum; byts när klockan passerar midnatt medan appen är öppen. */
    private val _idag = MutableStateFlow(LocalDate.now())

    val idagDag =
        _idag.flatMapLatest { repository.observeraDag(it) }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null,
        )

    val veckaRader =
        combine(_veckaAnkar, repository.observeraAllaDagar(), installning, uiInstallningarLager.aftnarLediga) { ankar, alla, inst, aftnar ->
            val slut = sondagISammaVecka(ankar)
            val map = alla.filter { !it.datum.isBefore(ankar) && !it.datum.isAfter(slut) }.associateBy { it.datum }
            (0L..6L).map { offset ->
                val datum = ankar.plusDays(offset)
                val dag = map[datum]
                val arbetat =
                    dag?.let { arbetadeMinuter(it.start, it.slut, it.rastMin) } ?: 0
                val utfall =
                    dag?.let { dagensUtfall(it, inst.normaltidMinPerDag, inst.arbetsdagarMask, aftnar) } ?: 0
                VeckaDagRad(datum, dag, arbetat, utfall)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val aftnarLediga =
        uiInstallningarLager.aftnarLediga.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            true,
        )

    val semesterdagarPerAr =
        uiInstallningarLager.semesterdagarPerAr.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SEMESTERDAGAR_STANDARD,
        )

    val semesterarStartmanad =
        uiInstallningarLager.semesterarStartmanad.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SEMESTERAR_STARTMANAD_STANDARD,
        )

    /** Saldo vid årets slut om årets takt håller i sig; null tidigt på året. */
    val saldoVidArsslut =
        combine(repository.observeraAllaDagar(), installning, saldo, uiInstallningarLager.aftnarLediga) { alla, inst, saldoNu, aftnar ->
            val idag = LocalDate.now()
            val utfallIAr = alla
                .filter { it.datum.year == idag.year && !it.datum.isAfter(idag) }
                .sumOf { dagensUtfall(it, inst.normaltidMinPerDag, inst.arbetsdagarMask, aftnar) }
            saldoVidArssluten(saldoNu, utfallIAr, idag)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val semester =
        combine(
            repository.observeraAllaDagar(),
            installning,
            semesterdagarPerAr,
            semesterarStartmanad,
        ) { alla, inst, perAr, startManad ->
            semesterSammanfattning(
                dagar = alla,
                idag = LocalDate.now(),
                arbetsdagarMask = inst.arbetsdagarMask,
                dagarPerAr = perAr,
                startManad = startManad,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val saldoTakMin =
        uiInstallningarLager.saldoTakMin.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val saldoGolvMin =
        uiInstallningarLager.saldoGolvMin.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val ledighetsKort =
        combine(saldo, installning, saldoGolvMin, aftnarLediga) { saldoNu, inst, golv, aftnar ->
            val flexDagar = ledighetsutrymme(saldoNu, golv, inst.normaltidMinPerDag)
            val idag = LocalDate.now()
            val antalForDatum =
                when (flexDagar) {
                    null -> 1
                    0 -> 0
                    else -> 1
                }
            val tidigaste =
                if (antalForDatum > 0) {
                    tidigasteLedigDag(idag, antalForDatum, inst.arbetsdagarMask, aftnar)
                } else {
                    null
                }
            LedighetsKortTillstand(flexDagar = flexDagar, tidigasteLedig = tidigaste)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LedighetsKortTillstand(flexDagar = null, tidigasteLedig = null),
        )

    val vanligStartMin =
        uiInstallningarLager.vanligStartMin.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            STANDARD_VANLIG_START_MIN,
        )

    val vanligSlutMin =
        uiInstallningarLager.vanligSlutMin.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            STANDARD_VANLIG_SLUT_MIN,
        )

    val vanligRastMin =
        uiInstallningarLager.vanligRastMin.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            STANDARD_VANLIG_RAST_MIN,
        )

    val visaFyllVanligDagKnapp =
        combine(idagDag, installning, vanligStartMin, vanligSlutMin, vanligRastMin) { dag, inst, startMin, slutMin, rastMin ->
            if (dag != null) return@combine false
            dagsForslagVanligDag(
                LocalDate.now(),
                inst.arbetsdagarMask,
                minuterTillLocalTime(startMin),
                minuterTillLocalTime(slutMin),
                rastMin,
            ) != null
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val veckaSammanfattning =
        combine(_veckaAnkar, repository.observeraAllaDagar(), installning, aftnarLediga) { ankar, alla, inst, aftnar ->
            periodSammanfattning(alla, ankar, sondagISammaVecka(ankar), inst, aftnar)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val manadSammanfattning =
        combine(repository.observeraAllaDagar(), installning, aftnarLediga) { alla, inst, aftnar ->
            val manad = YearMonth.now()
            periodSammanfattning(alla, manad.atDay(1), manad.atEndOfMonth(), inst, aftnar)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)


    val exportArManad =
        uiInstallningarLager.exportArManad.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            YearMonth.now(),
        )

    val arsOversiktTillstand =
        combine(
            repository.observeraAllaDagar(),
            installning,
            exportArManad,
            saldo,
            uiInstallningarLager.aftnarLediga,
        ) { dagar, inst, arManad, saldoNu, aftnar ->
            val ar = arManad.year
            val idag = LocalDate.now()
            val rader = arsOversikt(dagar, ar, inst, aftnar)
            val arArInnevarandeAr = ar == idag.year
            val prognosMin =
                if (arArInnevarandeAr) {
                    val utfallIAr =
                        dagar
                            .filter { it.datum.year == ar && !it.datum.isAfter(idag) }
                            .sumOf { dagensUtfall(it, inst.normaltidMinPerDag, inst.arbetsdagarMask, aftnar) }
                    saldoVidArssluten(saldoNu, utfallIAr, idag)
                } else {
                    null
                }
            ArsOversiktTillstand(ar, rader, prognosMin, arArInnevarandeAr)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ArsOversiktTillstand(
                ar = YearMonth.now().year,
                rader = emptyList(),
                prognosMin = null,
                arArInnevarandeAr = true,
            ),
        )

    init {
        viewModelScope.launch {
            // Kontrollera datumet varje minut, så att "idag" byts vid midnatt om appen ligger öppen.
            while (true) {
                delay(60_000)
                val nu = LocalDate.now()
                if (nu != _idag.value) _idag.value = nu
            }
        }
        viewModelScope.launch {
            proKalla.kontrollera()
            synkaPaminnelseJobb()
        }
        viewModelScope.launch {
            if (!uiInstallningarLager.valkommenVisad.first()) {
                if (repository.observeraAntalDagar().first() > 0) {
                    uiInstallningarLager.sattValkommenVisad()
                } else {
                    _visaValkommen.value = true
                }
            }
        }
    }

    fun avslutaValkommen() {
        _visaValkommen.value = false
        viewModelScope.launch { uiInstallningarLager.sattValkommenVisad() }
    }

    fun sattTemaLage(lage: TemaLage) {
        viewModelScope.launch { uiInstallningarLager.sattTemaLage(lage.name) }
    }

    fun foregaendeVecka() {
        _veckaAnkar.value = _veckaAnkar.value.minusWeeks(1)
    }

    fun nastaVecka() {
        _veckaAnkar.value = _veckaAnkar.value.plusWeeks(1)
    }

    fun gaTillDennaVecka() {
        _veckaAnkar.value = mandagISammaVecka(LocalDate.now())
    }

    fun kanLaggaTillNyDagPost(): Boolean =
        kanLaggaTillDagPost(antalDagar.value, proKalla.arPro.value)

    fun visaProDialog() {
        _proDialogSynlig.value = true
    }

    fun stangProDialog() {
        _proDialogSynlig.value = false
    }

    fun kopPro(activity: Activity) {
        proKalla.kop(activity)
    }

    fun stamplaIn() {
        viewModelScope.launch {
            val inst = installning.value
            when (
                repository.stamplaIn(LocalDate.now(), LocalTime.now(), inst.standardRastMin, proKalla.arPro.value)
            ) {
                FlextidRepository.SparaDagResult.Ok -> _meddelanden.emit(FlextidMeddelande.Sparat)
                FlextidRepository.SparaDagResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
                is FlextidRepository.SparaDagResult.Fel -> Unit
            }
        }
    }

    fun stamplaUt() {
        viewModelScope.launch {
            when (repository.stamplaUt(LocalDate.now(), LocalTime.now(), proKalla.arPro.value)) {
                FlextidRepository.SparaDagResult.Ok -> _meddelanden.emit(FlextidMeddelande.Sparat)
                FlextidRepository.SparaDagResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
                is FlextidRepository.SparaDagResult.Fel ->
                    _meddelanden.emit(FlextidMeddelande.SlutForeStart)
            }
        }
    }

    fun genvagStamplaIn() {
        viewModelScope.launch {
            val inst = installning.value
            when (
                val resultat =
                    repository.genvagStamplaIn(
                        LocalDate.now(),
                        LocalTime.now(),
                        inst.standardRastMin,
                        proKalla.arPro.value,
                    )
            ) {
                FlextidRepository.GenvagStamplaInResult.Sparat -> _meddelanden.emit(FlextidMeddelande.Sparat)
                is FlextidRepository.GenvagStamplaInResult.RedanInstamplad ->
                    _meddelanden.emit(FlextidMeddelande.RedanInstamplad(resultat.start))
                FlextidRepository.GenvagStamplaInResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
            }
        }
    }

    fun genvagStamplaUt() {
        viewModelScope.launch {
            when (repository.genvagStamplaUt(LocalDate.now(), LocalTime.now(), proKalla.arPro.value)) {
                FlextidRepository.GenvagStamplaUtResult.Sparat -> _meddelanden.emit(FlextidMeddelande.Sparat)
                FlextidRepository.GenvagStamplaUtResult.IngenInstempling ->
                    _meddelanden.emit(FlextidMeddelande.IngenInstempling)
                FlextidRepository.GenvagStamplaUtResult.SlutForeStart ->
                    _meddelanden.emit(FlextidMeddelande.SlutForeStart)
                FlextidRepository.GenvagStamplaUtResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
            }
        }
    }

    fun uppdateraIdagRast(minuter: Int) {
        viewModelScope.launch {
            val datum = LocalDate.now()
            val befintlig = repository.hamtaDag(datum)
            val dag = (befintlig ?: Dag(datum = datum, rastMin = minuter)).copy(rastMin = minuter)
            when (repository.sparaDagUtanValidering(dag, proKalla.arPro.value)) {
                FlextidRepository.SparaDagResult.Ok -> Unit
                FlextidRepository.SparaDagResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
                is FlextidRepository.SparaDagResult.Fel -> Unit
            }
        }
    }

    fun oppnaDagFormular(datum: LocalDate, forvaldTyp: DagTyp? = null) {
        viewModelScope.launch {
            if (repository.hamtaDag(datum) == null && !kanLaggaTillDagPost(repository.antalDagar(), proKalla.arPro.value)) {
                visaProDialog()
                _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                return@launch
            }
            val dag = repository.hamtaDag(datum)
            val inst = installning.value
            val vanligStart = minuterTillLocalTime(vanligStartMin.value)
            val vanligSlut = minuterTillLocalTime(vanligSlutMin.value)
            val vanligRast = vanligRastMin.value
            val vanligtForslag =
                if (dag == null) {
                    dagsForslagVanligDag(
                        datum,
                        inst.arbetsdagarMask,
                        vanligStart,
                        vanligSlut,
                        vanligRast,
                    )
                } else {
                    null
                }
            val senasteForslag =
                if (dag == null && vanligtForslag == null) {
                    senasteArbetsdagFore(repository.hamtaAllaDagar(), datum)
                } else {
                    null
                }
            _dagFormular.value =
                DagFormularTillstand(
                    datum = datum,
                    startText =
                        dag?.start?.toString()?.substring(0, 5)
                            ?: vanligtForslag?.start?.let { formateraTidKort(it) }
                            ?: "",
                    slutText =
                        dag?.slut?.toString()?.substring(0, 5)
                            ?: vanligtForslag?.slut?.let { formateraTidKort(it) }
                            ?: "",
                    rastText =
                        (dag?.rastMin ?: vanligtForslag?.rastMin ?: inst.standardRastMin).toString(),
                    typ = dag?.typ ?: forvaldTyp ?: DagTyp.ARBETE,
                    anteckning = dag?.anteckning ?: "",
                    forslagDatum = senasteForslag?.datum,
                    forslagStart = senasteForslag?.start?.let { formateraTidKort(it) } ?: "",
                    forslagSlut = senasteForslag?.slut?.let { formateraTidKort(it) } ?: "",
                    forslagRast = senasteForslag?.rastMin?.toString() ?: "",
                )
        }
    }

    fun planeraLedigDag(datum: LocalDate) {
        oppnaDagFormular(datum, forvaldTyp = DagTyp.LEDIG)
    }

    fun fyllVanligDagIdag() {
        viewModelScope.launch {
            val inst = installning.value
            when (
                repository.fyllVanligDag(
                    LocalDate.now(),
                    inst.arbetsdagarMask,
                    minuterTillLocalTime(vanligStartMin.value),
                    minuterTillLocalTime(vanligSlutMin.value),
                    vanligRastMin.value,
                    proKalla.arPro.value,
                )
            ) {
                FlextidRepository.FyllVanligDagResult.Sparat -> _meddelanden.emit(FlextidMeddelande.Sparat)
                FlextidRepository.FyllVanligDagResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
                FlextidRepository.FyllVanligDagResult.FinnsRedan,
                FlextidRepository.FyllVanligDagResult.IngetForslag,
                -> Unit
            }
        }
    }

    fun stangDagFormular() {
        _dagFormular.value = null
    }

    fun uppdateraDagFormular(tillstand: DagFormularTillstand) {
        _dagFormular.value = tillstand
    }

    fun sparaDagFormular() {
        val form = _dagFormular.value ?: return
        viewModelScope.launch {
            val start = tolkaTidText(form.startText)
            val slut = tolkaTidText(form.slutText)
            val rast = form.rastText.toIntOrNull() ?: installning.value.standardRastMin
            val dag =
                Dag(
                    datum = form.datum,
                    start = start,
                    slut = slut,
                    rastMin = rast.coerceAtLeast(0),
                    typ = form.typ,
                    anteckning = form.anteckning.trim().takeIf { it.isNotEmpty() },
                )
            when (repository.sparaDag(dag, proKalla.arPro.value)) {
                FlextidRepository.SparaDagResult.Ok -> {
                    _dagFormular.value = null
                    _meddelanden.emit(FlextidMeddelande.Sparat)
                }
                FlextidRepository.SparaDagResult.GransUtanPro -> {
                    visaProDialog()
                    _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                }
                is FlextidRepository.SparaDagResult.Fel -> {
                    _dagFormular.value = form.copy(fel = TidValideringFel.SlutForeStart)
                    _meddelanden.emit(FlextidMeddelande.SlutForeStart)
                }
            }
        }
    }

    fun taBortDag(datum: LocalDate) {
        viewModelScope.launch {
            repository.taBortDag(datum)
            _dagFormular.value = null
            _meddelanden.emit(FlextidMeddelande.Borttaget)
        }
    }

    fun sparaInstallning(
        normaltidText: String,
        rastText: String,
        ingangssaldoText: String,
        arbetsdagarMask: Int,
        vanligStartText: String,
        vanligSlutText: String,
        vanligRastText: String,
    ) {
        viewModelScope.launch {
            val normaltid = normaltidText.toIntOrNull() ?: installning.value.normaltidMinPerDag
            val rast = rastText.toIntOrNull() ?: installning.value.standardRastMin
            val ingang = parseSaldoMinuter(ingangssaldoText) ?: installning.value.ingangssaldoMin
            repository.sparaInstallning(
                installning.value.copy(
                    normaltidMinPerDag = normaltid.coerceAtLeast(0),
                    standardRastMin = rast.coerceAtLeast(0),
                    ingangssaldoMin = ingang,
                    arbetsdagarMask = arbetsdagarMask,
                ),
            )
            sparaVanligaTiderInternt(vanligStartText, vanligSlutText, vanligRastText)
            _meddelanden.emit(FlextidMeddelande.Sparat)
        }
    }

    private suspend fun sparaVanligaTiderInternt(startText: String, slutText: String, rastText: String) {
        val startMin =
            tolkaTidText(startText)?.let { localTimeTillMinuter(it) }
                ?: vanligStartMin.value
        val slutMin =
            tolkaTidText(slutText)?.let { localTimeTillMinuter(it) }
                ?: vanligSlutMin.value
        val rastMin = rastText.toIntOrNull() ?: vanligRastMin.value
        uiInstallningarLager.sattVanligaTider(startMin, slutMin, rastMin)
    }

    fun sattStamplaUtPaminnelse(pa: Boolean) {
        viewModelScope.launch {
            paminnelseInstallningar.sattStamplaUtPa(pa)
            synkaPaminnelseJobb()
        }
    }

    fun sattStamplaInPaminnelse(pa: Boolean) {
        viewModelScope.launch {
            paminnelseInstallningar.sattStamplaInPa(pa)
            synkaPaminnelseJobb()
        }
    }

    fun sattStamplaInPaminnelseKlockslag(minEfterMidnatt: Int) {
        viewModelScope.launch {
            paminnelseInstallningar.sattStamplaInPaminnelseMinEfterMidnatt(minEfterMidnatt)
            synkaPaminnelseJobb()
        }
    }

    fun synkaPaminnelseJobb() {
        viewModelScope.launch {
            val pa = paminnelseInstallningar.nagonPaminnelsePa.first()
            PaminnelsePlanerare.synka<PaminnelseWorker>(
                appContext,
                pa,
                FlextidPaminnelseKalla.UNIKT_JOBB_NAMN,
            )
        }
    }

    /** Texten är timmar, med eller utan minuter ("40" eller "40:00"). Tom text tar bort gränsen. */
    fun sattSaldoGranser(takText: String, golvText: String) {
        fun tolka(t: String): Int? {
            val r = t.trim().removePrefix("+").removePrefix("-").removePrefix("\u2212")
            if (r.isEmpty()) return null
            val delar = r.split(":")
            val h = delar[0].toIntOrNull() ?: return null
            val m = delar.getOrNull(1)?.toIntOrNull() ?: 0
            return h * 60 + m
        }
        viewModelScope.launch { uiInstallningarLager.sattSaldoGranser(tolka(takText), tolka(golvText)?.let { -it }) }
    }

    fun sattAftnarLediga(lediga: Boolean) {
        viewModelScope.launch {
            uiInstallningarLager.sattAftnarLediga(lediga)
            synkaPaminnelseJobb()
        }
    }

    fun sattSemesterdagarPerAr(text: String) {
        val dagar = text.trim().toIntOrNull() ?: return
        viewModelScope.launch { uiInstallningarLager.sattSemesterdagarPerAr(dagar) }
    }

    fun sattSemesterarKalenderar(kalenderar: Boolean) {
        viewModelScope.launch {
            uiInstallningarLager.sattSemesterarStartmanad(
                if (kalenderar) 1 else SEMESTERAR_STARTMANAD_STANDARD,
            )
        }
    }

    fun sattExportArManad(arManad: YearMonth) {
        viewModelScope.launch { uiInstallningarLager.sattExportArManad(arManad) }
    }

    suspend fun skapaSakerhetskopiaJson(): String =
        skapaFlextidSakerhetskopiaJson(repository, uiInstallningarLager, paminnelseInstallningar)

    fun valtAutoBackupMapp(uri: Uri, namn: String?) {
        viewModelScope.launch {
            val tidigare = autoBackupInstallningar.autoBackupMapp.first()
            autoBackupInstallningar.sattAutoBackupMapp(uri.toString(), namn)
            if (tidigare == null) {
                AutoSakerhetskopiaPlanerare.korNu(appContext)
            }
            synkaAutoBackupJobb()
        }
    }

    fun stangAvAutoBackup() {
        viewModelScope.launch {
            autoBackupInstallningar.sattAutoBackupMapp(null, null)
            synkaAutoBackupJobb()
        }
    }

    fun sattAutoBackupIntervall(dagar: Int) {
        viewModelScope.launch { autoBackupInstallningar.sattAutoBackupIntervall(dagar) }
    }

    fun korNuAutoBackup() {
        AutoSakerhetskopiaPlanerare.korNu(appContext)
    }

    fun synkaAutoBackupJobb() {
        viewModelScope.launch {
            AutoSakerhetskopiaPlanerare.synka(
                appContext,
                autoBackupInstallningar.autoBackupMapp.first() != null,
            )
        }
    }

    fun importeraSakerhetskopia(json: String) {
        viewModelScope.launch {
            when (val las = lasSakerhetskopiaJson(json)) {
                is SakerhetskopiaJsonLasResult.Ok -> {
                    repository.ersattAllData(
                        las.innehall.dagar.map { sakerhetskopiaDagTillDag(it) },
                        sakerhetskopiaInstallningTillInstallning(las.innehall.installning),
                    )
                    paminnelseInstallningar.sattStamplaInPaminnelseMinEfterMidnatt(
                        las.innehall.installning.stamplaInPaminnelseMinEfterMidnatt,
                    )
                    uiInstallningarLager.sattAftnarLediga(las.innehall.installning.aftnarLediga)
                    uiInstallningarLager.sattSemesterdagarPerAr(las.innehall.installning.semesterdagarPerAr)
                    uiInstallningarLager.sattSemesterarStartmanad(las.innehall.installning.semesterarStartmanad)
                    synkaPaminnelseJobb()
                    _meddelanden.emit(FlextidMeddelande.Importerat)
                }
                else -> _meddelanden.emit(FlextidMeddelande.ImportFel)
            }
        }
    }

    /** Årsöversikt för året i exportvalet: en rad per månad med saldo vid månadens slut. */
    suspend fun skapaArsCsv(rubriker: List<String>, locale: Locale): String {
        val ar = exportArManad.value.year
        val rader =
            arsOversikt(
                repository.hamtaAllaDagar(),
                ar,
                repository.hamtaInstallning(),
                uiInstallningarLager.aftnarLediga.first(),
            )
        val manadsnamn = java.time.Month.entries.map {
            it.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, locale)
        }
        return arsOversiktCsv(rader, rubriker, manadsnamn, locale)
    }

    suspend fun skapaManadsCsv(texter: ManadsCsvTexter, locale: Locale): String {
        val dagar = repository.hamtaAllaDagar()
        val inst = repository.hamtaInstallning()
        val arManad = exportArManad.value
        return byggManadsCsv(dagar, inst, arManad, texter, locale, uiInstallningarLager.aftnarLediga.first())
    }

    suspend fun skapaManadsRapport(texter: ManadsRapportTexter, locale: Locale): ManadsRapportData {
        val dagar = repository.hamtaAllaDagar()
        val inst = repository.hamtaInstallning()
        val arManad = exportArManad.value
        return byggManadsRapport(
            dagar,
            inst,
            arManad,
            texter,
            locale,
            aftnarLediga = uiInstallningarLager.aftnarLediga.first(),
        )
    }

    fun skrivTillUri(uri: Uri, skriv: suspend (java.io.OutputStream) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appContext.contentResolver.openOutputStream(uri)?.use { skriv(it) }
            }
            _meddelanden.emit(FlextidMeddelande.Exporterat)
        }
    }

    fun lasFranUri(uri: Uri, las: suspend (java.io.InputStream) -> String) {
        viewModelScope.launch {
            val text =
                withContext(Dispatchers.IO) {
                    appContext.contentResolver.openInputStream(uri)?.use { las(it) } ?: ""
                }
            importeraSakerhetskopia(text)
        }
    }

    private fun parseSaldoMinuter(text: String): Int? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return 0
        val neg = trimmed.startsWith("-")
        val parts = trimmed.removePrefix("-").removePrefix("+").split(':')
        if (parts.size == 2) {
            val h = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            val min = h * 60 + m
            return if (neg) -min else min
        }
        return trimmed.toIntOrNull()
    }

    private fun formateraTidKort(tid: LocalTime): String =
        String.format("%02d:%02d", tid.hour, tid.minute)
}

class FlextidViewModelFactory(
    private val repository: FlextidRepository,
    private val uiInstallningarLager: UiInstallningarLager,
    private val paminnelseInstallningar: FlextidPaminnelseInstallningarLager,
    private val proKalla: ProKalla,
    private val appContext: Context,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlextidViewModel::class.java)) {
            return FlextidViewModel(
                repository,
                uiInstallningarLager,
                paminnelseInstallningar,
                proKalla,
                appContext,
            ) as T
        }
        throw IllegalArgumentException("Okänd ViewModel")
    }
}
