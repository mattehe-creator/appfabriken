package se.tmconnect.husloggen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori
import se.tmconnect.husloggen.regler.DokumentFel
import se.tmconnect.husloggen.regler.grupperaDokumentPerKategori
import se.tmconnect.karna.format.formateraDatum

@Composable
fun DokumentFlik(
    dokument: List<Dokument>,
    locale: Locale,
    contentPadding: PaddingValues,
    onNyttDokument: () -> Unit,
    onOppnaDokument: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (dokument.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.tom_dokument),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            OutlinedButton(
                onClick = onNyttDokument,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.fab_nytt_dokument))
            }
        }
    } else {
        val grupper = remember(dokument) { grupperaDokumentPerKategori(dokument) }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            grupper.forEach { grupp ->
                item(key = "rubrik-${grupp.kategori}") {
                    Text(
                        text = dokumentKategoriEtikett(grupp.kategori),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                }
                items(grupp.dokument, key = { it.id }) { rad ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOppnaDokument(rad.id) }
                            .tryckyta48()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = rad.titel, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = formateraDatum(rad.tillagd, locale),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DokumentDetaljSkarm(
    dokument: Dokument,
    locale: Locale,
    onTillbaka: () -> Unit,
    onOppnaFil: () -> Unit,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visarTaBort by remember { mutableStateOf(false) }
    if (visarTaBort) {
        AlertDialog(
            onDismissRequest = { visarTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_dokument_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_dokument_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        visarTaBort = false
                        onTaBort()
                    },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarTaBort = false }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.avbryt))
                }
            },
        )
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = dokument.titel) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onAndra, modifier = Modifier.tryckyta48()) {
                        Text(text = stringResource(R.string.knapp_andra))
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.dokument_kategori_etikett,
                    dokumentKategoriEtikett(dokument.kategori),
                ),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(
                    R.string.dokument_tillagd_etikett,
                    formateraDatum(dokument.tillagd, locale),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            dokument.anteckning?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onOppnaFil, modifier = Modifier.tryckyta48()) {
                Text(text = stringResource(R.string.dokument_oppna_fil))
            }
            TextButton(onClick = { visarTaBort = true }, modifier = Modifier.tryckyta48()) {
                Text(text = stringResource(R.string.ta_bort_dokument))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DokumentFormularSkarm(
    tillstand: DokumentFormularTillstand,
    locale: Locale,
    idag: LocalDate,
    onTitelAndras: (String) -> Unit,
    onKategoriAndras: (DokumentKategori?) -> Unit,
    onAnteckningAndras: (String) -> Unit,
    onValjFil: () -> Unit,
    onSpara: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var kategoriMenySynlig by remember { mutableStateOf(false) }
    val titelFel =
        if (tillstand.valideringsFel == DokumentFel.TitelSaknas) {
            stringResource(R.string.fel_dokument_titel_saknas)
        } else {
            null
        }
    val filFel =
        if (tillstand.valideringsFel == DokumentFel.FilSaknas) {
            stringResource(R.string.fel_dokument_fil_saknas)
        } else {
            null
        }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (tillstand.redigerarId == 0L) {
                                R.string.nytt_dokument_rubrik
                            } else {
                                R.string.andra_dokument_rubrik
                            },
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onStang, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSpara, modifier = Modifier.tryckyta48()) {
                        Text(text = stringResource(R.string.spara))
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = tillstand.titel,
                onValueChange = onTitelAndras,
                label = { Text(text = stringResource(R.string.falt_dokument_titel)) },
                isError = titelFel != null,
                supportingText = titelFel?.let { { Text(text = it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Column {
                OutlinedButton(
                    onClick = { kategoriMenySynlig = true },
                    modifier = Modifier.tryckyta48(),
                ) {
                    Text(
                        text = stringResource(
                            R.string.dokument_kategori_vald,
                            dokumentKategoriEtikett(tillstand.kategori),
                        ),
                    )
                }
                DropdownMenu(
                    expanded = kategoriMenySynlig,
                    onDismissRequest = { kategoriMenySynlig = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = dokumentKategoriEtikett(null)) },
                        onClick = {
                            onKategoriAndras(null)
                            kategoriMenySynlig = false
                        },
                        modifier = Modifier.tryckyta48(),
                    )
                    DokumentKategori.entries.forEach { kategori ->
                        DropdownMenuItem(
                            text = { Text(text = dokumentKategoriEtikett(kategori)) },
                            onClick = {
                                onKategoriAndras(kategori)
                                kategoriMenySynlig = false
                            },
                            modifier = Modifier.tryckyta48(),
                        )
                    }
                }
            }
            OutlinedTextField(
                value = tillstand.anteckning,
                onValueChange = onAnteckningAndras,
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (tillstand.redigerarId == 0L) {
                OutlinedButton(onClick = onValjFil, modifier = Modifier.tryckyta48()) {
                    Text(
                        text = if (tillstand.filnamn == null) {
                            stringResource(R.string.dokument_valj_fil)
                        } else {
                            stringResource(R.string.dokument_fil_vald)
                        },
                    )
                }
                if (filFel != null) {
                    Text(
                        text = filFel,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.dokument_fil_oforandrad),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (tillstand.redigerarId == 0L) {
                Text(
                    text = stringResource(R.string.dokument_tillagd_info, formateraDatum(idag, locale)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun dokumentKategoriEtikett(kategori: DokumentKategori?): String =
    when (kategori) {
        null -> stringResource(R.string.dokument_kategori_saknas)
        DokumentKategori.MANUAL -> stringResource(R.string.dokument_kategori_manual)
        DokumentKategori.RITNING -> stringResource(R.string.dokument_kategori_ritning)
        DokumentKategori.BESIKTNING -> stringResource(R.string.dokument_kategori_besiktning)
        DokumentKategori.GARANTI -> stringResource(R.string.dokument_kategori_garanti)
        DokumentKategori.AVTAL -> stringResource(R.string.dokument_kategori_avtal)
        DokumentKategori.OVRIGT -> stringResource(R.string.dokument_kategori_ovrigt)
    }
