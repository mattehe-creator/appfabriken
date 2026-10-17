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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.arStodjerGransbelopp
import se.tmconnect.husloggen.regler.grupperaPerAr
import se.tmconnect.husloggen.regler.kanVaraForbattring
import se.tmconnect.husloggen.regler.postBeloppForSammanstallningOre
import se.tmconnect.husloggen.regler.summeraPerAr
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr

@Composable
fun SammanstallningSkarm(
    poster: List<Post>,
    forsaljningsAr: Int,
    onForsaljningsArAndras: (Int) -> Unit,
    locale: Locale,
    visaSkatteInfo: Boolean,
    modifier: Modifier = Modifier,
    onExportPdf: () -> Unit = {},
    onExportCsv: () -> Unit = {},
) {
    val grupperadePoster = grupperaPerAr(poster)
    val summeringar = summeraPerAr(poster)
    val listState = rememberLazyListState()
    var arValjareExpanderad by remember { mutableStateOf(false) }
    val valbaraAr = (forsaljningsAr - 5)..(forsaljningsAr + 5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        state = listState,
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "forsaljningsar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.sammanstallning_forsaljningsar),
                    style = MaterialTheme.typography.titleMedium,
                )
                OutlinedButton(
                    onClick = { arValjareExpanderad = true },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = forsaljningsAr.toString())
                }
                DropdownMenu(
                    expanded = arValjareExpanderad,
                    onDismissRequest = { arValjareExpanderad = false },
                ) {
                    valbaraAr.forEach { ar ->
                        DropdownMenuItem(
                            text = { Text(text = ar.toString()) },
                            onClick = {
                                onForsaljningsArAndras(ar)
                                arValjareExpanderad = false
                            },
                            modifier = Modifier.tryckyta48(),
                        )
                    }
                }
            }
        }

        item(key = "export-buttons") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onExportPdf,
                    modifier = Modifier
                        .weight(1f)
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.export_hus_historik_pdf))
                }
                OutlinedButton(
                    onClick = onExportCsv,
                    modifier = Modifier
                        .weight(1f)
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.export_hus_historik_csv))
                }
            }
        }

        grupperadePoster.forEach { arGrupp ->
            item(key = "ar-${arGrupp.ar}") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${arGrupp.ar} (${formateraPrisKr(arGrupp.summaOre, locale)})",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        summeringar[arGrupp.ar]?.forEach { (typ, summa) ->
                            Text(
                                text = "${postTypEtikett(typ)}: ${formateraPrisKr(summa, locale)}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        if (visaSkatteInfo) {
                            Text(
                                text = if (arStodjerGransbelopp(poster, arGrupp.ar)) {
                                    stringResource(R.string.sammanstallning_grans_ar)
                                } else {
                                    stringResource(R.string.sammanstallning_grans_ar_ej)
                                },
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            items(arGrupp.poster, key = { it.id }) { post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                ) {
                    Text(
                        text = "${formateraDatum(post.datum, locale)}: ${post.vad}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = postTypEtikett(post.typ),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = postBeloppForSammanstallningOre(post)
                                ?.let { formateraPrisKr(it, locale) }
                                .orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    if (visaSkatteInfo) {
                        val forbattring = kanVaraForbattring(post, forsaljningsAr)
                        Text(
                            text = when (forbattring) {
                                ForbattringMojlighet.Ja -> stringResource(R.string.sammanstallning_forbattring_ja)
                                ForbattringMojlighet.KravBedomning -> stringResource(
                                    R.string.sammanstallning_forbattring_bedomning,
                                )
                                ForbattringMojlighet.Nej -> stringResource(R.string.sammanstallning_forbattring_nej)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        if (visaSkatteInfo) {
            item(key = "skatte-info") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_rubrik),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_text),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
