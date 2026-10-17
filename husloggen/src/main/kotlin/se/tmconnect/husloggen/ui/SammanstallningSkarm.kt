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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.arStodjerGransbelopp
import se.tmconnect.husloggen.regler.kanVaraForbattring
import se.tmconnect.husloggen.regler.summeraPerAr
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr

private enum class SammanstallningFlik { SAMMANSTALLNING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SammanstallningSkarm(
    poster: List<Post>,
    forsaljningsAr: Int,
    onForsaljningsArAndras: (Int) -> Unit,
    locale: Locale,
    visaSkatteInfo: Boolean,
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanderad by remember { mutableStateOf(false) }
    val ar = LocalDate.now().year
    val minAr = ar - 10
    val maxAr = ar + 10
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.flik_sammanstallning)) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Försäljningsår väljare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.sammanstallning_forsaljningsar),
                    style = MaterialTheme.typography.bodyMedium,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = { onForsaljningsArAndras(forsaljningsAr - 1) },
                        modifier = Modifier.tryckyta48(),
                    ) {
                        Text(text = "-")
                    }
                    
                    // Dropdown eller TextField beroende på om vi vill ha en dropdown
                    Box {
                        TextButton(
                            onClick = { expanderad = true },
                            modifier = Modifier.tryckyta48(),
                        ) {
                            Text(text = forsaljningsAr.toString())
                        }
                        
                        DropdownMenu(
                            expanded = expanderad,
                            onDismissRequest = { expanderad = false },
                        ) {
                            (minAr..maxAr).forEach { ar ->
                                DropdownMenuItem(
                                    text = { Text(text = ar.toString()) },
                                    onClick = {
                                        onForsaljningsArAndras(ar)
                                        expanderad = false
                                    },
                                    modifier = Modifier.tryckyta48(),
                                )
                            }
                        }
                    }
                    
                    TextButton(
                        onClick = { onForsaljningsArAndras(forsaljningsAr + 1) },
                        modifier = Modifier.tryckyta48(),
                    ) {
                        Text(text = "+")
                    }
                }
            }
            
            // Sammanställning
            val grupperadePoster = poster.groupBy { it.datum.year }.toSortedMap(reverseOrder())
            val summeringPerAr = summeraPerAr(poster)
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                grupperadePoster.forEach { (ar, arPoster) ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // År och totalsumma
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = ar.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    
                                    val summa = summeringPerAr[ar]?.values?.sumOf { it } ?: 0L
                                    Text(
                                        text = formateraPrisKr(summa, locale),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                                
                                // Summor per typ
                                if (summeringPerAr.containsKey(ar)) {
                                    val typSummor = summeringPerAr[ar]!!
                                    typSummor.forEach { (typ, summa) ->
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
                                }
                                
                                // Gränsbelopp info
                                if (visaSkatteInfo) {
                                    val stodjerGrans = arStodjerGransbelopp(poster, ar)
                                    Text(
                                        text = if (stodjerGrans) {
                                            stringResource(R.string.sammanstallning_grans_ar)
                                        } else {
                                            stringResource(R.string.sammanstallning_grans_ar_ej)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (stodjerGrans) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                                
                                // Poster för år
                                items(arPoster) { post ->
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = formateraDatum(post.datum, locale),
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                            Text(
                                                text = post.vad,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            Text(
                                                text = postTypEtikett(post.typ),
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = formateraPrisKr(post.beloppOre ?: 0L, locale),
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            
                                            if (visaSkatteInfo) {
                                                val forbattring = kanVaraForbattring(post, forsaljningsAr)
                                                Text(
                                                    text = when (forbattring) {
                                                        ForbattringMojlighet.Ja -> stringResource(R.string.forbattring_ja)
                                                        ForbattringMojlighet.KravBedomning -> stringResource(R.string.forbattring_krav_bedomning)
                                                        ForbattringMojlighet.Nej -> stringResource(R.string.forbattring_nej)
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Skatteinformation
            if (visaSkatteInfo) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_rubrik),
                            style = MaterialTheme.typography.titleSmall,
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
