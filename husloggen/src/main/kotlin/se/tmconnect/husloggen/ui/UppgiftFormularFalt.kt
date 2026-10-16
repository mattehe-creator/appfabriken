package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.regler.UppgiftFel
import se.tmconnect.karna.format.formateraDatum

@Composable
fun UppgiftNamnFalt(
    namn: String,
    onNamnAndras: (String) -> Unit,
    fel: UppgiftFel?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = namn,
        onValueChange = onNamnAndras,
        label = { Text(text = stringResource(R.string.falt_namn)) },
        isError = fel == UppgiftFel.NAMN_SAKNAS,
        supportingText = if (fel == UppgiftFel.NAMN_SAKNAS) {
            { Text(text = stringResource(R.string.fel_namn_saknas)) }
        } else {
            null
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun UppgiftIntervallFalt(
    intervallText: String,
    onIntervallAndras: (String) -> Unit,
    fel: UppgiftFel?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = intervallText,
        onValueChange = onIntervallAndras,
        label = { Text(text = stringResource(R.string.falt_intervall_manader)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = fel == UppgiftFel.INTERVALL_SAKNAS ||
            fel == UppgiftFel.INTERVALL_UTANFOR,
        supportingText = when (fel) {
            UppgiftFel.INTERVALL_SAKNAS -> {
                { Text(text = stringResource(R.string.fel_intervall_saknas)) }
            }
            UppgiftFel.INTERVALL_UTANFOR -> {
                { Text(text = stringResource(R.string.fel_intervall_ogiltigt)) }
            }
            else -> null
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun UppgiftStartdatumFalt(
    datum: LocalDate,
    onValjDatum: () -> Unit,
    locale: Locale,
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
            text = "${stringResource(R.string.falt_startdatum)}: ${formateraDatum(datum, locale)}",
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
fun UppgiftPaminnelseFalt(
    paminnelsePa: Boolean,
    onPaminnelseAndras: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .tryckyta48(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.falt_paminnelse),
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = paminnelsePa,
            onCheckedChange = onPaminnelseAndras,
        )
    }
    Text(
        text = stringResource(if (paminnelsePa) R.string.paminnelse_pa else R.string.paminnelse_av),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun UppgiftAnteckningFalt(
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
