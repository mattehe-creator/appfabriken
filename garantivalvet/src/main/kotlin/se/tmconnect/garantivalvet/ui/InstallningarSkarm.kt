package se.tmconnect.garantivalvet.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R

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
    visaDebugProTestlage: Boolean,
    debugProTestlage: Boolean,
    onDebugProTestlageAndras: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.installningar_rubrik)) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka) {
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
                    TextButton(onClick = onVisaProDialog) {
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
                )
            }
            if (behorighetNekad) {
                Text(
                    text = stringResource(R.string.paminnelse_behorighet_nekad),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
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

            if (visaDebugProTestlage) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.pro_debug_testlage),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Switch(
                        checked = debugProTestlage,
                        onCheckedChange = onDebugProTestlageAndras,
                    )
                }
            }
        }
    }
}
