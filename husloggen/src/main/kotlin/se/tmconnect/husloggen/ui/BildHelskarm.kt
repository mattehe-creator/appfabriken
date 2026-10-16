package se.tmconnect.husloggen.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import se.tmconnect.husloggen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BildHelskarm(
    bildUrier: List<Uri>,
    valdBildIndex: Int,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = valdBildIndex.coerceIn(0, (bildUrier.size - 1).coerceAtLeast(0)),
        pageCount = { bildUrier.size },
    )

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isZoomed by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        scale = 1f
        offset = Offset.Zero
        isZoomed = false
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    TextButton(onClick = onStang, modifier = Modifier.tryckyta48()) {
                        Text(text = stringResource(R.string.bild_stang))
                    }
                },
                actions = {
                    if (bildUrier.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/jpeg"
                                    putExtra(Intent.EXTRA_STREAM, bildUrier[pagerState.currentPage])
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        context.getString(R.string.bild_dela),
                                    ),
                                )
                            },
                            modifier = Modifier.tryckyta48(),
                        ) {
                            Text(text = stringResource(R.string.bild_dela))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { page ->
            val bitmap = remember(bildUrier[page]) {
                try {
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(bildUrier[page])?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    }
                    val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight, 1024)
                    context.contentResolver.openInputStream(bildUrier[page])?.use { input ->
                        BitmapFactory.decodeStream(
                            input,
                            null,
                            BitmapFactory.Options().apply { inSampleSize = sampleSize },
                        )
                    }
                } catch (_: Exception) {
                    null
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(page) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offset += pan
                        }
                    }
                    .pointerInput(page) {
                        detectTapGestures(
                            onDoubleTap = {
                                isZoomed = !isZoomed
                                scale = if (isZoomed) 2f else 1f
                                offset = Offset.Zero
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                bitmap?.let { decoded ->
                    Image(
                        bitmap = decoded.asImageBitmap(),
                        contentDescription = stringResource(R.string.bild_har_bilder),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            },
                    )
                }
            }
        }
    }
}

private fun calculateInSampleSize(
    width: Int,
    height: Int,
    maxDimension: Int,
): Int {
    var inSampleSize = 1

    if (height > maxDimension || width > maxDimension) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= maxDimension &&
            (halfWidth / inSampleSize) >= maxDimension
        ) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}
