package se.tmconnect.rostfangst.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import se.tmconnect.rostfangst.R
import se.tmconnect.rostfangst.data.FangstEntitet
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RostfangstApp(
    viewModel: RostfangstViewModel,
    onStartInspelning: () -> Unit,
    onExporteraCsv: () -> Unit,
    onUtforKalender: (android.content.Intent) -> Unit,
) {
    val flik by viewModel.flik.collectAsStateWithLifecycle()
    val inspelning by viewModel.inspelning.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = flik == RostfangstFlik.START,
                    onClick = { viewModel.valjFlik(RostfangstFlik.START) },
                    icon = { Icon(Icons.Default.Mic, contentDescription = stringResource(R.string.flik_start)) },
                    label = { Text(stringResource(R.string.flik_start)) },
                )
                NavigationBarItem(
                    selected = flik == RostfangstFlik.ATT_GORA,
                    onClick = { viewModel.valjFlik(RostfangstFlik.ATT_GORA) },
                    icon = { Icon(Icons.Default.Check, contentDescription = stringResource(R.string.flik_att_gora)) },
                    label = { Text(stringResource(R.string.flik_att_gora)) },
                )
                NavigationBarItem(
                    selected = flik == RostfangstFlik.ANTECKNINGAR,
                    onClick = { viewModel.valjFlik(RostfangstFlik.ANTECKNINGAR) },
                    icon = { Icon(Icons.Default.Check, contentDescription = stringResource(R.string.flik_anteckningar)) },
                    label = { Text(stringResource(R.string.flik_anteckningar)) },
                )
                NavigationBarItem(
                    selected = flik == RostfangstFlik.HISTORIK,
                    onClick = { viewModel.valjFlik(RostfangstFlik.HISTORIK) },
                    icon = { Icon(Icons.Default.Check, contentDescription = stringResource(R.string.flik_historik)) },
                    label = { Text(stringResource(R.string.flik_historik)) },
                )
                NavigationBarItem(
                    selected = flik == RostfangstFlik.INSTALLNINGAR,
                    onClick = { viewModel.valjFlik(RostfangstFlik.INSTALLNINGAR) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.flik_installningar)) },
                    label = { Text(stringResource(R.string.flik_installningar)) },
                )
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            when (flik) {
                RostfangstFlik.START -> StartSkarm(inspelning, onStartInspelning, viewModel, onUtforKalender)
                RostfangstFlik.ATT_GORA -> AttGoraSkarm(viewModel)
                RostfangstFlik.ANTECKNINGAR -> AnteckningarSkarm(viewModel)
                RostfangstFlik.HISTORIK -> HistorikSkarm(viewModel)
                RostfangstFlik.INSTALLNINGAR -> InstallningarSkarm(viewModel, onExporteraCsv)
            }
        }
    }
}

