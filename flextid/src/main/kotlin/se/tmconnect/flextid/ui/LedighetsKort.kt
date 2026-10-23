package se.tmconnect.flextid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.flextid.R
import se.tmconnect.karna.format.formateraDatum

data class LedighetsKortTillstand(
    /** null = ingen golvgräns satt. */
    val flexDagar: Int?,
    val tidigasteLedig: LocalDate?,
)

@Composable
fun LedighetsKort(
    tillstand: LedighetsKortTillstand,
    locale: Locale,
    onPlaneraLedig: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (val dagar = tillstand.flexDagar) {
                null ->
                    Text(
                        text = stringResource(R.string.ledighet_kort_ingen_grans),
                        style = MaterialTheme.typography.titleMedium,
                    )
                0 ->
                    Text(
                        text = stringResource(R.string.ledighet_kort_for_lagt),
                        style = MaterialTheme.typography.titleMedium,
                    )
                else ->
                    Text(
                        text = stringResource(R.string.ledighet_kort_flexdagar, dagar),
                        style = MaterialTheme.typography.titleMedium,
                    )
            }
            tillstand.tidigasteLedig?.let { datum ->
                Text(
                    text = stringResource(R.string.ledighet_kort_tidigast, formateraDatum(datum, locale)),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            tillstand.tidigasteLedig?.let { datum ->
                OutlinedButton(onClick = { onPlaneraLedig(datum) }, modifier = Modifier.tryckyta48()) {
                    Text(stringResource(R.string.ledighet_planera_knapp))
                }
            }
        }
    }
}
