package se.tmconnect.husloggen.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetaljSkarm(
    post: Post,
    uppgiftNamn: String?,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    onTillbaka: () -> Unit,
    bildFilnamn: List<String> = emptyList(),
    bildUri: (String) -> Uri = { Uri.EMPTY },
    onOppnaBild: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    var visarTaBort by remember { mutableStateOf(false) }

    if (visarTaBort) {
        AlertDialog(
            onDismissRequest = { visarTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_post_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_post_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTaBort()
                        visarTaBort = false
                    },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.ja_ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarTaBort = false }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.nej))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.post_detalj_rubrik)) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PostDetaljRad(
                etikett = stringResource(R.string.falt_vad),
                varde = post.vad,
            )
            PostDetaljRad(
                etikett = stringResource(R.string.falt_datum),
                varde = formateraDatum(post.datum, locale),
            )
            PostDetaljRad(
                etikett = stringResource(R.string.falt_typ),
                varde = postTypEtikett(post.typ),
            )
            val belopp = formateraPrisKr(post.beloppOre, locale)
            if (belopp.isNotEmpty()) {
                PostDetaljRad(
                    etikett = stringResource(R.string.falt_belopp),
                    varde = belopp,
                )
            }
            val avdrag = formateraPrisKr(post.rotRutAvdragOre, locale)
            if (avdrag.isNotEmpty()) {
                PostDetaljRad(
                    etikett = stringResource(R.string.falt_rot_rut_avdrag),
                    varde = avdrag,
                )
            }
            post.utforare?.takeIf { it.isNotBlank() }?.let {
                PostDetaljRad(
                    etikett = stringResource(R.string.falt_utforare),
                    varde = it,
                )
            }
            PostDetaljRad(
                etikett = stringResource(R.string.falt_uppgift),
                varde = uppgiftNamn ?: stringResource(R.string.ingen_uppgift),
            )
            post.anteckning?.takeIf { it.isNotBlank() }?.let {
                PostDetaljRad(
                    etikett = stringResource(R.string.falt_anteckning),
                    varde = it,
                )
            }

            if (bildFilnamn.isNotEmpty()) {
                PostBildMiniatyrer(
                    bildFilnamn = bildFilnamn,
                    bildUri = bildUri,
                    onOppnaBild = onOppnaBild,
                )
            }

            Button(
                onClick = onAndra,
                modifier = Modifier
                    .fillMaxWidth()
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.knapp_andra))
            }
            OutlinedButton(
                onClick = { visarTaBort = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.ta_bort))
            }
        }
    }
}

@Composable
private fun PostDetaljRad(
    etikett: String,
    varde: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etikett,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = varde,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun PostBildMiniatyrer(
    bildFilnamn: List<String>,
    bildUri: (String) -> Uri,
    onOppnaBild: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bildHarBilder = stringResource(R.string.bild_har_bilder)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        bildFilnamn.forEachIndexed { index, filnamn ->
            val uri = remember(filnamn) { bildUri(filnamn) }
            val bitmap = remember(uri) {
                try {
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    }
                    val sampleSize = calculateMiniatyrInSampleSize(options.outWidth, options.outHeight)
                    context.contentResolver.openInputStream(uri)?.use { input ->
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

            bitmap?.let { decoded ->
                Image(
                    bitmap = decoded.asImageBitmap(),
                    contentDescription = bildHarBilder,
                    modifier = Modifier
                        .size(64.dp)
                        .tryckyta48()
                        .clickable { onOppnaBild(index) },
                )
            }
        }
    }
}

private fun calculateMiniatyrInSampleSize(width: Int, height: Int): Int {
    val maxSida = 480
    var inSampleSize = 1
    if (height > maxSida || width > maxSida) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= maxSida && (halfWidth / inSampleSize) >= maxSida) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
