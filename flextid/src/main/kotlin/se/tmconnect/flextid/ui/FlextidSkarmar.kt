package se.tmconnect.flextid.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.SemesterSammanfattning
import se.tmconnect.flextid.regler.PeriodSammanfattning
import se.tmconnect.flextid.regler.VeckoTextTexter
import se.tmconnect.flextid.regler.veckoText
import se.tmconnect.flextid.regler.SaldoStatus
import se.tmconnect.flextid.regler.saldoStatus
import se.tmconnect.flextid.regler.dagensUtfall
import se.tmconnect.flextid.regler.gaHemTid
import se.tmconnect.flextid.regler.gaHemTidForNollsaldo
import se.tmconnect.flextid.regler.helgdagPa
import se.tmconnect.flextid.regler.formateraMinuterSomTid
import se.tmconnect.flextid.regler.formateraSaldoMinuter
import se.tmconnect.flextid.regler.formateraTid
import se.tmconnect.flextid.regler.giltigaStamplaInPaminnelseMinEfterMidnatt
import se.tmconnect.flextid.regler.minuterTillLocalTime
import se.tmconnect.flextid.regler.veckodagBit
import se.tmconnect.karna.ui.tryckyta48
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.tema.TemaLage

enum class FlextidFlik { IDAG, VECKA, INSTALLNINGAR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlextidStartSkarm(
    viewModel: FlextidViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    startFlik: FlextidFlik? = null,
    behorighetNekad: Boolean = false,
    onStamplaUtPaminnelse: (Boolean) -> Unit,
    onStamplaInPaminnelse: (Boolean) -> Unit,
    onExporteraCsv: () -> Unit,
    onExporteraArsCsv: () -> Unit = {},
    onExporteraPdf: () -> Unit,
    onExporteraSakerhetskopia: () -> Unit,
    onImporteraSakerhetskopia: () -> Unit,
    locale: Locale,
) {
    var flik by remember(startFlik) { mutableStateOf(startFlik ?: FlextidFlik.IDAG) }
    val saldo by viewModel.saldo.collectAsState()
    val installning by viewModel.installning.collectAsState()
    val idagDag by viewModel.idagDag.collectAsState()
    val veckaRader by viewModel.veckaRader.collectAsState()
    val veckaAnkar by viewModel.veckaAnkar.collectAsState()
    val dagFormular by viewModel.dagFormular.collectAsState()
    val exportArManad by viewModel.exportArManad.collectAsState()
    val temaLage by viewModel.temaLage.collectAsState()
    val arPro by viewModel.arPro.collectAsState()
    val antalDagar by viewModel.antalDagar.collectAsState()
    val stamplaUtPaminnelse by viewModel.stamplaUtPaminnelsePa.collectAsState()
    val stamplaInPaminnelse by viewModel.stamplaInPaminnelsePa.collectAsState()
    val stamplaInPaminnelseMin by viewModel.stamplaInPaminnelseMinEfterMidnatt.collectAsState()
    val semester by viewModel.semester.collectAsState()
    val veckaSammanfattning by viewModel.veckaSammanfattning.collectAsState()
    val saldoTak by viewModel.saldoTakMin.collectAsState()
    val saldoGolv by viewModel.saldoGolvMin.collectAsState()
    val manadSammanfattning by viewModel.manadSammanfattning.collectAsState()
    val aftnarLediga by viewModel.aftnarLediga.collectAsState()
    val semesterdagarPerAr by viewModel.semesterdagarPerAr.collectAsState()
    val semesterarStartmanad by viewModel.semesterarStartmanad.collectAsState()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = flik == FlextidFlik.IDAG,
                    onClick = { flik = FlextidFlik.IDAG },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = stringResource(R.string.flik_idag),
                        )
                    },
                    label = { Text(stringResource(R.string.flik_idag)) },
                )
                NavigationBarItem(
                    selected = flik == FlextidFlik.VECKA,
                    onClick = { flik = FlextidFlik.VECKA },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.flik_vecka),
                        )
                    },
                    label = { Text(stringResource(R.string.flik_vecka)) },
                )
                NavigationBarItem(
                    selected = flik == FlextidFlik.INSTALLNINGAR,
                    onClick = { flik = FlextidFlik.INSTALLNINGAR },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.flik_installningar),
                        )
                    },
                    label = { Text(stringResource(R.string.flik_installningar)) },
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            when (flik) {
                FlextidFlik.IDAG ->
                    IdagInnehall(
                        saldo = saldo,
                        dag = idagDag,
                        standardRast = installning.standardRastMin,
                        installning = installning,
                        semester = semester,
                        manad = manadSammanfattning,
                        saldoTakMin = saldoTak,
                        saldoGolvMin = saldoGolv,
                        locale = locale,
                        onStamplaIn = viewModel::stamplaIn,
                        onStamplaUt = viewModel::stamplaUt,
                        onRastChange = viewModel::uppdateraIdagRast,
                        onRedigera = { viewModel.oppnaDagFormular(LocalDate.now()) },
                    )
                FlextidFlik.VECKA ->
                    VeckaInnehall(
                        veckaAnkar = veckaAnkar,
                        rader = veckaRader,
                        sammanfattning = veckaSammanfattning,
                        locale = locale,
                        onForegaende = viewModel::foregaendeVecka,
                        onNasta = viewModel::nastaVecka,
                        onDennaVecka = viewModel::gaTillDennaVecka,
                        onDagClick = viewModel::oppnaDagFormular,
                    )
                FlextidFlik.INSTALLNINGAR ->
                    InstallningarInnehall(
                        installning = installning,
                        exportArManad = exportArManad,
                        temaLage = temaLage,
                        locale = locale,
                        arPro = arPro,
                        antalDagar = antalDagar,
                        stamplaUtPaminnelse = stamplaUtPaminnelse,
                        stamplaInPaminnelse = stamplaInPaminnelse,
                        stamplaInPaminnelseMinEfterMidnatt = stamplaInPaminnelseMin,
                        behorighetNekad = behorighetNekad,
                        onVisaProDialog = viewModel::visaProDialog,
                        onStamplaUtPaminnelse = onStamplaUtPaminnelse,
                        onStamplaInPaminnelse = onStamplaInPaminnelse,
                        onStamplaInPaminnelseKlockslag = viewModel::sattStamplaInPaminnelseKlockslag,
                        onSpara = viewModel::sparaInstallning,
                        onTema = viewModel::sattTemaLage,
                        onExportManad = viewModel::sattExportArManad,
                        onExporteraCsv = onExporteraCsv,
                        onExporteraArsCsv = onExporteraArsCsv,
                        onExporteraPdf = onExporteraPdf,
                        onExporteraJson = onExporteraSakerhetskopia,
                        onImporteraJson = onImporteraSakerhetskopia,
                        aftnarLediga = aftnarLediga,
                        semesterdagarPerAr = semesterdagarPerAr,
                        semesterarKalenderar = semesterarStartmanad == 1,
                        onAftnarLediga = viewModel::sattAftnarLediga,
                        onSemesterdagarPerAr = viewModel::sattSemesterdagarPerAr,
                        onSemesterarKalenderar = viewModel::sattSemesterarKalenderar,
                        saldoTakMin = saldoTak,
                        saldoGolvMin = saldoGolv,
                        onSaldoGranser = viewModel::sattSaldoGranser,
                    )
            }
        }
    }

    dagFormular?.let { form ->
        DagFormularDialog(
            tillstand = form,
            locale = locale,
            onAndra = viewModel::uppdateraDagFormular,
            onSpara = viewModel::sparaDagFormular,
            onTaBort = { viewModel.taBortDag(form.datum) },
            onStang = viewModel::stangDagFormular,
        )
    }
}

