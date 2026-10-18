package se.tmconnect.flextid.ui

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.FlextidRepository
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.export.ManadsCsvTexter
import se.tmconnect.flextid.export.SakerhetskopiaInnehall
import se.tmconnect.flextid.export.SakerhetskopiaJsonLasResult
import se.tmconnect.flextid.export.byggManadsCsv
import se.tmconnect.flextid.export.dagTillSakerhetskopia
import se.tmconnect.flextid.export.installningTillSakerhetskopia
import se.tmconnect.flextid.export.lasSakerhetskopiaJson
import se.tmconnect.flextid.export.sakerhetskopiaDagTillDag
import se.tmconnect.flextid.export.sakerhetskopiaInstallningTillInstallning
import se.tmconnect.flextid.export.skrivSakerhetskopiaJson
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.TidValideringFel
import se.tmconnect.flextid.regler.arbetadeMinuter
import se.tmconnect.flextid.regler.dagensUtfall
import se.tmconnect.flextid.regler.mandagISammaVecka
import se.tmconnect.flextid.regler.sondagISammaVecka
import se.tmconnect.flextid.regler.tolkaTidText
import se.tmconnect.karna.export.skrivCsvMedBom
import se.tmconnect.karna.tema.TemaLage
import java.util.Locale

sealed interface FlextidMeddelande {
    data object Sparat : FlextidMeddelande

    data object Borttaget : FlextidMeddelande

    data object Exporterat : FlextidMeddelande

    data object Importerat : FlextidMeddelande

    data object SlutForeStart : FlextidMeddelande

    data object ImportFel : FlextidMeddelande
}

data class VeckaDagRad(
    val datum: LocalDate,
    val dag: Dag?,
    val arbetadeMin: Int,
    val utfallMin: Int,
)

data class DagFormularTillstand(
    val datum: LocalDate,
    val startText: String = "",
    val slutText: String = "",
    val rastText: String = "",
    val typ: DagTyp = DagTyp.ARBETE,
    val anteckning: String = "",
    val fel: TidValideringFel? = null,
)

class FlextidViewModel(
    private val repository: FlextidRepository,
    private val uiInstallningarLager: UiInstallningarLager,
    private val appContext: Context,
) : ViewModel() {
    private val _veckaAnkar = MutableStateFlow(mandagISammaVecka(LocalDate.now()))
    val veckaAnkar = _veckaAnkar.asStateFlow()

    private val _dagFormular = MutableStateFlow<DagFormularTillstand?>(null)
    val dagFormular = _dagFormular.asStateFlow()

    private val _meddelanden = MutableSharedFlow<FlextidMeddelande>(extraBufferCapacity = 1)
    val meddelanden = _meddelanden.asSharedFlow()

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

    val exportArManad =
        uiInstallningarLager.exportArManad.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            YearMonth.now(),
        )

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

    fun stamplaIn() {
        viewModelScope.launch {
            val nu = LocalTime.now()
            val inst = installning.value
            repository.stamplaIn(LocalDate.now(), nu, inst.standardRastMin)
            _meddelanden.emit(FlextidMeddelande.Sparat)
        }
    }

    fun stamplaUt() {
        viewModelScope.launch {
            when (repository.stamplaUt(LocalDate.now(), LocalTime.now())) {
                FlextidRepository.SparaDagResult.Ok -> _meddelanden.emit(FlextidMeddelande.Sparat)
                is FlextidRepository.SparaDagResult.Fel ->
                    _meddelanden.emit(FlextidMeddelande.SlutForeStart)
            }
        }
    }

    fun uppdateraIdagRast(minuter: Int) {
        viewModelScope.launch {
            val datum = LocalDate.now()
            val befintlig = repository.hamtaDag(datum)
            val dag = (befintlig ?: Dag(datum = datum, rastMin = minuter)).copy(rastMin = minuter)
            repository.sparaDagUtanValidering(dag)
        }
    }

    fun oppnaDagFormular(datum: LocalDate) {
        viewModelScope.launch {
            val dag = repository.hamtaDag(datum)
            _dagFormular.value =
                DagFormularTillstand(
                    datum = datum,
                    startText = dag?.start?.toString()?.substring(0, 5) ?: "",
                    slutText = dag?.slut?.toString()?.substring(0, 5) ?: "",
                    rastText = (dag?.rastMin ?: installning.value.standardRastMin).toString(),
                    typ = dag?.typ ?: DagTyp.ARBETE,
                    anteckning = dag?.anteckning ?: "",
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
            when (repository.sparaDag(dag)) {
                FlextidRepository.SparaDagResult.Ok -> {
                    _dagFormular.value = null
                    _meddelanden.emit(FlextidMeddelande.Sparat)
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

    fun sattExportArManad(arManad: YearMonth) {
        viewModelScope.launch { uiInstallningarLager.sattExportArManad(arManad) }
    }

    suspend fun skapaSakerhetskopiaJson(): String {
        val dagar = repository.hamtaAllaDagar()
        val inst = repository.hamtaInstallning()
        val innehall =
            SakerhetskopiaInnehall(
                version = se.tmconnect.flextid.export.FLEXTID_SAKERHETSKOPIA_VERSION,
                skapad = DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()),
                installning = installningTillSakerhetskopia(inst),
                dagar = dagar.map { dagTillSakerhetskopia(it) },
            )
        return skrivSakerhetskopiaJson(innehall)
    }

    fun importeraSakerhetskopia(json: String) {
        viewModelScope.launch {
            when (val las = lasSakerhetskopiaJson(json)) {
                is SakerhetskopiaJsonLasResult.Ok -> {
                    repository.ersattAllData(
                        las.innehall.dagar.map { sakerhetskopiaDagTillDag(it) },
                        sakerhetskopiaInstallningTillInstallning(las.innehall.installning),
                    )
                    _meddelanden.emit(FlextidMeddelande.Importerat)
                }
                else -> _meddelanden.emit(FlextidMeddelande.ImportFel)
            }
        }
    }

    suspend fun skapaManadsCsv(texter: ManadsCsvTexter, locale: Locale): String {
        val dagar = repository.hamtaAllaDagar()
        val inst = repository.hamtaInstallning()
        val arManad = exportArManad.value
        return byggManadsCsv(dagar, inst, arManad, texter, locale)
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
    private val appContext: Context,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlextidViewModel::class.java)) {
            return FlextidViewModel(repository, uiInstallningarLager, appContext) as T
        }
        throw IllegalArgumentException("Okänd ViewModel")
    }
}
