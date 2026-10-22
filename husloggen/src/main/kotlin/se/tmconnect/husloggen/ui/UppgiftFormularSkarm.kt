package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.husloggen.R
import se.tmconnect.karna.format.formateraPrisKr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UppgiftFormularSkarm(
    tillstand: UppgiftFormularTillstand,
    senasteKostnadOre: Long? = null,
    onNamnAndras: (String) -> Unit,
    onIntervallAndras: (String) -> Unit,
    onStartdatumAndras: (LocalDate) -> Unit,
    onPaminnelseAndras: (Boolean) -> Unit,
    onAnteckningAndras: (String) -> Unit,
    onValjForslag: (namn: String, intervallManader: Int) -> Unit,
    onSpara: () -> Unit,
    onTaBort: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val context = LocalContext.current
    var visarDatumvaljare by remember { mutableStateOf(false) }
    var visarTaBort by remember { mutableStateOf(false) }
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
                            onStartdatumAndras(LocalDate.ofEpochDay(millis / 86_400_000))
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
            UppgiftForslagValjare(
                onValj = { forslag ->
                    onValjForslag(
                        context.getString(forslag.namnResId),
                        forslag.intervallManader,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            UppgiftNamnFalt(
                namn = tillstand.namn,
                onNamnAndras = onNamnAndras,
                fel = tillstand.valideringsFel,
            )

            senasteKostnadOre?.let { ore ->
                Text(
                    text = stringResource(
                        R.string.uppgift_senaste_kostnad,
                        formateraPrisKr(ore, locale),
                    ),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )
            }

            UppgiftIntervallFalt(
                intervallText = tillstand.intervallText,
                onIntervallAndras = onIntervallAndras,
                fel = tillstand.valideringsFel,
            )

            UppgiftStartdatumFalt(
                datum = tillstand.startDatum,
                onValjDatum = { visarDatumvaljare = true },
                locale = locale,
            )

            UppgiftPaminnelseFalt(
                paminnelsePa = tillstand.paminnelse,
                onPaminnelseAndras = onPaminnelseAndras,
            )

            UppgiftAnteckningFalt(
                anteckning = tillstand.anteckning,
                onAnteckningAndras = onAnteckningAndras,
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
