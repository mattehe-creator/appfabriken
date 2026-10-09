package se.tmconnect.garantivalvet.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopListaSkarm(
    kopLista: List<Kop>,
    onLaggTill: () -> Unit,
    onOppna: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
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

@Composable
fun KopDetaljSkarm(
    kop: Kop,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    onTillbaka: () -> Unit,
    kvittoUri: Uri?,
    onOppnaKvitto: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.detalj_titel)) },
                navigationIcon = {
                    TextButton(onClick = onTillbaka) {
                        Text(text = stringResource(R.string.tillbaka))
                    }
                }
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
            KopDetaljSkelett(
                kop = kop,
                onAndra = onAndra,
                onTaBort = onTaBort,
                kvittoUri = kvittoUri,
                onOppnaKvitto = onOppnaKvitto
            )
        }
    }
}

@Composable
fun KopDetaljSkelett(
    kop: Kop,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    kvittoUri: Uri?,
    onOppnaKvitto: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = kop.vad, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        
        if (kop.varKopt != null) {
            Text(text = stringResource(R.string.var_kopt, kop.varKopt))
        }
        
        Text(text = stringResource(R.string.kopdatum, kop.kopdatum.toString()))
        Text(text = stringResource(R.string.garanti, kop.garantiManader.toString()))
        
        if (kop.prisOre != null) {
            Text(text = stringResource(R.string.pris, (kop.prisOre / 100.0).toString()))
        }
        
        if (kop.anteckning != null) {
            Text(text = stringResource(R.string.anteckning, kop.anteckning))
        }
        
        if (kvittoUri != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = stringResource(R.string.kvitto_fornhandsvisning))
                Image(
                    bitmap = kvittoUri.asImageBitmap(),
                    contentDescription = stringResource(R.string.kvitto_fornhandsvisning),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { onOppnaKvitto() }
                )
            }
        }
        
        Button(onClick = onAndra) {
            Text(text = stringResource(R.string.andra))
        }
        
        Button(onClick = onTaBort) {
            Text(text = stringResource(R.string.ta_bort))
        }
    }
}
