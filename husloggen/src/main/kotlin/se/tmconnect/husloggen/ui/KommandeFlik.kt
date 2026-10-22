package se.tmconnect.husloggen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.kommandePerManad

/** Jobben de närmaste tolv månaderna, per månad. Försenade jobb ligger först, under innevarande månad. */
@Composable
fun KommandeFlik(
    uppgifter: List<Uppgift>,
    locale: Locale,
    contentPadding: PaddingValues,
    onOppnaUppgift: (Long) -> Unit,
) {
    val perManad = remember(uppgifter) { kommandePerManad(uppgifter, LocalDate.now()) }
    val dagFormat = remember(locale) { DateTimeFormatter.ofPattern("d MMMM", locale) }
    LazyColumn(contentPadding = contentPadding) {
        item {
            Text(
                text = stringResource(R.string.kommande_rubrik),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
        if (perManad.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.kommande_tomt),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        perManad.forEach { (manad, jobb) ->
            item(key = "m$manad") {
                val namn = manad.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                    .replaceFirstChar { it.titlecase(locale) }
                Text(
                    text = "$namn ${manad.year}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                )
            }
            items(jobb, key = { "j${it.uppgiftId}-${it.datum}" }) { j ->
                ListItem(
                    headlineContent = { Text(j.namn) },
                    supportingContent = {
                        Text(
                            if (j.forsenad) {
                                stringResource(R.string.kommande_forsenad, j.datum.format(dagFormat))
                            } else {
                                j.datum.format(dagFormat)
                            },
                            color = if (j.forsenad) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .tryckyta48()
                        .clickable(role = Role.Button) { onOppnaUppgift(j.uppgiftId) },
                )
            }
        }
    }
}
