package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import se.tmconnect.garantivalvet.regler.garantiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopDetaljSkarm(
    kop: Kop,
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
                    }
                ) {
                    Text(text = stringResource(R.string.ja_ta_bort))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { visaBekraftaTaBort = false }
                ) {
                    Text(text = stringResource(R.string.nej))
                }
            }
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
                Text(text = stringResource(R.string.falt_var) + ": $it")
            }
            
            Text(text = stringResource(R.string.falt_kopdatum) + ": ${formateraDatum(kop.kopdatum)}")
            
            kop.prisOre?.let {
                Text(text = stringResource(R.string.falt_pris) + ": ${formateraPrisKr(it)}")
            }
            
            Text(text = stringResource(R.string.falt_garanti_manader) + ": ${kop.garantiManader}")
            
            val garantiStatus = garantiStatus(kop, LocalDate.now())
            Text(
                text = stringResource(R.string.falt_garanti_slut) + ": $garantiStatus",
                style = MaterialTheme.typography.bodyLarge
            )
            
            kop.anteckning?.let { 
                Text(text = stringResource(R.string.falt_anteckning) + ": $it")
            }
            
            Button(onClick = onAndra, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.andra))
            }
            
            Button(
                onClick = { visaBekraftaTaBort = true },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = stringResource(R.string.ta_bort))
            }
            
            Button(onClick = onTillbaka, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.avbryt))
            }
        }
    }
}
