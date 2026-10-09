package se.tmconnect.garantivalvet.ui

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopListaSkarm(
    kopLista: List<Kop>,
    onLaggTill: () -> Unit,
    onOppna: (Long) -> Unit,
    paminnelserPa: Boolean,
    onPaminnelserAndras: (Boolean) -> Unit,
    behorighetNekad: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
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
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onLaggTill) {
                Text(text = stringResource(R.string.lagg_till))
            }
        },
    ) { innerPadding ->
        if (kopLista.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = stringResource(R.string.tom_lista))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(kopLista, key = { it.id }) { kop ->
                    KopListRad(
                        kop = kop,
                        onClick = { onOppna(kop.id) },
                    )
                }
            }
        }
    }
}
