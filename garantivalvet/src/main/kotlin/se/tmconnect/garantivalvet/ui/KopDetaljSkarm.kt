package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.GarantiStatus
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import se.tmconnect.garantivalvet.regler.garantiSlut
import se.tmconnect.garantivalvet.regler.garantiStatus
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopDetaljSkarm(
    kop: Kop,
    kvittoUri: Uri?,
    onOppnaKvitto: () -> Unit,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visaBekraftaTaBort by remember { mutableStateOf(false) }

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
                text = stringResource(R.string.falt_datum) + ": " + formateraDatum(kop.kopdatum),
                style = MaterialTheme.typography.bodyLarge,
            )

            kop.prisOre?.let {
                Text(
                    text = stringResource(R.string.falt_pris) + ": " + formateraPrisKr(it),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Text(
                text = stringResource(R.string.falt_garanti_manader) + ": ${kop.garantiManader}",
                style = MaterialTheme.typography.bodyLarge,
            )

            val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
            val status = garantiStatus(kop.kopdatum, kop.garantiManader, LocalDate.now())
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.lista_garanti_slut, formateraDatum(garantiSlutDatum)),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                val statusText = when (status) {
                    GarantiStatus.GALLER -> stringResource(R.string.status_galler)
                    GarantiStatus.GAR_UT_SNART -> stringResource(R.string.status_gar_ut_snart)
                    GarantiStatus.UTGANGEN -> stringResource(R.string.status_utgangen)
                }
                val statusColor = when (status) {
                    GarantiStatus.GALLER -> MaterialTheme.colorScheme.primary
                    GarantiStatus.GAR_UT_SNART -> MaterialTheme.colorScheme.tertiary
                    GarantiStatus.UTGANGEN -> MaterialTheme.colorScheme.error
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor,
                )
            }

            kop.anteckning?.let {
                Text(
                    text = stringResource(R.string.falt_anteckning) + ": $it",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            kvittoUri?.let { uri ->
                val context = LocalContext.current
                val bitmap = remember(uri) {
                    try {
                        // Läs först måtten utan att ladda hela bilden
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            BitmapFactory.decodeStream(input, null, options)
                        }
                        
                        // Beräkna inSampleSize baserat på storlek (max 480 px på längsta sidan)
                        val sampleSize = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 480)
                        
                        // Läs bilden med rätt inSampleSize
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            val decodeOptions = BitmapFactory.Options().apply {
                                inSampleSize = sampleSize
                            }
                            BitmapFactory.decodeStream(input, null, decodeOptions)
                        }
                    } catch (_: Exception) {
                        null
                    }
                }
                bitmap?.let { decoded ->
                    Image(
                        bitmap = decoded.asImageBitmap(),
                        contentDescription = stringResource(R.string.kvitto_fornhandsvisning),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clickable(onClick = onOppnaKvitto),
                    )
                }
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
}
