package se.tmconnect.garantivalvet.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R

@Composable
fun ProDialog(
    synlig: Boolean,
    onStang: () -> Unit,
    onKopPro: (Activity) -> Unit,
    activity: Activity,
) {
    if (!synlig) {
        return
    }
    AlertDialog(
        onDismissRequest = onStang,
        title = { Text(text = stringResource(R.string.pro)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.pro_forklaring))
                Text(text = stringResource(R.string.pro_grans_nadd))
            }
        },
        confirmButton = {
            Button(
                onClick = { onKopPro(activity) },
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.pro_las_upp))
            }
        },
        dismissButton = {
            TextButton(onClick = onStang) {
                Text(text = stringResource(R.string.pro_inte_nu))
            }
        },
    )
}
