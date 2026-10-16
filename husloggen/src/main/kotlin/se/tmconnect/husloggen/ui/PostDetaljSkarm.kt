package se.tmconnect.husloggen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Photo
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
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
            
            // Visa bilder om det finns några
            if (post.bilder != null && post.bilder.isNotEmpty()) {
                PostDetaljRad(
                    etikett = stringResource(R.string.falt_bilder),
                    varde = "",
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Visa miniatyrer av bilder
                    post.bilder.forEachIndexed { index, filnamn ->
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            contentDescription = stringResource(R.string.bild_miniatyr),
                            modifier = Modifier
                                .weight(1f)
                                .tryckyta48()
                        )
                    }
                }
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
