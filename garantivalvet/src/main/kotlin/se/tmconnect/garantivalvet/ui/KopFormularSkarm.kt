package se.tmconnect.garantivalvet.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.regler.FormularFel
import se.tmconnect.garantivalvet.regler.valideraKopFormular

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
    onSpara: (vad: String, varKopt: String?, kopdatum: LocalDate, garantiManader: Int, prisOre: Long?, anteckning: String?) -> Unit,
    onAvbryt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    
    var vad by remember(initialVad) { mutableStateOf(initialVad) }
    var varKopt by remember(initialVar) { mutableStateOf(initialVar) }
    var garantiManader by remember(initialGarantiManader) { mutableStateOf(initialGarantiManader) }
    var prisText by remember(initialPris) { mutableStateOf(initialPris ?: "") }
    var anteckning by remember(initialAnteckning) { mutableStateOf(initialAnteckning ?: "") }
    var kopdatum by remember(initialKopdatum) { mutableStateOf(initialKopdatum) }
    
    var visarDatumväljare by remember { mutableStateOf(false) }
    val datumVäljare = rememberDatePickerState(
        initialSelectedDateMillis = kopdatum.toEpochDay() * 86400000
    )
    
    // Valideringsfel
    var fel by remember { mutableStateOf<List<FormularFel>>(emptyList()) }
    
    if (visarDatumväljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumväljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumVäljare.selectedDateMillis?.let { millis ->
                            kopdatum = LocalDate.ofEpochDay(millis / 86400000)
                        }
                        visarDatumväljare = false
                    }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            dismissButton = {
                Button(
                    onClick = { visarDatumväljare = false }
                ) {
                    Text(text = stringResource(R.string.avbryt))
                }
            }
        ) {
            DatePicker(state = datumVäljare)
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = titel) })
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
            // Fält: vad
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
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            
            // Fält: var (valfritt)
            OutlinedTextField(
                value = varKopt,
                onValueChange = { varKopt = it },
                label = { Text(text = stringResource(R.string.falt_var)) },
                modifier = Modifier.fillMaxWidth(),
            )
            
            // Fält: köpdatum
            Text(
                text = stringResource(R.string.falt_datum),
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = { visarDatumväljare = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = kopdatum.toString())
            }
            if (FormularFel.KOPDATUM_FRAMTID in fel) {
                Text(
                    text = stringResource(R.string.fel_datum),
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            
            // Fält: garantitid i månader
            OutlinedTextField(
                value = garantiManader,
                onValueChange = { garantiManader = it },
                label = { Text(text = stringResource(R.string.falt_garanti_manader)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = FormularFel.GARANTI_OGILTIG in fel,
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.GARANTI_OGILTIG in fel) {
                Text(
                    text = stringResource(R.string.fel_garanti),
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            
            // Fält: pris (valfritt)
            OutlinedTextField(
                value = prisText,
                onValueChange = { prisText = it },
                label = { Text(text = stringResource(R.string.falt_pris)) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.PRIS_OGILTIGT in fel) {
                Text(
                    text = stringResource(R.string.fel_pris),
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            
            // Fält: anteckning (valfritt)
            OutlinedTextField(
                value = anteckning,
                onValueChange = { anteckning = it },
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
            )
            
            // Knapp: spara
            Button(
                onClick = {
                    // Validera formuläret
                    val idag = LocalDate.now()
                    fel = valideraKopFormular(vad, garantiManader.toIntOrNull() ?: 0, kopdatum, idag, prisText)
                    
                    if (fel.isEmpty()) {
                        // Om inga fel finns, spara
                        val prisOre = se.tmconnect.garantivalvet.regler.tolkaPrisTillOre(prisText)
                        onSpara(vad, varKopt.takeIf { it.isNotBlank() }, kopdatum, garantiManader.toIntOrNull() ?: 0, prisOre, anteckning.takeIf { it.isNotBlank() })
                    } else {
                        // Visa felmeddelande
                        Toast.makeText(context, "Formuläret innehåller fel", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = vad.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.spara))
            }
            
            // Knapp: avbryt
            TextButton(onClick = onAvbryt, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.avbryt))
            }
        }
    }
}
