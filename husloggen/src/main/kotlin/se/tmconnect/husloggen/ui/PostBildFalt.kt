package se.tmconnect.husloggen.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    val context = LocalContext.current
    Column(modifier = modifier) {
        if (bildFilnamn.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                bildFilnamn.forEach { filnamn ->
                    val uri = bildUri(filnamn)
                    val bitmap by produceState<android.graphics.Bitmap?>(null, uri) {
                        value = if (uri == null) {
                            null
                        } else {
                            withContext(Dispatchers.IO) {
                                try {
                                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                    context.contentResolver.openInputStream(uri)?.use {
                                        BitmapFactory.decodeStream(it, null, options)
                                    }
                                    val sample = beraknaInSampleSize(
                                        options.outWidth,
                                        options.outHeight,
                                        maxSida = 320,
                                    )
                                    context.contentResolver.openInputStream(uri)?.use {
                                        BitmapFactory.decodeStream(
                                            it,
                                            null,
                                            BitmapFactory.Options().apply { inSampleSize = sample },
                                        )
                                    }
                                } catch (_: Exception) {
                                    null
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.size(104.dp)) {
                        bitmap?.let { bild ->
                            Image(
                                bitmap = bild.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 4.dp, end = 4.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                            )
                        }
                        IconButton(
                            onClick = { onTaBortBild(filnamn) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(48.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.bild_ta_bort),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                        CircleShape,
                                    )
                                    .padding(4.dp),
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
                modifier = Modifier
                    .weight(1f)
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.bild_valj_bild))
            }
            Button(
                onClick = onTaFoto,
                enabled = kanLaggaTill,
                modifier = Modifier
                    .weight(1f)
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.bild_ta_foto))
            }
        }

        if (!kanLaggaTill) {
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

private fun beraknaInSampleSize(bredd: Int, hojd: Int, maxSida: Int): Int {
    if (maxOf(bredd, hojd) <= maxSida) {
        return 1
    }
    val langsta = maxOf(bredd, hojd)
    var inSampleSize = 1
    while (langsta / (inSampleSize * 2) >= maxSida) {
        inSampleSize *= 2
    }
    return inSampleSize
}
