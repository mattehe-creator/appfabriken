package se.tmconnect.flextid.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.formateraMinuterSomTid
import se.tmconnect.flextid.regler.formateraSaldoMinuter
import se.tmconnect.flextid.regler.formateraTid
import se.tmconnect.flextid.regler.veckodagBit
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.tema.TemaLage

enum class FlextidFlik { IDAG, VECKA, INSTALLNINGAR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlextidStartSkarm(
    viewModel: FlextidViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onExporteraCsv: () -> Unit,
    onExporteraSakerhetskopia: () -> Unit,
    onImporteraSakerhetskopia: () -> Unit,
    locale: Locale,
) {
    var flik by remember { mutableStateOf(FlextidFlik.IDAG) }
    val saldo by viewModel.saldo.collectAsState()
    val installning by viewModel.installning.collectAsState()
    val idagDag by viewModel.idagDag.collectAsState()
    val veckaRader by viewModel.veckaRader.collectAsState()
    val veckaAnkar by viewModel.veckaAnkar.collectAsState()
    val dagFormular by viewModel.dagFormular.collectAsState()
    val exportArManad by viewModel.exportArManad.collectAsState()
    val temaLage by viewModel.temaLage.collectAsState()

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
                        onSpara = viewModel::sparaInstallning,
                        onTema = viewModel::sattTemaLage,
                        onExportManad = viewModel::sattExportArManad,
                        onExporteraCsv = onExporteraCsv,
                        onExporteraJson = onExporteraSakerhetskopia,
                        onImporteraJson = onImporteraSakerhetskopia,
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
private fun IdagInnehall(
    saldo: Int,
    dag: se.tmconnect.flextid.data.Dag?,
    standardRast: Int,
    locale: Locale,
    onStamplaIn: () -> Unit,
    onStamplaUt: () -> Unit,
    onRastChange: (Int) -> Unit,
    onRedigera: () -> Unit,
) {
    var rastText by remember(dag?.rastMin) { mutableStateOf((dag?.rastMin ?: standardRast).toString()) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.saldo_rubrik, formateraSaldoMinuter(saldo, locale)),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = formateraDatum(LocalDate.now(), locale),
            style = MaterialTheme.typography.bodyLarge,
        )
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
    }
}

@Composable
private fun VeckaInnehall(
    veckaAnkar: LocalDate,
    rader: List<VeckaDagRad>,
    locale: Locale,
    onForegaende: () -> Unit,
    onNasta: () -> Unit,
    onDennaVecka: () -> Unit,
    onDagClick: (LocalDate) -> Unit,
) {
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
            Text(formateraDatum(rad.datum, locale), style = MaterialTheme.typography.bodyLarge)
            val tidText =
                when {
                    rad.dag?.start != null && rad.dag.slut != null ->
                        "${formateraTid(rad.dag.start, locale)}–${formateraTid(rad.dag.slut, locale)}"
                    rad.dag?.start != null -> formateraTid(rad.dag.start, locale)
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
private fun InstallningarInnehall(
    installning: se.tmconnect.flextid.data.Installning,
    exportArManad: YearMonth,
    temaLage: TemaLage,
    locale: Locale,
    onSpara: (String, String, String, Int) -> Unit,
    onTema: (TemaLage) -> Unit,
    onExportManad: (YearMonth) -> Unit,
    onExporteraCsv: () -> Unit,
    onExporteraJson: () -> Unit,
    onImporteraJson: () -> Unit,
) {
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
            Button(
                onClick = {
                    onExportManad(YearMonth.of(exportAr, exportManad.coerceIn(1, 12)))
                    onExporteraCsv()
                },
                modifier = Modifier.tryckyta48(),
            ) {
                Text(stringResource(R.string.exportera_csv))
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
private fun DagFormularDialog(
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