@Composable
private fun StartSkarm(
    inspelning: InspelningsTillstand,
    onStartInspelning: () -> Unit,
    viewModel: RostfangstViewModel,
    onUtforKalender: (android.content.Intent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (inspelning.forslag == null && !inspelning.lyssnar) {
            Button(
                onClick = onStartInspelning,
                modifier = Modifier.fillMaxWidth().height(120.dp),
            ) {
                Icon(Icons.Default.Mic, contentDescription = null)
                Text(stringResource(R.string.sag_det_knapp), modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (inspelning.lyssnar || inspelning.deltext.isNotBlank()) {
            Text(stringResource(R.string.lyssnar))
            Text(inspelning.deltext.ifBlank { inspelning.text })
        }
        inspelning.forslag?.let { forslag ->
            ForslagKort(
                forslag = forslag,
                offline = inspelning.offlineSparad,
                onAndra = viewModel::uppdateraForslag,
                onUtfor = { viewModel.utforForslag(onUtforKalender) },
            )
        }
    }
}

@Composable
private fun ForslagKort(
    forslag: Forslag,
    offline: Boolean,
    onAndra: (Forslag) -> Unit,
    onUtfor: () -> Unit,
) {
    var typ by remember(forslag) { mutableStateOf(forslag.typ) }
    var titel by remember(forslag) { mutableStateOf(forslag.titel) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (offline) {
            Text(stringResource(R.string.offline_sparad))
        }
        Text(stringResource(R.string.forslag_rubrik))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ForslagTyp.entries.forEach { valt ->
                FilterChip(
                    selected = typ == valt,
                    onClick = {
                        typ = valt
                        onAndra(forslag.copy(typ = valt, titel = titel))
                    },
                    label = { Text(stringResource(typ.labelRes())) },
                )
            }
        }
        OutlinedTextField(
            value = titel,
            onValueChange = {
                titel = it
                onAndra(forslag.copy(typ = typ, titel = it))
            },
            label = { Text(stringResource(R.string.falt_titel)) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (forslag.typ == ForslagTyp.FRAGA && !forslag.svar.isNullOrBlank()) {
            Text(stringResource(R.string.svar_rubrik))
            Text(forslag.svar!!)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onUtfor) {
                Text(stringResource(R.string.utfor))
            }
        }
    }
}

@Composable
private fun AttGoraSkarm(viewModel: RostfangstViewModel) {
    val lista by viewModel.attGora.collectAsStateWithLifecycle()
    FangstLista(lista, visaKlar = true, onKlar = viewModel::togglaAttGora)
}

@Composable
private fun AnteckningarSkarm(viewModel: RostfangstViewModel) {
    val lista by viewModel.anteckningar.collectAsStateWithLifecycle()
    FangstLista(lista, visaKlar = false, onKlar = null)
}

@Composable
private fun HistorikSkarm(viewModel: RostfangstViewModel) {
    val lista by viewModel.historik.collectAsStateWithLifecycle()
    LazyColumn(Modifier.padding(16.dp)) {
        items(lista) { rad ->
            FangstRad(rad, onTolkaOm = { viewModel.rostaOmTolkning(rad) })
        }
    }
}

@Composable
private fun FangstLista(
    lista: List<FangstEntitet>,
    visaKlar: Boolean,
    onKlar: ((Long, Boolean) -> Unit)?,
) {
    LazyColumn(Modifier.padding(16.dp)) {
        items(lista) { rad ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (visaKlar && onKlar != null) {
                    Checkbox(
                        checked = rad.klar,
                        onCheckedChange = { onKlar(rad.id, it) },
                        modifier = Modifier.semantics { contentDescription = rad.titel },
                    )
                }
                Column {
                    Text(rad.titel, style = MaterialTheme.typography.bodyLarge)
                    Text(rad.typ.name, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun FangstRad(rad: FangstEntitet, onTolkaOm: () -> Unit) {
    val tid = Instant.ofEpochMilli(rad.skapadEpochMillis)
        .atZone(ZoneId.of("Europe/Stockholm"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("$tid · ${rad.typ.name}")
        Text(rad.titel)
        if (rad.vantarTolkning) {
            Button(onClick = onTolkaOm) { Text(stringResource(R.string.tolka_om)) }
        }
    }
}

@Composable
private fun InstallningarSkarm(viewModel: RostfangstViewModel, onExporteraCsv: () -> Unit) {
    val ui by viewModel.installningarUi.collectAsStateWithLifecycle()
    val perDag by viewModel.fangsterPerDag.collectAsStateWithLifecycle()
    var nyckel by remember(ui) { mutableStateOf(ui.apiNyckel) }
    var modell by remember(ui) { mutableStateOf(ui.modell) }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = nyckel,
            onValueChange = { nyckel = it },
            label = { Text(stringResource(R.string.api_nyckel)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = modell,
            onValueChange = { modell = it },
            label = { Text(stringResource(R.string.modell)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = { viewModel.sparaInstallningar(nyckel, modell) }) {
            Text(stringResource(R.string.spara))
        }
        Text(stringResource(R.string.fangster_per_dag_rubrik))
        perDag.forEach { (dag, antal) ->
            Text(stringResource(R.string.fangster_per_dag_rad, dag.toString(), antal))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onExporteraCsv) { Text(stringResource(R.string.exportera_csv)) }
    }
}

private fun ForslagTyp.labelRes(): Int = when (this) {
    ForslagTyp.KALENDER -> R.string.typ_kalender
    ForslagTyp.PAMINNELSE -> R.string.typ_paminnelse
    ForslagTyp.ATT_GORA -> R.string.typ_att_gora
    ForslagTyp.ANTECKNING -> R.string.typ_anteckning
    ForslagTyp.FRAGA -> R.string.typ_fraga
    ForslagTyp.HEM -> R.string.typ_hem
}
