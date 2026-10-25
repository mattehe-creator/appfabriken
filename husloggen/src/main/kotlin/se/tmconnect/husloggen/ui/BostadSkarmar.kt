package se.tmconnect.husloggen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.BostadBorttagningSatt

@Composable
fun BostadValjareRad(
    bostader: List<Bostad>,
    valdBostad: Bostad?,
    onValjBostad: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (bostader.size <= 1 || valdBostad == null) return
    var visaDialog by remember { mutableStateOf(false) }
    TextButton(
        onClick = { visaDialog = true },
        modifier = modifier.heightIn(min = 48.dp),
    ) {
        Text(text = stringResource(R.string.bostad_vald, valdBostad.namn))
    }
    if (visaDialog) {
        AlertDialog(
            onDismissRequest = { visaDialog = false },
            title = { Text(text = stringResource(R.string.bostad_byt)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    bostader.forEach { bostad ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clickable(role = Role.RadioButton) {
                                    onValjBostad(bostad.id)
                                    visaDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = bostad.id == valdBostad.id,
                                onClick = null,
                            )
                            Text(text = bostad.namn, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { visaDialog = false }) {
                    Text(text = stringResource(R.string.bild_stang))
                }
            },
        )
    }
}

@Composable
fun BostaderInstallningSektion(
    bostader: List<Bostad>,
    valdBostadId: Long?,
    onValjBostad: (Long) -> Unit,
    onSparaNyBostad: (String, String, String, (Boolean) -> Unit) -> Unit,
    onUppdateraBostad: (Bostad) -> Unit,
    onTaBortBostad: (Bostad, BostadBorttagningSatt, Long?, () -> Unit, () -> Unit) -> Unit,
    hamtaRaderingInfo: suspend (Long) -> Int,
    modifier: Modifier = Modifier,
) {
    // Sparas över rotation, annars stängs dialogen och det inskrivna försvinner.
    var visaNy by rememberSaveable { mutableStateOf(false) }
    var redigeraId by rememberSaveable { mutableStateOf<Long?>(null) }
    val redigera = redigeraId?.let { id -> bostader.firstOrNull { it.id == id } }
    var visaSistaVarning by remember { mutableStateOf(false) }
    var bekraftaBorttagning by remember { mutableStateOf<Bostad?>(null) }
    var bekraftAntal by remember { mutableStateOf(0) }
    var visaFlyttDialog by remember { mutableStateOf<Bostad?>(null) }
    var valdFlyttTillId by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.bostad_rubrik), style = MaterialTheme.typography.titleMedium)
        bostader.forEach { bostad ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clickable { onValjBostad(bostad.id) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = bostad.id == valdBostadId,
                    onClick = { onValjBostad(bostad.id) },
                )
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text(text = bostad.namn, style = MaterialTheme.typography.bodyLarge)
                    bostad.adress?.let {
                        Text(text = it, style = MaterialTheme.typography.bodySmall)
                    }
                }
                TextButton(onClick = { redigeraId = bostad.id }) {
                    Text(text = stringResource(R.string.knapp_andra))
                }
            }
        }
        Button(
            onClick = { visaNy = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Text(text = stringResource(R.string.bostad_lagg_till), modifier = Modifier.padding(start = 8.dp))
        }
    }

    if (visaNy) {
        BostadFormularDialog(
            titel = stringResource(R.string.bostad_lagg_till),
            onDismiss = { visaNy = false },
            onSpara = { namn, adress, anteckning ->
                onSparaNyBostad(namn, adress, anteckning) { ok ->
                    if (ok) visaNy = false
                }
            },
        )
    }
    redigera?.let { bostad ->
        BostadFormularDialog(
            titel = bostad.namn,
            startNamn = bostad.namn,
            startAdress = bostad.adress.orEmpty(),
            startAnteckning = bostad.anteckning.orEmpty(),
            onDismiss = { redigeraId = null },
            onSpara = { namn, adress, anteckning ->
                onUppdateraBostad(
                    bostad.copy(
                        namn = namn,
                        adress = adress.ifBlank { null },
                        anteckning = anteckning.ifBlank { null },
                    ),
                )
                redigeraId = null
            },
            onTaBort = if (bostader.size > 1) {
                {
                    scope.launch {
                        bekraftAntal = hamtaRaderingInfo(bostad.id)
                        bekraftaBorttagning = bostad
                        redigeraId = null
                    }
                }
            } else {
                { visaSistaVarning = true }
            },
        )
    }
    if (visaSistaVarning) {
        AlertDialog(
            onDismissRequest = { visaSistaVarning = false },
            title = { Text(text = stringResource(R.string.bostad_ta_bort)) },
            text = { Text(text = stringResource(R.string.bostad_sista_gar_inte)) },
            confirmButton = {
                TextButton(onClick = { visaSistaVarning = false }) {
                    Text(text = stringResource(R.string.bild_stang))
                }
            },
        )
    }
    bekraftaBorttagning?.let { bostad ->
        if (bekraftAntal > 0) {
            // Visa flytt-dialog
            visaFlyttDialog = bostad
        } else {
            // Visa enkel bekräftelse
            AlertDialog(
                onDismissRequest = { bekraftaBorttagning = null },
                title = { Text(text = stringResource(R.string.bostad_ta_bort)) },
                text = {
                    Text(
                        text = stringResource(
                            R.string.bostad_ta_bort_bekraftelse,
                            bostad.namn,
                            bekraftAntal,
                        ),
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onTaBortBostad(
                                bostad,
                                BostadBorttagningSatt.RADERA_INNEHALL,
                                null,
                                { visaSistaVarning = true },
                                { bekraftaBorttagning = null },
                            )
                        },
                        modifier = Modifier.heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.bostad_ta_bort))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bekraftaBorttagning = null }) {
                        Text(text = stringResource(R.string.pro_inte_nu))
                    }
                },
            )
        }
    }
    
    visaFlyttDialog?.let { bostad ->
        AlertDialog(
            onDismissRequest = { visaFlyttDialog = null },
            title = { Text(text = stringResource(R.string.bostad_ta_bort)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(
                            R.string.bostad_ta_bort_flytt_bekraftelse,
                            bostad.namn,
                            bekraftAntal,
                        ),
                    )
                    Text(text = stringResource(R.string.bostad_ta_bort_flytt_varning))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        bostader.filter { it.id != bostad.id }.forEach { annanBostad ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .clickable { valdFlyttTillId = annanBostad.id },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = valdFlyttTillId == annanBostad.id,
                                    onClick = null,
                                )
                                Text(text = annanBostad.namn, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (valdFlyttTillId != null) {
                            onTaBortBostad(
                                bostad,
                                BostadBorttagningSatt.FLYTTA_TILL,
                                valdFlyttTillId,
                                { visaSistaVarning = true },
                                { visaFlyttDialog = null },
                            )
                        }
                    },
                    enabled = valdFlyttTillId != null,
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    Text(text = stringResource(R.string.bostad_ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visaFlyttDialog = null }) {
                    Text(text = stringResource(R.string.pro_inte_nu))
                }
            },
        )
    }
}

