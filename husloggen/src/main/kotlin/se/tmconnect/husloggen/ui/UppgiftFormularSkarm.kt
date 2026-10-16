package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.regler.IntervallBestamdAv
import se.tmconnect.husloggen.regler.UppgiftFel
import se.tmconnect.husloggen.regler.UPPGIFTSFORSLAG
import se.tmconnect.karna.format.formateraDatum

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UppgiftFormularSkarm(
    tillstand: UppgiftFormularTillstand,
    onNamnAndras: (String) -> Unit,
    onIntervallAndras: (String) -> Unit,
    onStartdatumAndras: (java.time.LocalDate) -> Unit,
    onPaminnelseAndras: (Boolean) -> Unit,
    onAnteckningAndras: (String) -> Unit,
    onValjForslag: (namn: String, intervallManader: Int) -> Unit,
    onSpara: () -> Unit,
    onTaBort: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    var visarDatumvaljare by remember { mutableStateOf(false) }
    var visarTaBort by remember { mutableStateOf(false) }
    var senasteBestamdAv by remember { mutableStateOf<IntervallBestamdAv?>(null) }
    val datumValjare = rememberDatePickerState(
        initialSelectedDateMillis = tillstand.startDatum.toEpochDay() * 86_400_000,
    )

    if (visarDatumvaljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumvaljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumValjare.selectedDateMillis?.let { millis ->
                            onStartdatumAndras(java.time.LocalDate.ofEpochDay(millis / 86_400_000))
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

    if (visarTaBort) {
        AlertDialog(
            onDismissRequest = { visarTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_uppgift_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_uppgift_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTaBort()
                        visarTaBort = false
                    },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.ja_ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarTaBort = false }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.nej))
                }
            },
        )
    }

    val titel = if (tillstand.redigerarId == 0L) {
        stringResource(R.string.ny_uppgift_rubrik)
    } else {
        stringResource(R.string.andra_uppgift_rubrik)
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
                actions = {
                    TextButton(onClick = onSpara, modifier = Modifier.tryckyta48()) {
                        Text(text = stringResource(R.string.spara))
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.forslag_rubrik),
                style = MaterialTheme.typography.titleSmall,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UPPGIFTSFORSLAG.forEach { forslag ->
                    val forslagNamn = stringResource(forslag.namnResId)
                    FilterChip(
                        selected = false,
                        onClick = {
                            senasteBestamdAv = forslag.bestamdAv
                            onValjForslag(forslagNamn, forslag.intervallManader)
                        },
                        label = { Text(text = forslagNamn) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
            }
            when (senasteBestamdAv) {
                IntervallBestamdAv.KOMMUNEN -> {
                    Text(
                        text = stringResource(R.string.enligt_kommunen),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IntervallBestamdAv.TILLVERKAREN -> {
                    Text(
                        text = stringResource(R.string.enligt_tillverkaren),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                else -> Unit
            }

            OutlinedTextField(
                value = tillstand.namn,
                onValueChange = onNamnAndras,
                label = { Text(text = stringResource(R.string.falt_namn)) },
                isError = tillstand.valideringsFel == UppgiftFel.NAMN_SAKNAS,
                supportingText = if (tillstand.valideringsFel == UppgiftFel.NAMN_SAKNAS) {
                    { Text(text = stringResource(R.string.fel_namn_saknas)) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = tillstand.intervallText,
                onValueChange = onIntervallAndras,
                label = { Text(text = stringResource(R.string.falt_intervall_manader)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = tillstand.valideringsFel == UppgiftFel.INTERVALL_SAKNAS ||
                    tillstand.valideringsFel == UppgiftFel.INTERVALL_UTANFOR,
                supportingText = when (tillstand.valideringsFel) {
                    UppgiftFel.INTERVALL_SAKNAS -> {
                        { Text(text = stringResource(R.string.fel_intervall_saknas)) }
                    }
                    UppgiftFel.INTERVALL_UTANFOR -> {
                        { Text(text = stringResource(R.string.fel_intervall_ogiltigt)) }
                    }
                    else -> null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedButton(
                onClick = { visarDatumvaljare = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .tryckyta48(),
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
                Text(
                    text = "${stringResource(R.string.falt_startdatum)}: ${formateraDatum(tillstand.startDatum, locale)}",
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            RowPaminnelse(
                paminnelse = tillstand.paminnelse,
                onPaminnelseAndras = onPaminnelseAndras,
            )

            OutlinedTextField(
                value = tillstand.anteckning,
                onValueChange = onAnteckningAndras,
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            if (tillstand.redigerarId != 0L) {
                OutlinedButton(
                    onClick = { visarTaBort = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.ta_bort))
                }
            }
        }
    }
}

@Composable
private fun RowPaminnelse(
    paminnelse: Boolean,
    onPaminnelseAndras: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tryckyta48(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.falt_paminnelse),
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = paminnelse,
            onCheckedChange = onPaminnelseAndras,
        )
    }
    Text(
        text = stringResource(if (paminnelse) R.string.paminnelse_pa else R.string.paminnelse_av),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
