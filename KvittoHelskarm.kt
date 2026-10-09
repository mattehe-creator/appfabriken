package se.tmconnect.garantivalvet.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KvittoHelskarm(
    kvittoUri: android.net.Uri,
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val topAppBarState = rememberTopAppBarState()
    
    TopAppBar(
        title = { Text(text = stringResource(R.string.kvitto_helskarm_titel)) },
        navigationIcon = {
            Button(onClick = onTillbaka) {
                Text(text = stringResource(R.string.kvitto_stang))
            }
        },
        actions = {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, kvittoUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, stringResource(R.string.kvitto_dela)))
            }) {
                Text(text = stringResource(R.string.kvitto_dela))
            }
        },
        state = topAppBarState,
        modifier = modifier
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
    ) {
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var isDoubleTap by remember { mutableStateOf(false) }
        
        val density = LocalDensity.current
        val maxScale = 3f
        
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, maxScale)
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    )
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val event = awaitPointerEvent()
                        if (event.changes.size == 1 && event.changes[0].pressed) {
                            if (!isDoubleTap) {
                                isDoubleTap = true
                                // Delay to check for double tap
                                kotlinx.coroutines.delay(300)
                                isDoubleTap = false
                            } else {
                                // Double tap detected - reset scale and position
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                }
        ) {
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()
            
            Image(
                bitmap = android.graphics.BitmapFactory.decodeStream(context.contentResolver.openInputStream(kvittoUri))
                    .asImageBitmap(),
                contentDescription = stringResource(R.string.kvitto_bild_beskrivning),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeight.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            )
        }
    }
}
