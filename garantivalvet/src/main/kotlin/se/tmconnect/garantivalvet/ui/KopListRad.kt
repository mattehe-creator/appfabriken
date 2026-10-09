package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.GarantiStatus
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.garantiSlut
import se.tmconnect.garantivalvet.regler.garantiStatus

@Composable
fun KopListRad(
    kop: Kop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Text(text = kop.vad, style = MaterialTheme.typography.bodyLarge)
        kop.varKopt?.let { 
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
        
        val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
        val status = garantiStatus(kop.kopdatum, kop.garantiManader, LocalDate.now())
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.lista_garanti_slut, formateraDatum(garantiSlutDatum)),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
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
                style = MaterialTheme.typography.bodySmall,
                color = statusColor
            )
        }
    }
}
