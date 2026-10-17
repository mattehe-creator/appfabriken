package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.BuildConfig
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.grupperaPerAr
import se.tmconnect.husloggen.regler.kanVaraForbattring
import se.tmconnect.husloggen.regler.summeraPerAr
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr

private enum class HusFlik { ATT_GORA, LOGG, SAMMANSTALLNING }

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
    val arMedPoster = grupperadePoster.map { it.ar }.sortedDescending()
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.sammanstallning_rubrik),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            ForsaljningsArValjare(
                forsaljningsAr = forsaljningsAr,
                onForsaljningsArAndras = onForsaljningsArAndras,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        items(arMedPoster) { ar ->
            val arPoster = grupperadePoster.find { it.ar == ar }!!
            val harBelopp = arPoster.poster.any { it.beloppOre != null }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rubrik = if (harBelopp) {
                        "${ar} (${formateraPrisKr(arPoster.summaOre, locale)})"
                    } else {
                        ar.toString()
                    }
                    
                    Text(
                        text = rubrik,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Summering per typ
                    val summeringPerTyp = summeraPerAr(arPoster.poster)
                    itemsIndexed(summeringPerTyp.entries) { _, (typ, summa) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = postTypEtikett(typ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = formateraPrisKr(summa, locale),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    // Total summa
                    if (harBelopp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.sammanstallning_totalt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formateraPrisKr(arPoster.summaOre, locale),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Gransbelopp
                    if (visaSkatteInfo) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(
                                    if (arStodjerGransbelopp(poster, ar)) 
                                        R.string.sammanstallning_grans_ar 
                                    else 
                                        R.string.sammanstallning_grans_ar_ej
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Poster
                    items(arPoster.poster, key = { it.id }) { post ->
                        PostRad(
                            post = post,
                            locale = locale,
                            visaSkatteInfo = visaSkatteInfo,
                            forsaljningsAr = forsaljningsAr,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        
        if (visaSkatteInfo) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_rubrik),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForsaljningsArValjare(
    forsaljningsAr: Int,
    onForsaljningsArAndras: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanderad by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.sammanstallning_forsaljningsar),
            style = MaterialTheme.typography.bodyMedium
        )
        
        OutlinedButton(
            onClick = { expanderad = true },
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(text = forsaljningsAr.toString())
        }
        
        DropdownMenu(
            expanded = expanderad,
            onDismissRequest = { expanderad = false }
        ) {
            // Skapa en lista med år från 5 år bakåt till 5 år framåt
            val startAr = forsaljningsAr - 5
            val endAr = forsaljningsAr + 5
            
            for (ar in startAr..endAr) {
                DropdownMenuItem(
                    text = { Text(text = ar.toString()) },
                    onClick = {
                        onForsaljningsArAndras(ar)
                        expanderad = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PostRad(
    post: Post,
    locale: Locale,
    visaSkatteInfo: Boolean,
    forsaljningsAr: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = postTypEtikett(post.typ),
                style = MaterialTheme.typography.bodySmall
            )
            
            if (post.beloppOre != null) {
                Text(
                    text = formateraPrisKr(post.beloppOre, locale),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        if (visaSkatteInfo) {
            val forbattringMojlighet = kanVaraForbattring(post, forsaljningsAr)
            val forbattringText = when (forbattringMojlighet) {
                ForbattringMojlighet.Ja -> stringResource(R.string.sammanstallning_forbattring_ja)
                ForbattringMojlighet.KravBedomning -> stringResource(R.string.sammanstallning_forbattring_bedomning)
                ForbattringMojlighet.Nej -> stringResource(R.string.sammanstallning_forbattring_nej)
            }
            
            Text(
                text = forbattringText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PostTypEtikett(typ: se.tmconnect.husloggen.data.PostTyp): String = when (typ) {
    se.tmconnect.husloggen.data.PostTyp.UNDERHALL -> stringResource(R.string.post_typ_underhall)
    se.tmconnect.husloggen.data.PostTyp.REPARATION -> stringResource(R.string.post_typ_reparation)
    se.tmconnect.husloggen.data.PostTyp.OMBYGGNAD -> stringResource(R.string.post_typ_ombyggnad)
}
