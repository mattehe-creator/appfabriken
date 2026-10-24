package se.tmconnect.flextid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.util.Locale
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.NormaltidUndantag
import se.tmconnect.flextid.regler.formateraDagManad
import se.tmconnect.flextid.regler.formateraMinuterSomTid
import se.tmconnect.flextid.regler.veckodagBit
import se.tmconnect.karna.ui.tryckyta48

/**
 * Normaltid per veckodag och sommartid. Tomt fält = standardnormaltiden.
 * Sparas med egen knapp; ogiltiga fält ignoreras och sommartiden kräver alla tre fälten.
 */
@Composable
internal fun NormaltidUndantagAvsnitt(
    undantag: NormaltidUndantag,
    arbetsdagarMask: Int,
    standardMin: Int,
    locale: Locale,
    onSpara: (Map<DayOfWeek, String>, Boolean, String, String, String) -> Unit,
) {
    // Bara veckodagar som är arbetsdagar visas.
    val arbetsdagar = DayOfWeek.entries.filter { arbetsdagarMask and veckodagBit(it) != 0 }
    // Nyckel = DayOfWeek.name, så att osparad text överlever rotation (Map<String, String> kan sparas).
    var veckodagText by rememberSaveable(undantag.perVeckodag) {
        mutableStateOf(
            undantag.perVeckodag.entries.associate { (dag, min) -> dag.name to formateraMinuterSomTid(min, locale) },
        )
    }
    val sommar = undantag.sommar
    var sommarPa by rememberSaveable(sommar) { mutableStateOf(sommar != null) }
    var franText by rememberSaveable(sommar) { mutableStateOf(sommar?.fran?.let(::formateraDagManad) ?: "1/6") }
    var tillText by rememberSaveable(sommar) { mutableStateOf(sommar?.till?.let(::formateraDagManad) ?: "31/8") }
    var sommarNormaltidText by rememberSaveable(sommar) {
        mutableStateOf(sommar?.minPerDag?.let { formateraMinuterSomTid(it, locale) } ?: "")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.normaltid_undantag_rubrik), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(R.string.normaltid_undantag_beskrivning, formateraMinuterSomTid(standardMin, locale)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        arbetsdagar.forEach { dag ->
            OutlinedTextField(
                value = veckodagText[dag.name] ?: "",
                onValueChange = { veckodagText = veckodagText + (dag.name to it) },
                label = { Text(stringResource(veckodagStringRes(dag))) },
                placeholder = { Text(formateraMinuterSomTid(standardMin, locale)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.sommartid_rubrik), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.sommartid_beskrivning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val beskrivning = stringResource(R.string.sommartid_rubrik)
            Switch(
                checked = sommarPa,
                onCheckedChange = { sommarPa = it },
                modifier = Modifier.semantics { contentDescription = beskrivning },
            )
        }
        if (sommarPa) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = franText,
                    onValueChange = { franText = it },
                    label = { Text(stringResource(R.string.sommartid_fran)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = tillText,
                    onValueChange = { tillText = it },
                    label = { Text(stringResource(R.string.sommartid_till)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
            OutlinedTextField(
                value = sommarNormaltidText,
                onValueChange = { sommarNormaltidText = it },
                label = { Text(stringResource(R.string.sommartid_normaltid)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Button(
            onClick = {
                onSpara(
                    arbetsdagar.associateWith { veckodagText[it.name] ?: "" },
                    sommarPa,
                    franText,
                    tillText,
                    sommarNormaltidText,
                )
            },
            modifier = Modifier.tryckyta48(),
        ) {
            Text(stringResource(R.string.normaltid_undantag_spara))
        }
    }
}
