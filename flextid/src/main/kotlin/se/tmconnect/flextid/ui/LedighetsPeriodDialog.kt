package se.tmconnect.flextid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.LEDIGHETSTYPER_FOR_PERIOD

/**
 * Ledighet för en period: välj från och till i en kalender och typ. Förhandsvisningen visar hur
 * många arbetsdagar som får en post (helger, röda dagar och redan registrerade dagar hoppas över).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun LedighetsPeriodDialog(
    antalDagar: suspend (LocalDate, LocalDate) -> Int,
    typEtikett: @Composable (DagTyp) -> String,
    onSpara: (LocalDate, LocalDate, DagTyp) -> Unit,
    onStang: () -> Unit,
) {
    val state = rememberDateRangePickerState()
    var typ by rememberSaveable { mutableStateOf(DagTyp.SEMESTER) }
    val fran = state.selectedStartDateMillis?.let(::tillDatum)
    val till = (state.selectedEndDateMillis ?: state.selectedStartDateMillis)?.let(::tillDatum)
    var antal by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(fran, till) {
        antal = if (fran != null && till != null) antalDagar(fran, till) else null
    }
    DatePickerDialog(
        onDismissRequest = onStang,
        confirmButton = {
            TextButton(
                onClick = { if (fran != null && till != null) onSpara(fran, till, typ) },
                enabled = fran != null && till != null && (antal ?: 0) > 0,
                modifier = Modifier.heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.spara)) }
        },
        dismissButton = {
            TextButton(onClick = onStang, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.avbryt))
            }
        },
    ) {
        Column {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                LEDIGHETSTYPER_FOR_PERIOD.forEach { t ->
                    FilterChip(
                        selected = typ == t,
                        onClick = { typ = t },
                        label = { Text(typEtikett(t)) },
                    )
                }
            }
            antal?.let { n ->
                Text(
                    text = pluralStringResource(R.plurals.ledighet_forhandsvisning, n, n),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
            DateRangePicker(
                state = state,
                title = {
                    Text(
                        stringResource(R.string.ledighet_period_rubrik),
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                    )
                },
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            )
        }
    }
}

/** DatePicker räknar i UTC-millisekunder för dagens början. */
private fun tillDatum(millis: Long): LocalDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
