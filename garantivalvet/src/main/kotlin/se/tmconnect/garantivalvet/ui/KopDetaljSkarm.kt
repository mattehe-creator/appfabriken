package se.tmconnect.garantivalvet.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.KONSUMENTKOP_IKRAFT
import se.tmconnect.garantivalvet.regler.KopStatus
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import se.tmconnect.garantivalvet.regler.garantiSlut
import se.tmconnect.garantivalvet.regler.kopStatus
import se.tmconnect.garantivalvet.regler.reklamationSlut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopDetaljSkarm(
    kop: Kop,
    kvittoUri: Uri?,
    onOppnaKvitto: () -> Unit,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    onTillbaka: () -> Unit,
    arPro: Boolean,
    onExportPdf: () -> Unit,
    onDelaPdf: () -> Unit,
    onVisaProDialog: () -> Unit,
    exportPdfMeddelande: ExportPdfMeddelande?,
    onExportMeddelandeVisat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visaBekraftaTaBort by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Hämta enhetens locale
    val locale = LocalConfiguration.current.locales[0]

    if (visaBekraftaTaBort) {
        AlertDialog(
            onDismissRequest = { visaBekraftaTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTaBort()
                        visaBekraftaTaBort = false
                    },
                ) {
                    Text(text = stringResource(R.string.ja_ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visaBekraftaTaBort = false }) {
                    Text(text = stringResource(R.string.nej))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = kop.vad) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            kop.varKopt?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }

            Text(
                text = stringResource(R.string.falt_datum) + ": " + formateraDatum(kop.kopdatum, locale),
                style = MaterialTheme.typography.bodyLarge,
            )

            kop.prisOre?.let {
                Text(
                    text = stringResource(R.string.falt_pris) + ": " + formateraPrisKr(it, locale),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Text(
                text = stringResource(R.string.falt_garanti_manader) + ": ${kop.garantiManader}",
                style = MaterialTheme.typography.bodyLarge,
            )

            val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
            val status = kopStatus(kop.kopdatum, kop.garantiManader, LocalDate.now())
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.lista_garanti_slut, formateraDatum(garantiSlutDatum, locale)),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                val statusText = when (status) {
                    KopStatus.GARANTI_GALLER -> stringResource(R.string.status_galler)
                    KopStatus.GARANTI_SNART -> stringResource(R.string.status_gar_ut_snart)
                    KopStatus.REKLAMATION_GALLER -> stringResource(R.string.status_reklamation_galler)
                    KopStatus.UTGANGEN -> stringResource(R.string.status_utgangen)
                }
                val statusColor = when (status) {
                    KopStatus.GARANTI_GALLER -> MaterialTheme.colorScheme.primary
                    KopStatus.GARANTI_SNART -> MaterialTheme.colorScheme.tertiary
                    KopStatus.REKLAMATION_GALLER -> MaterialTheme.colorScheme.primary
                    KopStatus.UTGANGEN -> MaterialTheme.colorScheme.error
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor,
                )
            }

            // Reklamationsrätt information
            val reklamationSlutDatum = reklamationSlut(kop.kopdatum)
            if (reklamationSlutDatum != null) {
                // Visa reklamationsinformation för köp efter 2022-05-01
                Text(
                    text = stringResource(R.string.detalj_reklamation_till, formateraDatum(reklamationSlutDatum, locale)),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.detalj_reklamation_rubrik),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.detalj_reklamation_forklaring),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.detalj_reklamation_vagledning),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else if (kop.kopdatum.isBefore(KONSUMENTKOP_IKRAFT)) {
                // Visa information om äldre lag för köp före 2022-05-01
                Text(
                    text = stringResource(R.string.detalj_reklamation_aldre_lag),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            kop.anteckning?.let {
                Text(
                    text = stringResource(R.string.falt_anteckning) + ": $it",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Button(
                onClick = {
                    if (arPro) {
                        onExportPdf()
                    } else {
                        onVisaProDialog()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(text = stringResource(R.string.export_pdf))
            }

            Button(
                onClick = {
                    if (arPro) {
                        onDelaPdf()
                    } else {
                        onVisaProDialog()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.export_dela))
            }

            Button(onClick = onAndra, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.andra))
            }

            Button(
                onClick = { visaBekraftaTaBort = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text(text = stringResource(R.string.ta_bort))
            }

            Button(onClick = onTillbaka, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.avbryt))
            }
        }
    }

    // Visa snackbar när exportmeddelande är satt
    exportPdfMeddelande?.let {
        val meddelande = when (it) {
            ExportPdfMeddelande.Sparad -> stringResource(R.string.export_pdf_sparad)
            ExportPdfMeddelande.Misslyckades -> stringResource(R.string.export_pdf_misslyckades)
        }
        
        // Visa snackbar
        androidx.compose.runtime.LaunchedEffect(meddelande) {
            snackbarHostState.showSnackbar(meddelande)
            onExportMeddelandeVisat()
        }
    }
}