@Composable
internal fun IdagInnehall(
    saldo: Int,
    dag: se.tmconnect.flextid.data.Dag?,
    standardRast: Int,
    installning: se.tmconnect.flextid.data.Installning,
    semester: SemesterSammanfattning?,
    locale: Locale,
    onStamplaIn: () -> Unit,
    onStamplaUt: () -> Unit,
    onRastChange: (Int) -> Unit,
    onRedigera: () -> Unit,
    manad: PeriodSammanfattning? = null,
    saldoTakMin: Int? = null,
    saldoGolvMin: Int? = null,
) {
    var rastText by remember(dag?.rastMin) { mutableStateOf((dag?.rastMin ?: standardRast).toString()) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.saldo_rubrik, formateraSaldoMinuter(saldo, locale)),
            style = MaterialTheme.typography.headlineSmall,
        )
        when (saldoStatus(saldo, saldoTakMin, saldoGolvMin)) {
            SaldoStatus.OVER_TAKET ->
                Text(
                    text = stringResource(R.string.saldo_over_taket, formateraSaldoMinuter(saldoTakMin ?: 0, locale)),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            SaldoStatus.UNDER_GOLVET ->
                Text(
                    text = stringResource(R.string.saldo_under_golvet, formateraSaldoMinuter(saldoGolvMin ?: 0, locale)),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            SaldoStatus.INOM -> Unit
        }
        Text(
            text = formateraDatum(LocalDate.now(), locale),
            style = MaterialTheme.typography.bodyLarge,
        )
        helgdagPa(LocalDate.now(), medAftnar = true)?.let { helg ->
            Text(
                text = helgdagNamn(helg),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        dag?.start?.let {
            Text(
                text = stringResource(R.string.idag_in, formateraTid(it, locale)),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        dag?.slut?.let {
            Text(
                text = stringResource(R.string.idag_ut, formateraTid(it, locale)),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        val start = dag?.start
        if (dag != null && start != null && dag.slut == null) {
            val dagensUtfallNu = dagensUtfall(dag, installning.normaltidMinPerDag, installning.arbetsdagarMask)
            val saldoForeIdag = saldo - dagensUtfallNu
            gaHemTid(start, dag.rastMin, installning.normaltidMinPerDag)?.let { tid ->
                Text(
                    text = stringResource(R.string.ga_hem_normaltid, formateraTid(tid, locale)),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            gaHemTidForNollsaldo(start, dag.rastMin, installning.normaltidMinPerDag, saldoForeIdag)?.let { tid ->
                Text(
                    text = stringResource(R.string.ga_hem_nollsaldo, formateraTid(tid, locale)),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onStamplaIn, modifier = Modifier.tryckyta48()) {
                Text(stringResource(R.string.stampla_in))
            }
            Button(onClick = onStamplaUt, modifier = Modifier.tryckyta48()) {
                Text(stringResource(R.string.stampla_ut))
            }
        }
        OutlinedTextField(
            value = rastText,
            onValueChange = {
                rastText = it
                it.toIntOrNull()?.let(onRastChange)
            },
            label = { Text(stringResource(R.string.rast_min)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedButton(onClick = onRedigera, modifier = Modifier.tryckyta48()) {
            Text(stringResource(R.string.redigera_dag))
        }
        manad?.let { m ->
            Text(
                text = stringResource(
                    R.string.manad_arbetat,
                    YearMonth.now().month.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, locale)
                        .replaceFirstChar { it.titlecase(locale) },
                    formateraMinuterSomTid(m.arbetadMin, locale),
                    formateraMinuterSomTid(m.normaltidMin, locale),
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        semester?.let { sem ->
            Text(
                text = stringResource(
                    R.string.semester_rad,
                    formateraDatum(sem.ar.fran, locale),
                    formateraDatum(sem.ar.till, locale),
                ),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.semester_siffror, sem.uttagna, sem.planerade, sem.kvar),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
internal fun VeckaInnehall(
    veckaAnkar: LocalDate,
    rader: List<VeckaDagRad>,
    locale: Locale,
    onForegaende: () -> Unit,
    onNasta: () -> Unit,
    onDennaVecka: () -> Unit,
    onDagClick: (LocalDate) -> Unit,
    sammanfattning: PeriodSammanfattning? = null,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val slut = veckaAnkar.plusDays(6)
    val veckosumma = rader.sumOf { it.utfallMin }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onForegaende) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.foregaende_vecka))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text =
                    stringResource(
                        R.string.vecka_intervall,
                        formateraDatum(veckaAnkar, locale),
                        formateraDatum(slut, locale),
                    ),
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(onClick = onDennaVecka) {
                Text(stringResource(R.string.denna_vecka))
            }
        }
        IconButton(onClick = onNasta) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.nasta_vecka))
        }
    }
    Text(
        text = stringResource(R.string.veckosumma, formateraSaldoMinuter(veckosumma, locale)),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 8.dp),
    )
    sammanfattning?.let { v ->
        Text(
            text = stringResource(
                R.string.vecka_arbetat,
                formateraMinuterSomTid(v.arbetadMin, locale),
                formateraMinuterSomTid(v.normaltidMin, locale),
            ),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
    }
    val veckoNummer = veckaAnkar.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear())
    val rubrik = stringResource(R.string.dela_vecka_rubrik, veckoNummer)
    val summaEtikett = stringResource(R.string.dela_vecka_summa)
    val veckodagar = DayOfWeek.entries.associateWith { stringResource(veckodagStringRes(it)) }
    val typer = DagTyp.entries.associateWith { stringResource(dagTypStringRes(it)) }
    val delaRubrik = stringResource(R.string.dela_vecka)
    TextButton(
        onClick = {
            val text = veckoText(
                dagar = rader.mapNotNull { it.dag },
                mandag = veckaAnkar,
                texter = VeckoTextTexter(rubrik, veckodagar, typer, summaEtikett),
                locale = locale,
            )
            val skicka = android.content.Intent(android.content.Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(android.content.Intent.EXTRA_SUBJECT, rubrik)
                .putExtra(android.content.Intent.EXTRA_TEXT, text)
            context.startActivity(android.content.Intent.createChooser(skicka, delaRubrik))
        },
        modifier = Modifier.tryckyta48(),
    ) {
        Text(delaRubrik)
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(rader) { rad ->
            VeckaRad(rad, locale, onClick = { onDagClick(rad.datum) })
        }
    }
}

@Composable
private fun VeckaRad(rad: VeckaDagRad, locale: Locale, onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp)
                .tryckyta48(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(formateraVeckodagOchDatum(rad.datum, locale), style = MaterialTheme.typography.bodyLarge)
            helgdagPa(rad.datum, medAftnar = true)?.let { helg ->
                Text(
                    text = helgdagNamn(helg),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            val tidText =
                when {
                    rad.dag?.start != null && rad.dag.slut != null ->
                        "${formateraTid(rad.dag.start, locale)}–${formateraTid(rad.dag.slut, locale)}"
                    rad.dag?.start != null -> formateraTid(rad.dag.start, locale)
                    rad.dag != null && rad.dag.typ != DagTyp.ARBETE -> stringResource(dagTypStringRes(rad.dag.typ))
                    else -> "—"
                }
            Text(tidText, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            formateraSaldoMinuter(rad.utfallMin, locale),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
internal fun InstallningarInnehall(
    installning: se.tmconnect.flextid.data.Installning,
    exportArManad: YearMonth,
    temaLage: TemaLage,
    locale: Locale,
    arPro: Boolean,
    antalDagar: Int,
    stamplaUtPaminnelse: Boolean,
    stamplaInPaminnelse: Boolean,
    stamplaInPaminnelseMinEfterMidnatt: Int,
    behorighetNekad: Boolean,
    onVisaProDialog: () -> Unit,
    onStamplaUtPaminnelse: (Boolean) -> Unit,
    onStamplaInPaminnelse: (Boolean) -> Unit,
    onStamplaInPaminnelseKlockslag: (Int) -> Unit,
    onSpara: (String, String, String, Int) -> Unit,
    onTema: (TemaLage) -> Unit,
    onExportManad: (YearMonth) -> Unit,
    onExporteraCsv: () -> Unit,
    onExporteraArsCsv: () -> Unit = {},
    onExporteraPdf: () -> Unit,
    onExporteraJson: () -> Unit,
    onImporteraJson: () -> Unit,
    aftnarLediga: Boolean = true,
    semesterdagarPerAr: Int = se.tmconnect.flextid.regler.SEMESTERDAGAR_STANDARD,
    semesterarKalenderar: Boolean = false,
    onAftnarLediga: (Boolean) -> Unit = {},
    onSemesterdagarPerAr: (String) -> Unit = {},
    onSemesterarKalenderar: (Boolean) -> Unit = {},
    saldoTakMin: Int? = null,
    saldoGolvMin: Int? = null,
    onSaldoGranser: (String, String) -> Unit = { _, _ -> },
) {
    var takText by remember(saldoTakMin) { mutableStateOf(saldoTakMin?.let { formateraMinuterSomTid(it, locale) } ?: "") }
    var golvText by remember(saldoGolvMin) { mutableStateOf(saldoGolvMin?.let { formateraMinuterSomTid(-it, locale) } ?: "") }
    var semesterdagarText by remember(semesterdagarPerAr) { mutableStateOf(semesterdagarPerAr.toString()) }
    var normaltid by remember(installning.normaltidMinPerDag) {
        mutableStateOf(installning.normaltidMinPerDag.toString())
    }
    var rast by remember(installning.standardRastMin) {
        mutableStateOf(installning.standardRastMin.toString())
    }
    var ingang by remember(installning.ingangssaldoMin) {
        mutableStateOf(formateraMinuterSomTid(installning.ingangssaldoMin, locale))
    }
    var mask by remember(installning.arbetsdagarMask) { mutableIntStateOf(installning.arbetsdagarMask) }
    var exportAr by remember(exportArManad) { mutableIntStateOf(exportArManad.year) }
    var exportManad by remember(exportArManad) { mutableIntStateOf(exportArManad.monthValue) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(stringResource(R.string.installningar_rubrik), style = MaterialTheme.typography.titleLarge)
        }
        item {
            ProInstallningRad(arPro = arPro, onVisaProDialog = onVisaProDialog)
        }
        if (!arPro && antalDagar >= se.tmconnect.flextid.regler.GRATIS_MAX_DAGPOSTER) {
            item {
                Text(
                    text = stringResource(R.string.pro_grans_forklaring),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        item {
            Text(stringResource(R.string.paminnelse_rubrik), style = MaterialTheme.typography.titleMedium)
        }
        item {
            PaminnelseReglage(
                rubrik = stringResource(R.string.paminnelse_stampla_ut_rubrik),
                beskrivning = stringResource(R.string.paminnelse_stampla_ut_beskrivning),
                pa = stamplaUtPaminnelse,
                onAndras = onStamplaUtPaminnelse,
            )
        }
        item {
            PaminnelseReglage(
                rubrik = stringResource(R.string.paminnelse_stampla_in_rubrik),
                beskrivning = stringResource(R.string.paminnelse_stampla_in_beskrivning),
                pa = stamplaInPaminnelse,
                onAndras = onStamplaInPaminnelse,
            )
        }
        item {
            StamplaInPaminnelseKlockslagVal(
                valtMinEfterMidnatt = stamplaInPaminnelseMinEfterMidnatt,
                locale = locale,
                onValj = onStamplaInPaminnelseKlockslag,
            )
        }
        item {
            PaminnelseReglage(
                rubrik = stringResource(R.string.aftnar_lediga_rubrik),
                beskrivning = stringResource(R.string.aftnar_lediga_beskrivning),
                pa = aftnarLediga,
                onAndras = onAftnarLediga,
            )
        }
        if (se.tmconnect.flextid.ruta.kanBeOmRuta()) {
            item {
                val context = androidx.compose.ui.platform.LocalContext.current
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                se.tmconnect.flextid.ruta.beOmAttLaggaTillRuta(context)
                            }
                        },
                        modifier = Modifier.tryckyta48(),
                    ) {
                        Text(stringResource(R.string.ruta_lagg_till))
                    }
                    Text(
                        text = stringResource(R.string.ruta_lagg_till_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            Text(stringResource(R.string.saldo_granser_rubrik), style = MaterialTheme.typography.titleMedium)
        }
        item {
            OutlinedTextField(
                value = takText,
                onValueChange = {
                    takText = it
                    onSaldoGranser(takText, golvText)
                },
                label = { Text(stringResource(R.string.saldo_tak)) },
                supportingText = { Text(stringResource(R.string.saldo_grans_hjalp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = golvText,
                onValueChange = {
                    golvText = it
                    onSaldoGranser(takText, golvText)
                },
                label = { Text(stringResource(R.string.saldo_golv)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Text(stringResource(R.string.semester_rubrik), style = MaterialTheme.typography.titleMedium)
        }
        item {
            OutlinedTextField(
                value = semesterdagarText,
                onValueChange = {
                    semesterdagarText = it
                    onSemesterdagarPerAr(it)
                },
                label = { Text(stringResource(R.string.semesterdagar_per_ar)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            PaminnelseReglage(
                rubrik = stringResource(R.string.semesterar_kalenderar_rubrik),
                beskrivning = stringResource(R.string.semesterar_kalenderar_beskrivning),
                pa = semesterarKalenderar,
                onAndras = onSemesterarKalenderar,
            )
        }
        if (behorighetNekad) {
            item {
                Text(
                    text = stringResource(R.string.paminnelse_behorighet_nekad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        item {
            OutlinedTextField(
                value = normaltid,
                onValueChange = { normaltid = it },
                label = { Text(stringResource(R.string.normaltid_min)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
        item {
            OutlinedTextField(
                value = rast,
                onValueChange = { rast = it },
                label = { Text(stringResource(R.string.standard_rast_min)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
        item {
            OutlinedTextField(
                value = ingang,
                onValueChange = { ingang = it },
                label = { Text(stringResource(R.string.ingangssaldo)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
        item {
            Text(stringResource(R.string.arbetsdagar_rubrik), style = MaterialTheme.typography.titleMedium)
            DayOfWeek.entries.forEach { dag ->
                val bit = veckodagBit(dag)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = mask and bit != 0,
                        onCheckedChange = { checked ->
                            mask =
                                if (checked) {
                                    mask or bit
                                } else {
                                    mask and bit.inv()
                                }
                        },
                    )
                    Text(stringResource(veckodagStringRes(dag)))
                }
            }
        }
        item {
            Button(
                onClick = { onSpara(normaltid, rast, ingang, mask) },
                modifier = Modifier.tryckyta48(),
            ) {
                Text(stringResource(R.string.spara))
            }
        }
        item {
            Text(stringResource(R.string.tema_rubrik), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TemaLage.entries.forEach { lage ->
                    FilterChip(
                        selected = temaLage == lage,
                        onClick = { onTema(lage) },
                        label = { Text(stringResource(temaStringRes(lage))) },
                    )
                }
            }
        }
        item {
            Text(stringResource(R.string.export_rubrik), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = exportAr.toString(),
                    onValueChange = { it.toIntOrNull()?.let { ar -> exportAr = ar } },
                    label = { Text(stringResource(R.string.export_ar)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = exportManad.toString(),
                    onValueChange = { it.toIntOrNull()?.let { m -> exportManad = m } },
                    label = { Text(stringResource(R.string.export_manad)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onExportManad(YearMonth.of(exportAr, exportManad.coerceIn(1, 12)))
                        onExporteraCsv()
                    },
                    modifier = Modifier.tryckyta48().weight(1f),
                ) {
                    Text(stringResource(R.string.exportera_csv))
                }
                Button(
                    onClick = {
                        onExportManad(YearMonth.of(exportAr, exportManad.coerceIn(1, 12)))
                        onExporteraPdf()
                    },
                    modifier = Modifier.tryckyta48().weight(1f),
                ) {
                    Text(stringResource(R.string.exportera_pdf))
                }
            }
            OutlinedButton(
                onClick = {
                    onExportManad(YearMonth.of(exportAr, exportManad.coerceIn(1, 12)))
                    onExporteraArsCsv()
                },
                modifier = Modifier.tryckyta48().fillMaxWidth(),
            ) {
                Text(stringResource(R.string.exportera_ars_csv))
            }
            OutlinedButton(onClick = onExporteraJson, modifier = Modifier.tryckyta48()) {
                Text(stringResource(R.string.exportera_sakerhetskopia))
            }
            OutlinedButton(onClick = onImporteraJson, modifier = Modifier.tryckyta48()) {
                Text(stringResource(R.string.importera_sakerhetskopia))
            }
        }
        item { Spacer(modifier = Modifier.padding(bottom = 24.dp)) }
    }
}

@Composable
internal fun DagFormularDialog(
    tillstand: DagFormularTillstand,
    locale: Locale,
    onAndra: (DagFormularTillstand) -> Unit,
    onSpara: () -> Unit,
    onTaBort: () -> Unit,
    onStang: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onStang,
        title = { Text(formateraDatum(tillstand.datum, locale)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val forslagDatum = tillstand.forslagDatum
                if (forslagDatum != null && tillstand.typ == DagTyp.ARBETE && tillstand.startText.isBlank() && tillstand.slutText.isBlank()) {
                    TextButton(
                        onClick = {
                            onAndra(
                                tillstand.copy(
                                    startText = tillstand.forslagStart,
                                    slutText = tillstand.forslagSlut,
                                    rastText = tillstand.forslagRast,
                                    fel = null,
                                ),
                            )
                        },
                        modifier = Modifier.heightIn(min = 48.dp),
                    ) {
                        Text(
                            stringResource(
                                R.string.samma_tider_som,
                                formateraDatum(forslagDatum, locale),
                                tillstand.forslagStart,
                                tillstand.forslagSlut,
                            ),
                        )
                    }
                }
                OutlinedTextField(
                    value = tillstand.startText,
                    onValueChange = { onAndra(tillstand.copy(startText = it, fel = null)) },
                    label = { Text(stringResource(R.string.start_tid)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = tillstand.slutText,
                    onValueChange = { onAndra(tillstand.copy(slutText = it, fel = null)) },
                    label = { Text(stringResource(R.string.slut_tid)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = tillstand.rastText,
                    onValueChange = { onAndra(tillstand.copy(rastText = it)) },
                    label = { Text(stringResource(R.string.rast_min)) },
                    singleLine = true,
                )
                Text(stringResource(R.string.dagtyp_rubrik))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DagTyp.entries.forEach { typ ->
                        FilterChip(
                            selected = tillstand.typ == typ,
                            onClick = { onAndra(tillstand.copy(typ = typ)) },
                            label = { Text(stringResource(dagTypStringRes(typ))) },
                        )
                    }
                }
                OutlinedTextField(
                    value = tillstand.anteckning,
                    onValueChange = { onAndra(tillstand.copy(anteckning = it)) },
                    label = { Text(stringResource(R.string.anteckning)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                tillstand.fel?.let {
                    Text(
                        stringResource(R.string.fel_slut_fore_start),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSpara) { Text(stringResource(R.string.spara)) }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onTaBort) { Text(stringResource(R.string.ta_bort)) }
                TextButton(onClick = onStang) { Text(stringResource(R.string.avbryt)) }
            }
        },
    )
}

@Composable
private fun ProInstallningRad(
    arPro: Boolean,
    onVisaProDialog: () -> Unit,
) {
    if (arPro) {
        Text(
            text = stringResource(R.string.pro_du_har),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
    } else {
        TextButton(
            onClick = onVisaProDialog,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(text = stringResource(R.string.pro_las_upp))
        }
    }
}

@Composable
private fun StamplaInPaminnelseKlockslagVal(
    valtMinEfterMidnatt: Int,
    locale: Locale,
    onValj: (Int) -> Unit,
) {
    var expanderad by remember { mutableStateOf(false) }
    val alternativ = remember { giltigaStamplaInPaminnelseMinEfterMidnatt() }
    val valdText =
        formateraTid(minuterTillLocalTime(valtMinEfterMidnatt), locale)
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanderad = true },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .tryckyta48(),
        ) {
            Text(
                text =
                    stringResource(
                        R.string.paminnelse_stampla_in_klockslag_val,
                        valdText,
                    ),
            )
        }
        DropdownMenu(
            expanded = expanderad,
            onDismissRequest = { expanderad = false },
        ) {
            alternativ.forEach { min ->
                DropdownMenuItem(
                    text = { Text(formateraTid(minuterTillLocalTime(min), locale)) },
                    onClick = {
                        onValj(min)
                        expanderad = false
                    },
                    modifier = Modifier.tryckyta48(),
                )
            }
        }
    }
}

@Composable
private fun PaminnelseReglage(
    rubrik: String,
    beskrivning: String,
    pa: Boolean,
    onAndras: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = rubrik, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = beskrivning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = pa,
            onCheckedChange = onAndras,
            modifier = Modifier.tryckyta48(),
        )
    }
}

private fun veckodagStringRes(dag: DayOfWeek): Int =
    when (dag) {
        DayOfWeek.MONDAY -> R.string.veckodag_man
        DayOfWeek.TUESDAY -> R.string.veckodag_tis
        DayOfWeek.WEDNESDAY -> R.string.veckodag_ons
        DayOfWeek.THURSDAY -> R.string.veckodag_tor
        DayOfWeek.FRIDAY -> R.string.veckodag_fre
        DayOfWeek.SATURDAY -> R.string.veckodag_lor
        DayOfWeek.SUNDAY -> R.string.veckodag_son
    }

private fun dagTypStringRes(typ: DagTyp): Int =
    when (typ) {
        DagTyp.ARBETE -> R.string.dagtyp_arbete
        DagTyp.LEDIG -> R.string.dagtyp_ledig
        DagTyp.SEMESTER -> R.string.dagtyp_semester
        DagTyp.SJUK -> R.string.dagtyp_sjuk
        DagTyp.HELGDAG -> R.string.dagtyp_helgdag
    }

private fun temaStringRes(lage: TemaLage): Int =
    when (lage) {
        TemaLage.SYSTEM -> R.string.tema_system
        TemaLage.LJUST -> R.string.tema_ljust
        TemaLage.MORKT -> R.string.tema_morkt
    }

/** "Måndag 12 oktober" eller "Monday 12 October": veckodagen är det man letar efter i veckovyn. */
internal fun formateraVeckodagOchDatum(datum: LocalDate, locale: Locale): String =
    datum.format(java.time.format.DateTimeFormatter.ofPattern("EEEE d MMMM", locale))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
