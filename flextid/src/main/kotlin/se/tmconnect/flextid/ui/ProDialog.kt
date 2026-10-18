package se.tmconnect.flextid.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.flextid.R
@Composable
fun ProDialog(
    onDismiss: () -> Unit,
    onKopPro: (Activity) -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.pro)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.pro_forklaring))
                Text(text = stringResource(R.string.pro_grans_nadd))
                Text(text = stringResource(R.string.pro_export_gratis))
            }
        },
        confirmButton = {
            Button(
                onClick = { onKopPro(context as Activity) },
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.pro_las_upp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.pro_inte_nu))
            }
        },
    )
}
