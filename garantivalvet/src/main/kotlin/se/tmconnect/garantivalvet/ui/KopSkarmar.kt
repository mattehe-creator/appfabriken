package se.tmconnect.garantivalvet.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.ListaGrupp
import se.tmconnect.garantivalvet.regler.listaGrupp
import se.tmconnect.garantivalvet.regler.skaVisaGratisGransVarning
import se.tmconnect.garantivalvet.regler.kvittonKvarGratis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopListaSkarm(
    kopLista: List<Kop>,
    kvittoUriFor: (Kop) -> Uri?,
    onLaggTill: () -> Unit,
    onOppna: (Long) -> Unit,
    onOppnaInstallningar: () -> Unit,
    arPro: Boolean = false,
    onVisaProDialog: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOppnaInstallningar) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.installningar_rubrik),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onLaggTill,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )
                },
                text = { Text(text = stringResource(R.string.fab_nytt_kvitto)) },
            )
        },
    ) { innerPadding ->
        if (kopLista.isEmpty()) {
            TomListaInnehall(
                onLaggTill = onLaggTill,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            val idag = LocalDate.now()
            val grupperadLista = kopLista.groupBy { listaGrupp(it, idag) }
            val gruppOrdning = listOf(
                ListaGrupp.GAR_UT_SNART,
                ListaGrupp.GALLER,
                ListaGrupp.UTGANGNA,
            )
            val rubrikRes = mapOf(
                ListaGrupp.GAR_UT_SNART to R.string.lista_grupp_snart,
                ListaGrupp.GALLER to R.string.lista_grupp_galler,
                ListaGrupp.UTGANGNA to R.string.lista_grupp_utgangna,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Lägg till varningskortet om det behövs
                if (skaVisaGratisGransVarning(kopLista.size, arPro)) {
                    item(key = "varningskort") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.pro_kvar_gratis, kvittonKvarGratis(kopLista.size)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                TextButton(
                                    onClick = onVisaProDialog,
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text(text = stringResource(R.string.pro_las_upp))
                                }
                            }
                        }
                    }
                }

                gruppOrdning.forEach { grupp ->
                    val kopIGrupp = grupperadLista[grupp] ?: return@forEach
                    item(key = "rubrik_$grupp") {
                        Text(
                            text = stringResource(rubrikRes.getValue(grupp)),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        )
                    }
                    items(kopIGrupp, key = { it.id }) { kop ->
                        KopListRad(
                            kop = kop,
                            kvittoUri = kvittoUriFor(kop),
                            onClick = { onOppna(kop.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TomListaInnehall(
    onLaggTill: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_kvitto),
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.tom_lista_rubrik),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = stringResource(R.string.tom_lista_beskrivning),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp),
        )
        Button(
            onClick = onLaggTill,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(text = stringResource(R.string.tom_lista_knapp))
        }
    }
}
