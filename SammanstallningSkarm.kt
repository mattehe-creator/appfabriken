package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.arStodjerGransbelopp
import se.tmconnect.husloggen.regler.grupperaPerAr
import se.tmconnect.husloggen.regler.kanVaraForbattring
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
) {
    val grupperadePoster = grupperaPerAr(poster)
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // Försäljningsår väljare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.sammanstallning_forsaljningsar),
                    style = MaterialTheme.typography.titleMedium,
                )
                
                val forsaljningsArValel = remember { mutableIntStateOf(forsaljningsAr) }
                
                // Dropdown meny för försäljningsår
                var expanderad by remember { mutableStateOf(false) }
                val ar = (forsaljningsAr - 5)..(forsaljningsAr + 5)
                
                OutlinedButton(
                    onClick = { expanderad = true },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    Text(text = forsaljningsAr.toString())
                }
                
                DropdownMenu(
                    expanded = expanderad,
                    onDismissRequest = { expanderad = false },
                ) {
                    ar.forEach { ar ->
                        DropdownMenuItem(
                            text = { Text(text = ar.toString()) },
                            onClick = {
                                forsaljningsArValel.intValue = ar
                                onForsaljningsArAndras(ar)
                                expanderad = false
                            },
                            modifier = Modifier.heightIn(min = 48.dp),
                        )
                    }
                }
            }
        }
        
        grupperadePoster.forEach { arGrupp ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        // Årskort med summor
                        val harBelopp = arGrupp.poster.any { it.beloppOre != null }
                        val rubrik = if (harBelopp) {
                            "${arGrupp.ar} (${formateraPrisKr(arGrupp.summaOre, locale)})"
                        } else {
                            arGrupp.ar.toString()
                        }
                        
                        Text(
                            text = rubrik,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        
                        // Summor per typ
                        val summeringPerTyp = summeraPerAr(arGrupp.poster)
                        summeringPerTyp.entries.forEach { (typ, summa) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = postTypEtikett(typ),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = formateraPrisKr(summa, locale),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                        
                        // Total summa
                        if (harBelopp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = stringResource(R.string.sammanstallning_totalt),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = formateraPrisKr(arGrupp.summaOre, locale),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        // Gransbelopp info
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
                        
                        // Poster
                        items(arGrupp.poster, key = { it.id }) { post ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = formateraDatum(post.datum, locale),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = post.vad,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = postTypEtikett(post.typ),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                    
                                    Text(
                                        text = post.beloppOre?.let { formateraPrisKr(it, locale) }
                                            ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                
                                // Förbättringsbedömning
                                if (visaSkatteInfo) {
                                    val forbattring = kanVaraForbattring(post, forsaljningsAr)
                                    Text(
                                        text = when (forbattring) {
                                            ForbattringMojlighet.Ja -> stringResource(R.string.sammanstallning_forbattring_ja)
                                            ForbattringMojlighet.KravBedomning -> stringResource(R.string.sammanstallning_forbattring_bedomning)
                                            ForbattringMojlighet.Nej -> stringResource(R.string.sammanstallning_forbattring_nej)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (visaSkatteInfo) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
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

@Composable
private fun postTypEtikett(typ: PostTyp): String = when (typ) {
    PostTyp.UNDERHALL -> stringResource(R.string.post_typ_underhall)
    PostTyp.REPARATION -> stringResource(R.string.post_typ_reparation)
    PostTyp.OMBYGGNAD -> stringResource(R.string.post_typ_ombyggnad)
}
