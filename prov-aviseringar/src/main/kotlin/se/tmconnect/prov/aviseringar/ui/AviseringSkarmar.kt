package se.tmconnect.prov.aviseringar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import se.tmconnect.prov.aviseringar.R
import se.tmconnect.prov.aviseringar.data.AppAntalRad
import se.tmconnect.prov.aviseringar.data.AviseringEntitet
import se.tmconnect.prov.aviseringar.data.PaketStatistik
import se.tmconnect.karna.tema.AppfabrikKortForm

enum class AviseringSkarm {
    HEM,
    INSTALLNINGAR,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AviseringAppSkarm(
    skarm: AviseringSkarm,
    atkomstAktiverad: Boolean,
    antalPerApp: List<AppAntalRad>,
    senaste: List<AviseringEntitet>,
    paketStatistik: List<PaketStatistik>,
    valdaPaket: Set<String>,
    onSkarm: (AviseringSkarm) -> Unit,
    onOppnaAtkomst: () -> Unit,
    onExportera: () -> Unit,
    onRensa: () -> Unit,
    onValtPaket: (String, Boolean) -> Unit,
) {
    var visaRensaDialog by remember { mutableStateOf(false) }

    if (visaRensaDialog) {
        AlertDialog(
            onDismissRequest = { visaRensaDialog = false },
            title = { Text(stringResource(R.string.rensa_bekrafta_titel)) },
            text = { Text(stringResource(R.string.rensa_bekrafta_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        visaRensaDialog = false
                        onRensa()
                    },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(stringResource(R.string.rensa))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { visaRensaDialog = false },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(stringResource(R.string.avbryt))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (skarm) {
                            AviseringSkarm.HEM -> stringResource(R.string.skarm_hem)
                            AviseringSkarm.INSTALLNINGAR -> stringResource(R.string.skarm_installningar)
                        },
                    )
                },
                navigationIcon = {
                    if (skarm == AviseringSkarm.INSTALLNINGAR) {
                        val cd = stringResource(R.string.tillbaka)
                        IconButton(
                            onClick = { onSkarm(AviseringSkarm.HEM) },
                            modifier = Modifier.semantics { contentDescription = cd },
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                },
                actions = {
                    if (skarm == AviseringSkarm.HEM) {
                        val cd = stringResource(R.string.skarm_installningar)
                        IconButton(
                            onClick = { onSkarm(AviseringSkarm.INSTALLNINGAR) },
                            modifier = Modifier.semantics { contentDescription = cd },
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        }
                    }
                },
            )
        },
    ) { padding ->
        when (skarm) {
            AviseringSkarm.HEM ->
                HemSkarm(
                    modifier = Modifier.padding(padding),
                    atkomstAktiverad = atkomstAktiverad,
                    antalPerApp = antalPerApp,
                    senaste = senaste,
                    onOppnaAtkomst = onOppnaAtkomst,
                    onExportera = onExportera,
                    onRensa = { visaRensaDialog = true },
                )
            AviseringSkarm.INSTALLNINGAR ->
                InstallningarSkarm(
                    modifier = Modifier.padding(padding),
                    paketStatistik = paketStatistik,
                    valdaPaket = valdaPaket,
                    onValtPaket = onValtPaket,
                )
        }
    }
}

@Composable
private fun HemSkarm(
    modifier: Modifier = Modifier,
    atkomstAktiverad: Boolean,
    antalPerApp: List<AppAntalRad>,
    senaste: List<AviseringEntitet>,
    onOppnaAtkomst: () -> Unit,
    onExportera: () -> Unit,
    onRensa: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
    ) {
        item {
            AtkomstKort(atkomstAktiverad = atkomstAktiverad, onOppnaAtkomst = onOppnaAtkomst)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(onClick = onExportera, modifier = Modifier.tryckyta48()) {
                    Text(stringResource(R.string.exportera))
                }
                TextButton(onClick = onRensa, modifier = Modifier.tryckyta48()) {
                    Text(stringResource(R.string.rensa))
                }
            }
        }
        item {
            Text(
                stringResource(R.string.antal_per_app_rubrik),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (antalPerApp.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.inga_sparade),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(antalPerApp, key = { it.paketNamn }) { rad ->
                Text(
                    stringResource(R.string.antal_per_app_rad, rad.appNamn, rad.antal),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                stringResource(R.string.senaste_rubrik),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (senaste.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.inga_senaste),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(senaste, key = { it.id }) { rad ->
                AviseringRad(rad)
            }
        }
    }
}

@Composable
private fun AtkomstKort(atkomstAktiverad: Boolean, onOppnaAtkomst: () -> Unit) {
    Card(shape = AppfabrikKortForm, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                if (atkomstAktiverad) {
                    stringResource(R.string.atkomst_pa)
                } else {
                    stringResource(R.string.atkomst_av)
                },
                style = MaterialTheme.typography.titleMedium,
            )
            if (!atkomstAktiverad) {
                Text(
                    stringResource(R.string.atkomst_hjalp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onOppnaAtkomst, modifier = Modifier.tryckyta48()) {
                Text(stringResource(R.string.oppna_atkomst))
            }
        }
    }
}

@Composable
private fun AviseringRad(rad: AviseringEntitet) {
    val tidText =
        remember(rad.loggadTid) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .format(Instant.ofEpochMilli(rad.loggadTid).atZone(ZoneId.systemDefault()))
        }
    Card(shape = AppfabrikKortForm, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "${rad.appNamn} · $tidText",
                style = MaterialTheme.typography.labelLarge,
            )
            val rubrik = rad.title ?: rad.conversationTitle
            if (!rubrik.isNullOrBlank()) {
                Text(rubrik, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            val brödtext = rad.text ?: rad.bigText ?: rad.textLines ?: rad.messages
            if (!brödtext.isNullOrBlank()) {
                Text(
                    brödtext,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InstallningarSkarm(
    modifier: Modifier = Modifier,
    paketStatistik: List<PaketStatistik>,
    valdaPaket: Set<String>,
    onValtPaket: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text(
                stringResource(R.string.valj_appar_hjalp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        if (paketStatistik.isEmpty()) {
            item {
                Text(stringResource(R.string.inga_paket))
            }
        } else {
            items(paketStatistik, key = { it.paketNamn }) { stat ->
                PaketRad(
                    stat = stat,
                    valt = stat.paketNamn in valdaPaket,
                    onValt = { onValtPaket(stat.paketNamn, it) },
                )
            }
        }
    }
}

@Composable
private fun PaketRad(stat: PaketStatistik, valt: Boolean, onValt: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().tryckyta48(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = valt, onCheckedChange = onValt)
        Column(modifier = Modifier.weight(1f)) {
            Text(stat.appNamn, style = MaterialTheme.typography.bodyLarge)
            Text(
                stringResource(R.string.paket_antal, stat.paketNamn, stat.antal),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
