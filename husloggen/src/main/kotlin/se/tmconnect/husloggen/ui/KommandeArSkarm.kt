package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.KommandeUnderhallArGrupp
import se.tmconnect.husloggen.regler.kommandeUnderhall
import se.tmconnect.karna.format.formateraPrisKr

@Composable
fun KommandeArSkarm(
    uppgifter: List<Uppgift>,
    poster: List<Post>,
    locale: Locale,
    contentPadding: PaddingValues,
    onExportCsv: () -> Unit,
    idag: LocalDate = LocalDate.now(),
    antalAr: Int = 5,
) {
    val grupper =
        remember(uppgifter, poster, idag, antalAr) {
            kommandeUnderhall(uppgifter, poster, idag, antalAr)
        }
    val dagFormat = remember(locale) { DateTimeFormatter.ofPattern("d MMMM yyyy", locale) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "rubrik") {
            Text(
                text = stringResource(R.string.kommande_ar_rubrik),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        item(key = "forklaring") {
            Text(
                text = stringResource(R.string.kommande_ar_uppskattning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item(key = "export") {
            OutlinedButton(
                onClick = onExportCsv,
                modifier = Modifier
                    .fillMaxWidth()
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.export_kommande_ar_csv))
            }
        }
        if (grupper.isEmpty()) {
            item(key = "tomt") {
                Text(
                    text = stringResource(R.string.kommande_ar_tomt),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
        grupper.forEach { grupp ->
            item(key = "ar-${grupp.ar}") {
                ArGruppKort(grupp, locale, dagFormat)
            }
        }
    }
}

@Composable
private fun ArGruppKort(
    grupp: KommandeUnderhallArGrupp,
    locale: Locale,
    dagFormat: DateTimeFormatter,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.kommande_ar_ar_rubrik, grupp.ar),
                style = MaterialTheme.typography.titleMedium,
            )
            if (grupp.summaOre > 0) {
                Text(
                    text = stringResource(
                        R.string.kommande_ar_summa,
                        formateraPrisKr(grupp.summaOre, locale),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (grupp.saknarKostnad) {
                Text(
                    text = stringResource(R.string.kommande_ar_saknar_kostnad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            grupp.rader.forEach { rad ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = rad.namn, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = rad.datum.format(dagFormat),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text =
                            rad.kostnadOre?.let { formateraPrisKr(it, locale) }
                                ?: stringResource(R.string.kommande_ar_kostnad_okand),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
