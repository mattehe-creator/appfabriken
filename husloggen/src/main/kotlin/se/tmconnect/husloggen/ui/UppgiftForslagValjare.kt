package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.regler.IntervallBestamdAv
import se.tmconnect.husloggen.regler.UppgiftsForslag
import se.tmconnect.husloggen.regler.UPPGIFTSFORSLAG

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UppgiftForslagValjare(
    onValj: (UppgiftsForslag) -> Unit,
    modifier: Modifier = Modifier,
) {
    var senasteValt by remember { mutableStateOf<UppgiftsForslag?>(null) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.forslag_rubrik),
            style = MaterialTheme.typography.titleSmall,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UPPGIFTSFORSLAG.forEach { forslag ->
                val forslagNamn = stringResource(forslag.namnResId)
                FilterChip(
                    selected = false,
                    onClick = {
                        senasteValt = forslag
                        onValj(forslag)
                    },
                    label = { Text(text = forslagNamn) },
                    modifier = Modifier.tryckyta48(),
                )
            }
        }
        when (senasteValt?.bestamdAv) {
            IntervallBestamdAv.KOMMUNEN -> {
                Text(
                    text = stringResource(R.string.enligt_kommunen),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            IntervallBestamdAv.TILLVERKAREN -> {
                Text(
                    text = stringResource(R.string.enligt_tillverkaren),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            else -> Unit
        }
    }
}
