package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
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
            TopAppBar(
                title = { 
                    Column {
                        Text(text = stringResource(R.string.app_name))
                        if (behorighetNekad) {
                            Text(
                                text = stringResource(R.string.paminnelse_behorighet_nekad),
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            )
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
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.paminnelse_installning_rubrik),
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(R.string.paminnelse_installning_beskrivning),
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                        )
                        Switch(
                            checked = paminnelserPa,
                            onCheckedChange = onPaminnelserAndras,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
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

// Denna komponent måste finnas för att kunna användas
@Composable
fun KopListRad(kop: Kop, onClick: () -> Unit) {
    // Implementering av KopListRad komponenten
    // Denna är en stub som behövs för att kompilera
    Text(text = "KopListRad stub")
}
