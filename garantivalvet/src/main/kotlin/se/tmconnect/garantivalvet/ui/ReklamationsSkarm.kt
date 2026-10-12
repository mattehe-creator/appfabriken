package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.Krav
import se.tmconnect.garantivalvet.regler.ReklamationsTexter
import se.tmconnect.garantivalvet.regler.byggReklamationsbrev
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.karna.tema.AppfabrikKortForm

/** Hjälper användaren skriva ett reklamationsmeddelande till företaget. Alltid gratis. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReklamationsSkarm(
    kop: Kop,
    kvittoFinns: Boolean,
    onSkickaMejl: (amne: String, text: String) -> Unit,
    onSparaFel: (felUpptackt: LocalDate, beskrivning: String) -> Unit,
    onHarReklamerat: (felUpptackt: LocalDate, beskrivning: String) -> Unit,
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    var beskrivning by remember(kop.felBeskrivning) { mutableStateOf(kop.felBeskrivning.orEmpty()) }
    var upptackt by remember(kop.felUpptackt) { mutableStateOf(kop.felUpptackt ?: idag) }
    var krav by remember { mutableStateOf(Krav.AVHJALP) }
    var visarDatumvaljare by remember { mutableStateOf(false) }
    var kopierad by remember { mutableStateOf(false) }
    val datumValjare = rememberDatePickerState(initialSelectedDateMillis = upptackt.toEpochDay() * 86_400_000)
    val clipboard = LocalClipboardManager.current

    val texter = ReklamationsTexter(
        amneMall = stringResource(R.string.brev_amne),
        hej = stringResource(R.string.brev_hej),
        intro = stringResource(R.string.brev_intro),
        vara = stringResource(R.string.brev_vara),
        kopdatum = stringResource(R.string.brev_kopdatum),
        pris = stringResource(R.string.brev_pris),
        kvittoBifogas = stringResource(R.string.brev_kvitto_bifogas),
        fel = stringResource(R.string.brev_fel),
        upptacktes = stringResource(R.string.brev_upptacktes),
        kravMall = stringResource(R.string.brev_krav_mall),
        kravAvhjalp = stringResource(R.string.brev_krav_avhjalp),
        kravPrisavdrag = stringResource(R.string.brev_krav_prisavdrag),
        kravHava = stringResource(R.string.brev_krav_hava),
        avslut = stringResource(R.string.brev_avslut),
        halsning = stringResource(R.string.brev_halsning),
    )
    val brev = byggReklamationsbrev(
        vad = kop.vad,
        kopdatum = kop.kopdatum,
        prisOre = kop.prisOre,
        kvittoFinns = kvittoFinns,
        felBeskrivning = beskrivning.ifBlank { "…" },
        felUpptackt = upptackt,
        krav = krav,
        texter = texter,
        locale = locale,
    )
    val kanSkicka = beskrivning.isNotBlank()

    if (visarDatumvaljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumvaljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumValjare.selectedDateMillis?.let { millis ->
                            val valt = LocalDate.ofEpochDay(millis / 86_400_000)
                            upptackt = if (valt.isAfter(idag)) idag else valt
                        }
                        visarDatumvaljare = false
                    },
                ) {
                    Text(text = stringResource(R.string.spara))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarDatumvaljare = false }) {
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
                title = { Text(text = stringResource(R.string.reklamera_titel)) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.reklamera_info),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = beskrivning,
                onValueChange = {
                    beskrivning = it
                    kopierad = false
                },
                label = { Text(text = stringResource(R.string.reklamera_fel_etikett)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = formateraDatum(upptackt, locale),
                onValueChange = {},
                label = { Text(text = stringResource(R.string.reklamera_upptackt_etikett)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { visarDatumvaljare = true }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = stringResource(R.string.reklamera_valj_datum),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.reklamera_krav_rubrik),
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(
                    Krav.AVHJALP to R.string.reklamera_krav_avhjalp,
                    Krav.PRISAVDRAG to R.string.reklamera_krav_prisavdrag,
                    Krav.HAVA to R.string.reklamera_krav_hava,
                ).forEach { (valtKrav, textRes) ->
                    FilterChip(
                        selected = krav == valtKrav,
                        onClick = {
                            krav = valtKrav
                            kopierad = false
                        },
                        label = { Text(text = stringResource(textRes)) },
                    )
                }
            }
            Text(
                text = stringResource(R.string.reklamera_krav_hjalp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.reklamera_meddelande_rubrik),
                style = MaterialTheme.typography.labelLarge,
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppfabrikKortForm,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = brev.amne, style = MaterialTheme.typography.titleSmall)
                    Text(text = brev.text, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    onSparaFel(upptackt, beskrivning)
                    onSkickaMejl(brev.amne, brev.text)
                },
                enabled = kanSkicka,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.reklamera_skicka_mejl))
            }
            OutlinedButton(
                onClick = {
                    onSparaFel(upptackt, beskrivning)
                    clipboard.setText(AnnotatedString("${brev.amne}\n\n${brev.text}"))
                    kopierad = true
                },
                enabled = kanSkicka,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.reklamera_kopiera))
            }
            if (kopierad) {
                Text(
                    text = stringResource(R.string.reklamera_kopierad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            OutlinedButton(
                onClick = { onHarReklamerat(upptackt, beskrivning) },
                enabled = kanSkicka,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.reklamera_har_reklamerat))
            }
        }
    }
}
