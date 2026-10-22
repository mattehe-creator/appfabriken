package se.tmconnect.husloggen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Avlasning
import se.tmconnect.husloggen.data.Matare
import se.tmconnect.husloggen.regler.AvlasningFel
import se.tmconnect.husloggen.regler.MatareFel
import se.tmconnect.husloggen.regler.beraknaArsforbrukning
import se.tmconnect.husloggen.regler.jamforArsforbrukning
import se.tmconnect.husloggen.regler.arsprognos
import se.tmconnect.husloggen.regler.beraknaAvlasningarMedForbrukning
import se.tmconnect.husloggen.regler.forbrukningKostnadOre
import se.tmconnect.husloggen.regler.formateraMatareVarde
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr

@Composable
fun MatareFlik(
    matare: List<Matare>,
    contentPadding: PaddingValues,
    onNyMatare: () -> Unit,
    onOppnaMatare: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (matare.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.tom_matare),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            OutlinedButton(
                onClick = onNyMatare,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.fab_ny_matare))
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(matare, key = { it.id }) { rad ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOppnaMatare(rad.id) }
                        .tryckyta48()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = rad.namn, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = stringResource(R.string.matare_enhet_etikett, rad.enhet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatareDetaljSkarm(
    matare: Matare,
    avlasningar: List<Avlasning>,
    locale: Locale,
    idag: LocalDate,
    onTillbaka: () -> Unit,
    onAndra: () -> Unit,
    onNyAvlasning: () -> Unit,
    onExportCsv: () -> Unit,
    onTaBortMatare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val medForbrukning = remember(avlasningar) { beraknaAvlasningarMedForbrukning(avlasningar) }
    val arsSumma = remember(medForbrukning) { beraknaArsforbrukning(medForbrukning) }
    val prognos = remember(avlasningar, idag) { arsprognos(avlasningar, idag.year, idag) }
    var visarTaBort by remember { mutableStateOf(false) }

    if (visarTaBort) {
        AlertDialog(
            onDismissRequest = { visarTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_matare_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_matare_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        visarTaBort = false
                        onTaBortMatare()
                    },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarTaBort = false }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.avbryt))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = matare.namn) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onExportCsv, modifier = Modifier.tryckyta48()) {
                        Text(text = stringResource(R.string.export_avlasning_csv))
                    }
                },
            )
        },
        floatingActionButton = {
            androidx.compose.material3.ExtendedFloatingActionButton(
                onClick = onNyAvlasning,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.fab_ny_avlasning),
                    )
                },
                text = { Text(text = stringResource(R.string.fab_ny_avlasning)) },
                modifier = Modifier.tryckyta48(),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.matare_enhet_etikett, matare.enhet),
                    style = MaterialTheme.typography.bodyLarge,
                )
                matare.prisOrePerEnhet?.let { pris ->
                    Text(
                        text = stringResource(
                            R.string.matare_pris_etikett,
                            pris.toString(),
                            matare.enhet,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                TextButton(onClick = onAndra, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.andra_matare))
                }
                matare.anteckning?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                TextButton(onClick = { visarTaBort = true }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.ta_bort_matare))
                }
            }
            if (prognos != null) {
                item {
                    Text(
                        text = stringResource(
                            R.string.matare_prognos,
                            idag.year.toString(),
                            formateraMatareVarde(prognos),
                            matare.enhet,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            if (arsSumma.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.matare_arsforbrukning_rubrik),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    jamforArsforbrukning(arsSumma).forEach { rad ->
                        val bas = stringResource(
                            R.string.matare_arsforbrukning_rad,
                            rad.ar.toString(),
                            formateraMatareVarde(rad.forbrukning),
                            matare.enhet,
                        )
                        val forandring = rad.forandringTiondelsProcent?.let { t ->
                            val tecken = if (t > 0) "+" else if (t < 0) "\u2212" else ""
                            val a = kotlin.math.abs(t)
                            stringResource(R.string.matare_forandring, "$tecken${a / 10},${a % 10}")
                        }
                        Text(
                            text = if (forandring != null) "$bas $forandring" else bas,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.matare_avlasningar_rubrik),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (medForbrukning.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.tom_avlasningar),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(medForbrukning.reversed(), key = { it.avlasning.id }) { rad ->
                    AvlasningRad(rad, matare.enhet, matare.prisOrePerEnhet, locale)
                }
            }
        }
    }
}

@Composable
private fun AvlasningRad(
    rad: se.tmconnect.husloggen.regler.AvlasningMedForbrukning,
    enhet: String,
    prisOrePerEnhet: Long?,
    locale: Locale,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = formateraDatum(rad.avlasning.datum, locale),
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = stringResource(
                R.string.avlasning_varde_rad,
                formateraMatareVarde(rad.avlasning.varde),
                enhet,
            ),
            style = MaterialTheme.typography.bodyLarge,
        )
        rad.periodForbrukning?.let { forbrukning ->
            val kostnadOre =
                prisOrePerEnhet?.let { forbrukningKostnadOre(forbrukning, it) }
            Text(
                text = buildString {
                    append(
                        stringResource(
                            R.string.avlasning_forbrukning_rad,
                            formateraMatareVarde(forbrukning),
                            enhet,
                        ),
                    )
                    kostnadOre?.let { ore ->
                        append(' ')
                        append(
                            stringResource(
                                R.string.avlasning_forbrukning_kostnad_rad,
                                formateraPrisKr(ore, locale),
                            ),
                        )
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatareFormularSkarm(
    tillstand: MatareFormularTillstand,
    onNamnAndras: (String) -> Unit,
    onEnhetAndras: (String) -> Unit,
    onPrisAndras: (String) -> Unit,
    onAnteckningAndras: (String) -> Unit,
    onValjForslag: (String, String) -> Unit,
    onSpara: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val forslag =
        listOf(
            stringResource(R.string.matare_forslag_el) to stringResource(R.string.matare_enhet_kwh),
            stringResource(R.string.matare_forslag_vatten) to stringResource(R.string.matare_enhet_kubik),
            stringResource(R.string.matare_forslag_fjarrvarme) to stringResource(R.string.matare_enhet_kwh),
            stringResource(R.string.matare_forslag_olja) to stringResource(R.string.matare_enhet_liter),
            stringResource(R.string.matare_forslag_pellets) to stringResource(R.string.matare_enhet_kg),
        )

    val titel = if (tillstand.redigerarId == 0L) {
        stringResource(R.string.ny_matare_rubrik)
    } else {
        stringResource(R.string.andra_matare_rubrik)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = titel) },
                navigationIcon = {
                    IconButton(onClick = onStang, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.stang),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = stringResource(R.string.matare_forslag_rubrik), style = MaterialTheme.typography.titleSmall)
            forslag.forEach { (namn, enhet) ->
                TextButton(
                    onClick = { onValjForslag(namn, enhet) },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.matare_forslag_knapp, namn, enhet))
                }
            }
            OutlinedTextField(
                value = tillstand.namn,
                onValueChange = onNamnAndras,
                label = { Text(text = stringResource(R.string.falt_namn)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel == MatareFel.NamnSaknas,
                supportingText = {
                    if (tillstand.valideringsFel == MatareFel.NamnSaknas) {
                        Text(text = stringResource(R.string.fel_matare_namn_saknas))
                    }
                },
            )
            OutlinedTextField(
                value = tillstand.enhet,
                onValueChange = onEnhetAndras,
                label = { Text(text = stringResource(R.string.falt_enhet)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel == MatareFel.EnhetSaknas,
                supportingText = {
                    if (tillstand.valideringsFel == MatareFel.EnhetSaknas) {
                        Text(text = stringResource(R.string.fel_matare_enhet_saknas))
                    }
                },
            )
            OutlinedTextField(
                value = tillstand.prisOrePerEnhetText,
                onValueChange = onPrisAndras,
                label = { Text(text = stringResource(R.string.falt_matare_pris)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel == MatareFel.PrisOgiltigt,
                supportingText = {
                    when (tillstand.valideringsFel) {
                        MatareFel.PrisOgiltigt ->
                            Text(text = stringResource(R.string.fel_matare_pris_ogiltigt))
                        else -> Text(text = stringResource(R.string.falt_matare_pris_hjalp))
                    }
                },
            )
            OutlinedTextField(
                value = tillstand.anteckning,
                onValueChange = onAnteckningAndras,
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = onSpara, modifier = Modifier.tryckyta48()) {
                Text(text = stringResource(R.string.spara))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvlasningFormularSkarm(
    tillstand: AvlasningFormularTillstand,
    enhet: String,
    idag: LocalDate,
    onDatumAndras: (LocalDate) -> Unit,
    onVardeAndras: (String) -> Unit,
    onSpara: () -> Unit,
    onBekraftaLagreVarde: () -> Unit,
    onAvbrytLagreVarde: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visarDatumvaljare by remember { mutableStateOf(false) }
    val selectableDates = remember(idag) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val datum = LocalDate.ofEpochDay(utcTimeMillis / 86_400_000)
                return !datum.isAfter(idag)
            }
        }
    }
    val datumValjare = rememberDatePickerState(
        initialSelectedDateMillis = tillstand.datum.toEpochDay() * 86_400_000,
        selectableDates = selectableDates,
    )

    if (tillstand.visarLagreVardeBekraftelse) {
        AlertDialog(
            onDismissRequest = onAvbrytLagreVarde,
            title = { Text(text = stringResource(R.string.avlasning_lagre_varde_titel)) },
            text = { Text(text = stringResource(R.string.avlasning_lagre_varde_text)) },
            confirmButton = {
                Button(onClick = onBekraftaLagreVarde, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.spara_anda))
                }
            },
            dismissButton = {
                TextButton(onClick = onAvbrytLagreVarde, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.avbryt))
                }
            },
        )
    }

    if (visarDatumvaljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumvaljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumValjare.selectedDateMillis?.let { millis ->
                            onDatumAndras(LocalDate.ofEpochDay(millis / 86_400_000))
                        }
                        visarDatumvaljare = false
                    },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.spara))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarDatumvaljare = false }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.avbryt))
                }
            },
        ) {
            DatePicker(state = datumValjare)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.ny_avlasning_rubrik)) },
                navigationIcon = {
                    IconButton(onClick = onStang, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.stang),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = { visarDatumvaljare = true },
                modifier = Modifier.tryckyta48(),
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
                Text(
                    text = formateraDatum(tillstand.datum, LocalConfiguration.current.locales[0]),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            OutlinedTextField(
                value = tillstand.vardeText,
                onValueChange = onVardeAndras,
                label = { Text(text = stringResource(R.string.falt_avlasning_varde)) },
                supportingText = { Text(text = stringResource(R.string.falt_avlasning_varde_hjalp, enhet)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel != null,
            )
            when (tillstand.valideringsFel) {
                AvlasningFel.VardeOgiltigt ->
                    Text(
                        text = stringResource(R.string.fel_avlasning_varde),
                        color = MaterialTheme.colorScheme.error,
                    )
                AvlasningFel.DatumRedanAnvant ->
                    Text(
                        text = stringResource(R.string.fel_avlasning_datum),
                        color = MaterialTheme.colorScheme.error,
                    )
                null -> Unit
            }
            Button(onClick = onSpara, modifier = Modifier.tryckyta48()) {
                Text(text = stringResource(R.string.spara))
            }
        }
    }
}
