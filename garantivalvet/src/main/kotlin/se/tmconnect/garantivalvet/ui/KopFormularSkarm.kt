package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.regler.FormularFel
import se.tmconnect.garantivalvet.regler.tolkaPrisTillOre
import se.tmconnect.garantivalvet.regler.valideraKopFormular
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.GarantitidVal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopFormularSkarm(
    titel: String,
    initialVad: String,
    initialVar: String,
    initialGarantiManader: String,
    initialPris: String?,
    initialAnteckning: String?,
    initialKopdatum: LocalDate,
    kvittoUri: Uri?,
    onValjBild: () -> Unit,
    onTaFoto: () -> Unit,
    onTaBortKvitto: () -> Unit,
    onSpara: (vad: String, varKopt: String?, kopdatum: LocalDate, garantiManader: Int, prisOre: Long?, anteckning: String?) -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var vad by remember(initialVad) { mutableStateOf(initialVad) }
    var varKopt by remember(initialVar) { mutableStateOf(initialVar) }
    var garantiManaderText by remember(initialGarantiManader) { mutableStateOf(initialGarantiManader) }
    var prisText by remember(initialPris) { mutableStateOf(initialPris ?: "") }
    var anteckning by remember(initialAnteckning) { mutableStateOf(initialAnteckning ?: "") }
    var kopdatum by remember(initialKopdatum) { mutableStateOf(initialKopdatum) }

    var visarDatumvaljare by remember { mutableStateOf(false) }
    val datumValjare = rememberDatePickerState(
        initialSelectedDateMillis = kopdatum.toEpochDay() * 86_400_000,
    )

    var fel by remember { mutableStateOf<List<FormularFel>>(emptyList()) }
    var valtChipIndex by remember(initialGarantiManader) { mutableIntStateOf(1) } // Standard är 24 månader
    var annatText by remember { mutableStateOf("") }

    if (visarDatumvaljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumvaljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumValjare.selectedDateMillis?.let { millis ->
                            kopdatum = LocalDate.ofEpochDay(millis / 86_400_000)
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

    fun spara() {
        val idag = LocalDate.now()
        val garantiManader = GarantitidVal.manaderFranVal(valtChipIndex, annatText)
        
        fel = valideraKopFormular(
            vad,
            garantiManader ?: -1,
            kopdatum,
            idag,
            prisText,
        )
        if (fel.isEmpty()) {
            val prisOre = tolkaPrisTillOre(prisText)
            onSpara(
                vad,
                varKopt.takeIf { it.isNotBlank() },
                kopdatum,
                garantiManader ?: -1,
                prisOre,
                anteckning.takeIf { it.isNotBlank() },
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = titel) },
                navigationIcon = {
                    IconButton(onClick = onStang) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.stang),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { spara() }) {
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
            // Kvitto-kort (kommenterad ut eftersom det inte finns i den aktuella koden)
            // KvittoKort(
            //     kvittoUri = kvittoUri,
            //     onValjBild = onValjBild,
            //     onTaFoto = onTaFoto,
            //     onTaBortKvitto = onTaBortKvitto,
            // )

            OutlinedTextField(
                value = vad,
                onValueChange = { vad = it },
                label = { Text(text = stringResource(R.string.falt_vad)) },
                isError = FormularFel.VAD_SAKNAS in fel,
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.VAD_SAKNAS in fel) {
                Text(
                    text = stringResource(R.string.fel_vad_saknas),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = varKopt,
                onValueChange = { varKopt = it },
                label = { Text(text = stringResource(R.string.falt_var)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.falt_datum),
                style = MaterialTheme.typography.labelLarge,
            )
            Button(
                onClick = { visarDatumvaljare = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = kopdatum.toString())
            }
            if (FormularFel.KOPDATUM_FRAMTID in fel) {
                Text(
                    text = stringResource(R.string.fel_datum),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            // Garantitid med chips
            Text(
                text = stringResource(R.string.falt_garanti_manader),
                style = MaterialTheme.typography.labelLarge,
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = valtChipIndex == 0,
                    onClick = { valtChipIndex = 0 },
                    label = { Text(text = "12 mån") } // Hårdkodad sträng
                )
                FilterChip(
                    selected = valtChipIndex == 1,
                    onClick = { valtChipIndex = 1 },
                    label = { Text(text = "24 mån") } // Hårdkodad sträng
                )
                FilterChip(
                    selected = valtChipIndex == 2,
                    onClick = { valtChipIndex = 2 },
                    label = { Text(text = "36 mån") } // Hårdkodad sträng
                )
                FilterChip(
                    selected = valtChipIndex == 3,
                    onClick = { valtChipIndex = 3 },
                    label = { Text(text = "Annat") } // Hårdkodad sträng
                )
            }

            if (valtChipIndex == 3) {
                OutlinedTextField(
                    value = annatText,
                    onValueChange = { annatText = it },
                    label = { Text(text = "Ange antal månader") }, // Hårdkodad sträng
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (FormularFel.GARANTI_OGILTIG in fel) {
                Text(
                    text = stringResource(R.string.fel_garanti),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = prisText,
                onValueChange = { prisText = it },
                label = { Text(text = stringResource(R.string.falt_pris)) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.PRIS_OGILTIGT in fel) {
                Text(
                    text = stringResource(R.string.fel_pris),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = anteckning,
                onValueChange = { anteckning = it },
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
