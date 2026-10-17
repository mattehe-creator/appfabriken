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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    
    val arMedPoster = poster
        .filter { it.beloppOre != null }
        .map { it.datum.year }
        .distinct()
        .sortedDescending()
    
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
        ) {
            // Försäljningsår väljare
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.sammanstallning_forsaljningsar),
                    style = MaterialTheme.typography.bodyMedium,
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { onForsaljningsArAndras(forsaljningsAr - 1) },
                        modifier = Modifier.tryckyta48(),
                    ) {
                        Text(text = "-")
                    }
                    
                    // Dropdown för årval
                    Box {
                        OutlinedButton(
                            onClick = { expanderad = true },
                            modifier = Modifier.tryckyta48(),
                        ) {
                            Text(text = forsaljningsAr.toString())
                        }
                        
                        DropdownMenu(
                            expanded = expanderad,
                            onDismissRequest = { expanderad = false },
                        ) {
                            (2010..2030).forEach { ar ->
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
                    
                    OutlinedButton(
                        onClick = { onForsaljningsArAndras(forsaljningsAr + 1) },
                        modifier = Modifier.tryckyta48(),
                    ) {
                        Text(text = "+")
                    }
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                arMedPoster.forEach { ar ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                        ) {
                            // År rubrik med summa
                            Text(
                                text = if (poster.any { it.beloppOre != null && it.datum.year == ar }) {
                                    "${ar} (${formateraPrisKr(poster.filter { it.datum.year == ar && it.beloppOre != null }.sumOf { it.beloppOre!! }, locale)})"
                                } else {
                                    ar.toString()
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            
                            // Summor per typ
                            val summorPerTyp = summeraPerAr(poster.filter { it.datum.year == ar })
                            if (summorPerTyp.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                ) {
                                    summorPerTyp.forEach { (typ, summa) ->
                                        Text(
                                            text = "${postTypEtikett(typ)}: ${formateraPrisKr(summa, locale)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                            
                            // Total summa
                            val totalSumma = poster.filter { it.datum.year == ar && it.beloppOre != null }.sumOf { it.beloppOre!! }
                            if (totalSumma > 0) {
                                Text(
                                    text = stringResource(R.string.sammanstallning_total_summa, formateraPrisKr(totalSumma, locale)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                            
                            // Gränsbelopp info
                            if (visaSkatteInfo) {
                                Text(
                                    text = if (arStodjerGransbelopp(poster, ar)) {
                                        stringResource(R.string.sammanstallning_grans_ar)
                                    } else {
                                        stringResource(R.string.sammanstallning_grans_ar_ej)
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                            
                            // Poster för år
                            val arPoster = poster.filter { it.datum.year == ar }
                            if (arPoster.isNotEmpty()) {
                                items(arPoster, key = { it.id }) { post ->
                                    PostSammanstallningRad(
                                        post = post,
                                        locale = locale,
                                        forsaljningsAr = forsaljningsAr,
                                        visaSkatteInfo = visaSkatteInfo,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Skatteinformation
            if (visaSkatteInfo) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .heightIn(min = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_rubrik),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = stringResource(R.string.sammanstallning_skatte_info_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostSammanstallningRad(
    post: Post,
    locale: Locale,
    forsaljningsAr: Int,
    visaSkatteInfo: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${formateraDatum(post.datum, locale)} - ${post.vad}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = formateraPrisKr(post.beloppOre!!, locale),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = postTypEtikett(post.typ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