@Composable
private fun BostadFormularDialog(
    titel: String,
    startNamn: String = "",
    startAdress: String = "",
    startAnteckning: String = "",
    onDismiss: () -> Unit,
    onSpara: (String, String, String) -> Unit,
    onTaBort: (() -> Unit)? = null,
) {
    var namn by rememberSaveable { mutableStateOf(startNamn) }
    var adress by rememberSaveable { mutableStateOf(startAdress) }
    var anteckning by rememberSaveable { mutableStateOf(startAnteckning) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titel) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = namn,
                    onValueChange = { namn = it },
                    label = { Text(text = stringResource(R.string.bostad_namn)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = adress,
                    onValueChange = { adress = it },
                    label = { Text(text = stringResource(R.string.bostad_adress)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = anteckning,
                    onValueChange = { anteckning = it },
                    label = { Text(text = stringResource(R.string.bostad_anteckning)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSpara(namn.trim(), adress, anteckning) },
                enabled = namn.isNotBlank(),
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(text = stringResource(R.string.bostad_spara))
            }
        },
        dismissButton = {
            Row {
                onTaBort?.let {
                    TextButton(onClick = it) {
                        Text(text = stringResource(R.string.bostad_ta_bort))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.pro_inte_nu))
                }
            }
        },
    )
}
