package se.tmconnect.husloggen.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.io.File
import se.tmconnect.husloggen.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostBildFalt(
    bildFilnamn: List<String>,
    bildUri: (String) -> Uri?,
    kanLaggaTill: Boolean,
    onValjBild: () -> Unit,
    onTaFoto: () -> Unit,
    onTaBortBild: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (bildFilnamn.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                bildFilnamn.forEach { filnamn ->
                    val uri = bildUri(filnamn)
                    if (uri != null) {
                        // Visa miniatyr
                        val context = LocalContext.current
                        val file = File(context.filesDir, "bilder/$filnamn")
                        if (file.exists()) {
                            // Placeholder för att undvika kompilationsfel
                            // I praktiken skulle vi ladda bild från filsystemet här
                            Image(
                                bitmap = androidx.compose.ui.graphics.ImageBitmap.imageResource(
                                    context.resources,
                                    androidx.core.R.drawable.notification_icon_background
                                ),
                                contentDescription = stringResource(R.string.bild_fornhandsvisning),
                                modifier = Modifier
                                    .size(100.dp)
                                    .height(100.dp),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        IconButton(
                            onClick = { onTaBortBild(filnamn) },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.bild_ta_bort),
                            )
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Button(
                onClick = onValjBild,
                enabled = kanLaggaTill,
                modifier = Modifier.weight(1f),
            ) {
                Icon(imageVector = Icons.Filled.Photo, contentDescription = null)
                Text(text = stringResource(R.string.bild_valj_bild))
            }
            Button(
                onClick = onTaFoto,
                enabled = kanLaggaTill,
                modifier = Modifier.weight(1f),
            ) {
                Icon(imageVector = Icons.Filled.Photo, contentDescription = null)
                Text(text = stringResource(R.string.bild_ta_foto))
            }
        }

        if (!kanLaggaTill && bildFilnamn.isNotEmpty()) {
            Text(
                text = stringResource(R.string.bild_max_antal),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
    }
}
