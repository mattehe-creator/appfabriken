package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R

@Composable
fun GarantivalvetDebugInstallningar(
    arPro: Boolean,
    onTestlageProAndras: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.debug_pro_testlage),
            style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
            checked = arPro,
            onCheckedChange = onTestlageProAndras,
        )
    }
}
