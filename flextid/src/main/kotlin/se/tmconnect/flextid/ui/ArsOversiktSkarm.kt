package se.tmconnect.flextid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.ManadsRad
import se.tmconnect.flextid.regler.formateraMinuterSomTid
import se.tmconnect.flextid.regler.formateraSaldoMinuter
import se.tmconnect.karna.ui.tryckyta48

@Composable
fun ArsOversiktInnehall(
    tillstand: ArsOversiktTillstand,
    locale: Locale,
    onExporteraCsv: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (tillstand.arArInnevarandeAr) {
            item {
                ArsPrognosKort(
                    prognosMin = tillstand.prognosMin,
                    locale = locale,
                )
            }
        }
        item {
            ManadsListaRubrik()
        }
        items(tillstand.rader) { rad ->
            ManadsRadRad(rad = rad, locale = locale)
        }
        item {
            OutlinedButton(
                onClick = onExporteraCsv,
                modifier = Modifier.tryckyta48().fillMaxWidth(),
            ) {
                Text(stringResource(R.string.exportera_ars_csv))
            }
        }
        item {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}

@Composable
private fun ArsPrognosKort(
    prognosMin: Int?,
    locale: Locale,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.ars_prognos_rubrik),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text =
                if (prognosMin != null) {
                    formateraSaldoMinuter(prognosMin, locale)
                } else {
                    stringResource(R.string.ars_prognos_ingen_data)
                },
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.ars_prognos_forklaring),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ManadsListaRubrik() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.csv_manad),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = stringResource(R.string.csv_arbetad),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.csv_utfall),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.csv_saldo),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ManadsRadRad(
    rad: ManadsRad,
    locale: Locale,
) {
    val manadsnamn =
        Month.of(rad.manad).getDisplayName(TextStyle.SHORT_STANDALONE, locale)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = manadsnamn,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = formateraMinuterSomTid(rad.arbetadMin, locale),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = formateraSaldoMinuter(rad.utfallMin, locale),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = formateraSaldoMinuter(rad.saldoVidSlutMin, locale),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
    }
}
