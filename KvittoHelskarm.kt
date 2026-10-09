package se.tmconnect.garantivalvet.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R

@Composable
fun KvittoHelskarm(
    kvittoUri: Uri,
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var isZoomed by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    // Empty navigation icon to maintain consistent layout
                },
                actions = {
                    // Dela-knapp
                    androidx.compose.material3.IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/jpeg"
                                putExtra(Intent.EXTRA_STREAM, kvittoUri)
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.kvitto_dela)))
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.filled.Share,
                            contentDescription = stringResource(R.string.kvitto_dela)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, zoom, _ ->
                            scale *= zoom
                            offset += pan
                        }
                    )
                    detectTapGestures(
                        onDoubleTap = { offsetPixels ->
                            isZoomed = !isZoomed
                            scale = if (isZoomed) 2f else 1f
                            offset = androidx.compose.ui.geometry.Offset.Zero
                        }
                    )
                }
        ) {
            val bitmap = remember(kvittoUri) {
                context.contentResolver.openInputStream(kvittoUri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
            
            bitmap?.let { decoded ->
                Image(
                    bitmap = decoded.asImageBitmap(),
                    contentDescription = stringResource(R.string.kvitto_fornhandsvisning),
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .scale(scale)
                        .offset(offset.x, offset.y)
                )
            }
        }
    }
}
