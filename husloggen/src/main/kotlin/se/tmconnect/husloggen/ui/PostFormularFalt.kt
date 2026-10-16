package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.PostFel
import se.tmconnect.karna.format.formateraDatum

@Composable
fun PostVadFalt(
    vad: String,
    onVadAndras: (String) -> Unit,
    fel: PostFel?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = vad,
        onValueChange = onVadAndras,
        label = { Text(text = stringResource(R.string.falt_vad)) },
        isError = fel == PostFel.VAD_SAKNAS,
        supportingText = if (fel == PostFel.VAD_SAKNAS) {
            { Text(text = stringResource(R.string.fel_vad_saknas)) }
        } else {
            null
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun PostDatumFalt(
    datum: LocalDate,
    onValjDatum: () -> Unit,
    locale: Locale,
    fel: PostFel?,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onValjDatum,
        modifier = modifier
            .fillMaxWidth()
            .tryckyta48(),
    ) {
        Icon(Icons.Filled.DateRange, contentDescription = null)
        Text(
            text = "${stringResource(R.string.falt_datum)}: ${formateraDatum(datum, locale)}",
            modifier = Modifier.padding(start = 8.dp),
        )
    }
    if (fel == PostFel.DATUM_I_FRAMTIDEN) {
        Text(
            text = stringResource(R.string.fel_datum_framtid),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun PostTypValjare(
    typ: PostTyp,
    onTypAndras: (PostTyp) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(text = stringResource(R.string.falt_typ), style = MaterialTheme.typography.labelLarge)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PostTyp.entries.forEach { postTyp ->
            FilterChip(
                selected = typ == postTyp,
                onClick = { onTypAndras(postTyp) },
                label = { Text(text = postTypEtikett(postTyp)) },
                modifier = Modifier.tryckyta48(),
            )
        }
    }
}

@Composable
fun PostBeloppFalt(
    beloppText: String,
    onBeloppAndras: (String) -> Unit,
    fel: PostFel?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = beloppText,
        onValueChange = onBeloppAndras,
        label = { Text(text = stringResource(R.string.falt_belopp)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = fel == PostFel.BELOPP_OGILTIGT,
        supportingText = if (fel == PostFel.BELOPP_OGILTIGT) {
            { Text(text = stringResource(R.string.fel_belopp_ogiltigt)) }
        } else {
            null
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun PostUtforareFalt(
    utforare: String,
    onUtforareAndras: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = utforare,
        onValueChange = onUtforareAndras,
        label = { Text(text = stringResource(R.string.falt_utforare)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun PostUppgiftValjare(
    uppgifter: List<Uppgift>,
    valdUppgiftId: Long?,
    onValjUppgift: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanderad by remember { mutableStateOf(false) }
    val valdText = uppgifter.find { it.id == valdUppgiftId }?.namn
        ?: stringResource(R.string.ingen_uppgift)

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanderad = true },
            modifier = Modifier
                .fillMaxWidth()
                .tryckyta48(),
        ) {
            Text(
                text = "${stringResource(R.string.falt_uppgift)}: $valdText",
            )
        }
        DropdownMenu(
            expanded = expanderad,
            onDismissRequest = { expanderad = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.ingen_uppgift)) },
                onClick = {
                    onValjUppgift(null)
                    expanderad = false
                },
                modifier = Modifier.tryckyta48(),
            )
            uppgifter.forEach { uppgift ->
                DropdownMenuItem(
                    text = { Text(text = uppgift.namn) },
                    onClick = {
                        onValjUppgift(uppgift.id)
                        expanderad = false
                    },
                    modifier = Modifier.tryckyta48(),
                )
            }
        }
    }
}

@Composable
fun PostAnteckningFalt(
    anteckning: String,
    onAnteckningAndras: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = anteckning,
        onValueChange = onAnteckningAndras,
        label = { Text(text = stringResource(R.string.falt_anteckning)) },
        modifier = modifier.fillMaxWidth(),
        minLines = 2,
    )
}

@Composable
fun postTypEtikett(typ: PostTyp): String = when (typ) {
    PostTyp.UNDERHALL -> stringResource(R.string.post_typ_underhall)
    PostTyp.REPARATION -> stringResource(R.string.post_typ_reparation)
    PostTyp.OMBYGGNAD -> stringResource(R.string.post_typ_ombyggnad)
}
