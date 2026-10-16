package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.regler.IntervallBestamdAv
import se.tmconnect.husloggen.regler.UPPGIFTSFORSLAG

@Composable
fun UppgiftForslagValjare(
    onValj: (UppgiftsForslag) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.forslag_rubrik),
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier.padding(bottom = 8.dp)
    )
    
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        UPPGIFTSFORSLAG.forEach { forslag ->
            val forslagNamn = stringResource(forslag.namnResId)
            FilterChip(
                selected = false,
                onClick = { onValj(forslag) },
                label = { Text(text = forslagNamn) },
                modifier = Modifier.tryckyta48(),
            )
        }
    }
    
    // Visa hjälptext efter förslagsraden
    val senasteForslag = UPPGIFTSFORSLAG.lastOrNull()
    if (senasteForslag != null) {
        when (senasteForslag.bestamdAv) {
            IntervallBestamdAv.KOMMUNEN -> {
                Text(
                    text = stringResource(R.string.enligt_kommunen),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IntervallBestamdAv.TILLVERKAREN -> {
                Text(
                    text = stringResource(R.string.enligt_tillverkaren),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            else -> Unit
        }
    }
}
