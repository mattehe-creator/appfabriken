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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
import se.tmconnect.flextid.regler.mandagISammaVecka
import se.tmconnect.flextid.regler.sondagISammaVecka
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

    data class Instamplad(val tid: LocalTime) : FlextidMeddelande

    data class Utstamplad(val tid: LocalTime) : FlextidMeddelande

    data class RedanInstamplad(val tid: LocalTime) : FlextidMeddelande

    data object IngenInstampling : FlextidMeddelande

    data class RedanUtstamplad(val tid: LocalTime) : FlextidMeddelande
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
        repository.observeraSaldo().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0,
        )

    val idagDag =
        repository.observeraDag(LocalDate.now()).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null,
        )

    val veckaRader =
        combine(_veckaAnkar, repository.observeraAllaDagar(), installning) { ankar, alla, inst ->
            val slut = sondagISammaVecka(ankar)
            val map = alla.filter { !it.datum.isBefore(ankar) && !it.datum.isAfter(slut) }.associateBy { it.datum }
            (0L..6L).map { offset ->
                val datum = ankar.plusDays(offset)
                val dag = map[datum]
                val arbetat =
                    dag?.let { arbetadeMinuter(it.start, it.slut, it.rastMin) } ?: 0
                val utfall =
                    dag?.let { dagensUtfall(it, inst.normaltidMinPerDag, inst.arbetsdagarMask) } ?: 0
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
        combine(repository.observeraAllaDagar(), installning, saldo) { alla, inst, saldoNu ->
            val idag = LocalDate.now()
            val utfallIAr = alla
                .filter { it.datum.year == idag.year && !it.datum.isAfter(idag) }
                .sumOf { dagensUtfall(it, inst.normaltidMinPerDag, inst.arbetsdagarMask) }
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
        combine(repository.observeraAllaDagar(), installning, exportArManad, saldo) { dagar, inst, arManad, saldoNu ->
            val ar = arManad.year
            val idag = LocalDate.now()
            val rader = arsOversikt(dagar, ar, inst)
            val arArInnevarandeAr = ar == idag.year
            val prognosMin =
                if (arArInnevarandeAr) {
                    val utfallIAr =
                        dagar
                            .filter { it.datum.year == ar && !it.datum.isAfter(idag) }
                            .sumOf { dagensUtfall(it, inst.normaltidMinPerDag, inst.arbetsdagarMask) }
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
            hanteraStamplaInResultat(
                repository.stamplaIn(
                    LocalDate.now(),
                    LocalTime.now(),
                    installning.value.standardRastMin,
                    proKalla.arPro.value,
                ),
            )
        }
    }

    fun stamplaUt() {
        viewModelScope.launch {
            hanteraStamplaUtResultat(
                repository.stamplaUt(LocalDate.now(), LocalTime.now(), proKalla.arPro.value),
            )
        }
    }

    private suspend fun hanteraStamplaInResultat(resultat: FlextidRepository.SparaDagResult) {
        when (resultat) {
            FlextidRepository.SparaDagResult.Ok -> {
                val start = repository.hamtaDag(LocalDate.now())?.start
                if (start != null) {
                    _meddelanden.emit(FlextidMeddelande.Instamplad(start))
                } else {
                    _meddelanden.emit(FlextidMeddelande.Sparat)
                }
            }
            FlextidRepository.SparaDagResult.GransUtanPro -> {
                visaProDialog()
                _meddelanden.emit(FlextidMeddelande.GransUtanPro)
            }
            is FlextidRepository.SparaDagResult.RedanInstamplad ->
                _meddelanden.emit(FlextidMeddelande.RedanInstamplad(resultat.start))
            is FlextidRepository.SparaDagResult.Fel -> Unit
            FlextidRepository.SparaDagResult.IngenInstampling -> Unit
            is FlextidRepository.SparaDagResult.RedanUtstamplad -> Unit
        }
    }

    private suspend fun hanteraStamplaUtResultat(resultat: FlextidRepository.SparaDagResult) {
        when (resultat) {
            FlextidRepository.SparaDagResult.Ok -> {
                val slut = repository.hamtaDag(LocalDate.now())?.slut
                if (slut != null) {
                    _meddelanden.emit(FlextidMeddelande.Utstamplad(slut))
                } else {
                    _meddelanden.emit(FlextidMeddelande.Sparat)
                }
            }
            FlextidRepository.SparaDagResult.GransUtanPro -> {
                visaProDialog()
                _meddelanden.emit(FlextidMeddelande.GransUtanPro)
            }
            is FlextidRepository.SparaDagResult.Fel ->
                _meddelanden.emit(FlextidMeddelande.SlutForeStart)
            FlextidRepository.SparaDagResult.IngenInstampling ->
                _meddelanden.emit(FlextidMeddelande.IngenInstampling)
            is FlextidRepository.SparaDagResult.RedanInstamplad -> Unit
            is FlextidRepository.SparaDagResult.RedanUtstamplad ->
                _meddelanden.emit(FlextidMeddelande.RedanUtstamplad(resultat.slut))
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
                is FlextidRepository.SparaDagResult.RedanInstamplad -> Unit
                FlextidRepository.SparaDagResult.IngenInstampling -> Unit
                is FlextidRepository.SparaDagResult.RedanUtstamplad -> Unit
            }
        }
    }

    fun oppnaDagFormular(datum: LocalDate) {
        viewModelScope.launch {
            if (repository.hamtaDag(datum) == null && !kanLaggaTillDagPost(repository.antalDagar(), proKalla.arPro.value)) {
                visaProDialog()
                _meddelanden.emit(FlextidMeddelande.GransUtanPro)
                return@launch
            }
            val dag = repository.hamtaDag(datum)
            val forslag = if (dag == null) senasteArbetsdagFore(repository.hamtaAllaDagar(), datum) else null
            _dagFormular.value =
                DagFormularTillstand(
                    datum = datum,
                    startText = dag?.start?.toString()?.substring(0, 5) ?: "",
                    slutText = dag?.slut?.toString()?.substring(0, 5) ?: "",
                    rastText = (dag?.rastMin ?: installning.value.standardRastMin).toString(),
                    typ = dag?.typ ?: DagTyp.ARBETE,
                    anteckning = dag?.anteckning ?: "",
                    forslagDatum = forslag?.datum,
                    forslagStart = forslag?.start?.toString()?.substring(0, 5) ?: "",
                    forslagSlut = forslag?.slut?.toString()?.substring(0, 5) ?: "",
                    forslagRast = forslag?.rastMin?.toString() ?: "",
                )
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
                is FlextidRepository.SparaDagResult.RedanInstamplad -> Unit
                FlextidRepository.SparaDagResult.IngenInstampling -> Unit
                is FlextidRepository.SparaDagResult.RedanUtstamplad -> Unit
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
            _meddelanden.emit(FlextidMeddelande.Sparat)
        }
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
        val rader = arsOversikt(repository.hamtaAllaDagar(), ar, installning.value)
        val manadsnamn = java.time.Month.entries.map {
            it.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, locale)
        }
        return arsOversiktCsv(rader, rubriker, manadsnamn, locale)
    }

    suspend fun skapaManadsCsv(texter: ManadsCsvTexter, locale: Locale): String {
        val dagar = repository.hamtaAllaDagar()
        val inst = repository.hamtaInstallning()
        val arManad = exportArManad.value
        return byggManadsCsv(dagar, inst, arManad, texter, locale)
    }

    suspend fun skapaManadsRapport(texter: ManadsRapportTexter, locale: Locale): ManadsRapportData {
        val dagar = repository.hamtaAllaDagar()
        val inst = repository.hamtaInstallning()
        val arManad = exportArManad.value
        return byggManadsRapport(dagar, inst, arManad, texter, locale, aftnarLediga = aftnarLediga.value)
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
