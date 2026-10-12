package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.KopStatus
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.garantiSlut
import se.tmconnect.garantivalvet.regler.kopStatus

@Composable
fun KopListRad(
    kop: Kop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Hämta enhetens locale
    val locale = LocalConfiguration.current.locales[0]
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = kop.vad, style = MaterialTheme.typography.bodyLarge)
            if (kop.kvittoFil != null) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.kvitto_har_kvitto),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
        kop.varKopt?.let { 
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
        
        val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
        val status = kopStatus(kop.kopdatum, kop.garantiManader, LocalDate.now())
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.lista_garanti_slut, formateraDatum(garantiSlutDatum, locale)),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
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
                style = MaterialTheme.typography.bodySmall,
                color = statusColor
            )
        }
    }
}
