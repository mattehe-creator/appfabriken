package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R
import se.tmconnect.karna.tema.TemaLage
import se.tmconnect.garantivalvet.export.AUTO_INTERVALL_DAGLIGEN
import se.tmconnect.garantivalvet.export.AUTO_INTERVALL_MANADSVIS
import se.tmconnect.garantivalvet.export.AUTO_INTERVALL_VECKOVIS
import se.tmconnect.garantivalvet.export.AutoBackupTillstand
import se.tmconnect.garantivalvet.regler.formateraDatum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallningarSkarm(
    paminnelserPa: Boolean,
    onPaminnelserAndras: (Boolean) -> Unit,
    behorighetNekad: Boolean,
    arPro: Boolean,
    onVisaProDialog: () -> Unit,
    onSparaSakerhetskopia: () -> Unit,
    onHamtaSakerhetskopia: () -> Unit,
    onTillbaka: () -> Unit,
    debugInstallningar: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    autoBackup: AutoBackupTillstand = AutoBackupTillstand(),
    onAutoBackupPa: (Boolean) -> Unit = {},
    onByttMapp: () -> Unit = {},
    onAutoBackupIntervall: (Int) -> Unit = {},
    onKorNuAutoBackup: () -> Unit = {},
    onHemforsakringPdf: () -> Unit = {},
    onHemforsakringCsv: () -> Unit = {},
    temaLage: TemaLage = TemaLage.SYSTEM,
    onTemaLage: (TemaLage) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.installningar_rubrik)) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.pro_app_rubrik),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = if (arPro) {
                            stringResource(R.string.pro_du_har)
                        } else {
                            stringResource(R.string.pro_inte_aktiv)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!arPro) {
                    TextButton(onClick = onVisaProDialog, modifier = Modifier.tryckyta48()) {
                        Text(text = stringResource(R.string.pro_las_upp))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.paminnelse_installning_rubrik),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = stringResource(R.string.paminnelse_installning_beskrivning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = paminnelserPa,
                    onCheckedChange = onPaminnelserAndras,
                    modifier = Modifier.tryckyta48(),
                )
            }
            if (behorighetNekad) {
                Text(
                    text = stringResource(R.string.paminnelse_behorighet_nekad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.utseende_rubrik),
                    style = MaterialTheme.typography.titleMedium,
                )
                Column(modifier = Modifier.selectableGroup()) {
                    listOf(
                        TemaLage.SYSTEM to R.string.utseende_system,
                        TemaLage.LJUST to R.string.utseende_ljust,
                        TemaLage.MORKT to R.string.utseende_morkt,
                    ).forEach { (lage, namn) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .semantics(mergeDescendants = true) {
                                    selected = temaLage == lage
                                }
                                .clickable(role = Role.RadioButton) { onTemaLage(lage) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = temaLage == lage,
                                onClick = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clearAndSetSemantics { },
                            )
                            Text(
                                text = stringResource(namn),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 12.dp),
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.sakerhetskopia_rubrik),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.sakerhetskopia_beskrivning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.sakerhetskopia_import_beskrivning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onSparaSakerhetskopia,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.sakerhetskopia_spara))
                    }
                    Button(
                        onClick = onHamtaSakerhetskopia,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.sakerhetskopia_hamta))
                    }
                }
            }

            AutoBackupAvsnitt(
                tillstand = autoBackup,
                onPa = onAutoBackupPa,
                onByttMapp = onByttMapp,
                onIntervall = onAutoBackupIntervall,
                onKorNu = onKorNuAutoBackup,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.hem_rubrik),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.hem_beskrivning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onHemforsakringPdf,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.hem_pdf))
                    }
                    Button(
                        onClick = onHemforsakringCsv,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.hem_csv))
                    }
                }
            }

            debugInstallningar()
        }
    }
}

@Composable
private fun AutoBackupAvsnitt(
    tillstand: AutoBackupTillstand,
    onPa: (Boolean) -> Unit,
    onByttMapp: () -> Unit,
    onIntervall: (Int) -> Unit,
    onKorNu: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.auto_rubrik), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(R.string.auto_beskrivning),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.auto_pa), style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = tillstand.pa,
                onCheckedChange = onPa,
                modifier = Modifier.tryckyta48(),
            )
        }
        if (tillstand.pa) {
            Text(
                text = stringResource(
                    R.string.auto_mapp,
                    tillstand.mappNamn ?: stringResource(R.string.auto_mapp_okand),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(
                    AUTO_INTERVALL_DAGLIGEN to R.string.auto_dagligen,
                    AUTO_INTERVALL_VECKOVIS to R.string.auto_veckovis,
                    AUTO_INTERVALL_MANADSVIS to R.string.auto_manadsvis,
                ).forEach { (dagar, textRes) ->
                    FilterChip(
                        selected = tillstand.intervallDagar == dagar,
                        onClick = { onIntervall(dagar) },
                        label = { Text(text = stringResource(textRes)) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
            }
            Text(
                text = tillstand.senaste?.let {
                    stringResource(
                        R.string.auto_senaste,
                        formateraDatum(
                            java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                            locale,
                        ),
                    )
                } ?: stringResource(R.string.auto_ingen_an),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (tillstand.fel) {
                Text(
                    text = stringResource(R.string.auto_fel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onByttMapp, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                    Text(text = stringResource(R.string.auto_byt_mapp))
                }
                OutlinedButton(onClick = onKorNu, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                    Text(text = stringResource(R.string.auto_kor_nu))
                }
            }
        }
    }
}
