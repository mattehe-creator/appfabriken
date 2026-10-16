package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.PostFel
import se.tmconnect.karna.format.formateraDatum

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostFormularSkarm(
    tillstand: PostFormularTillstand,
    uppgifter: List<Uppgift>,
    idag: LocalDate,
    onVadAndras: (String) -> Unit,
    onDatumAndras: (LocalDate) -> Unit,
    onTypAndras: (PostTyp) -> Unit,
    onBeloppAndras: (String) -> Unit,
    onUtforareAndras: (String) -> Unit,
    onUppgiftAndras: (Long?) -> Unit,
    onAnteckningAndras: (String) -> Unit,
    onSpara: () -> Unit,
    onTaBort: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    var visarDatumvaljare by remember { mutableStateOf(false) }
    var visarTaBort by remember { mutableStateOf(false) }
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

    if (visarTaBort) {
        AlertDialog(
            onDismissRequest = { visarTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_post_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_post_text)) },
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
        stringResource(R.string.ny_post_rubrik)
    } else {
        stringResource(R.string.andra_post_rubrik)
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
            PostVadFalt(
                vad = tillstand.vad,
                onVadAndras = onVadAndras,
                fel = tillstand.valideringsFel,
            )

            PostDatumFalt(
                datum = tillstand.datum,
                onValjDatum = { visarDatumvaljare = true },
                locale = locale,
                fel = tillstand.valideringsFel,
            )

            PostTypValjare(
                typ = tillstand.typ,
                onTypAndras = onTypAndras,
            )

            PostBeloppFalt(
                beloppText = tillstand.beloppText,
                onBeloppAndras = onBeloppAndras,
                fel = tillstand.valideringsFel,
            )

            PostUtforareFalt(
                utforare = tillstand.utforare,
                onUtforareAndras = onUtforareAndras,
            )

            PostUppgiftValjare(
                uppgifter = uppgifter,
                valdUppgiftId = tillstand.uppgiftId,
                onValjUppgift = onUppgiftAndras,
            )

            PostAnteckningFalt(
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

@Composable
private fun PostUppgiftMeny(
    uppgifter: List<Uppgift>,
    valdUppgiftId: Long?,
    onValj: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanderad by remember { mutableStateOf(false) }
    val valdText = uppgifter.find { it.id == valdUppgiftId }?.namn
        ?: stringResource(R.string.ingen_uppgift)

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanderad = true },
            modifier = Modifier
                .fillMaxWidth()
                .tryckyta48(),
        ) {
            Text(
                text = "${stringResource(R.string.falt_uppgift)}: $valdText",
            )
        }
        DropdownMenu(
            expanded = expanderad,
            onDismissRequest = { expanderad = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.ingen_uppgift)) },
                onClick = {
                    onValj(null)
                    expanderad = false
                },
                modifier = Modifier.tryckyta48(),
            )
            uppgifter.forEach { uppgift ->
                DropdownMenuItem(
                    text = { Text(text = uppgift.namn) },
                    onClick = {
                        onValj(uppgift.id)
                        expanderad = false
                    },
                    modifier = Modifier.tryckyta48(),
                )
            }
        }
    }
}

// Funktionen postTypEtikett har tagits bort från PostFormularSkarm.kt för att undvika överlappning
// Den finns nu i PostFormularFalt.kt
