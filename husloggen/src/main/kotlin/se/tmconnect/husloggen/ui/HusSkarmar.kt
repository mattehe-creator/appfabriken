package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale
import se.tmconnect.husloggen.BuildConfig
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.grupperaPerAr
import se.tmconnect.karna.format.formateraPrisKr

private enum class HusFlik { ATT_GORA, LOGG, SAMMANSTALLNING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HusStartSkarm(
    uppgifter: List<Uppgift>,
    poster: List<Post>,
    onInstallningar: () -> Unit,
    onNyUppgift: () -> Unit,
    onNyPost: () -> Unit,
    onOppnaUppgift: (Long) -> Unit,
    onOppnaPost: (Long) -> Unit,
    onMarkeraGjord: (Long) -> Unit,
    oppnaAttGora: Boolean = false,
    paminnelserPa: Boolean = false,
    onPaminnelserAndras: (Boolean) -> Unit = {},
    behorighetNekad: Boolean = false,
    arPro: Boolean = false,
    onVisaProDialog: () -> Unit = {},
    forsaljningsAr: Int = java.time.LocalDate.now().year,
    onForsaljningsArAndras: (Int) -> Unit = {},
    onExportHistorikPdf: () -> Unit = {},
    onExportHistorikCsv: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var valdFlik by rememberSaveable { mutableIntStateOf(HusFlik.ATT_GORA.ordinal) }
    LaunchedEffect(oppnaAttGora) {
        if (oppnaAttGora) {
            valdFlik = HusFlik.ATT_GORA.ordinal
        }
    }
    val locale = LocalConfiguration.current.locales[0]
    val visaSkatteInfo = locale.country.equals("SE", ignoreCase = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onInstallningar, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.installningar_rubrik),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = valdFlik == HusFlik.ATT_GORA.ordinal,
                    onClick = { valdFlik = HusFlik.ATT_GORA.ordinal },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(text = stringResource(R.string.flik_att_gora)) },
                )
                NavigationBarItem(
                    selected = valdFlik == HusFlik.LOGG.ordinal,
                    onClick = { valdFlik = HusFlik.LOGG.ordinal },
                    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    label = { Text(text = stringResource(R.string.flik_logg)) },
                )
                NavigationBarItem(
                    selected = valdFlik == HusFlik.SAMMANSTALLNING.ordinal,
                    onClick = { valdFlik = HusFlik.SAMMANSTALLNING.ordinal },
                    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    label = { Text(text = stringResource(R.string.flik_sammanstallning)) },
                )
            }
        },
        floatingActionButton = {
            when (HusFlik.entries[valdFlik]) {
                HusFlik.ATT_GORA -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyUppgift,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_ny_uppgift)) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
                HusFlik.LOGG -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyPost,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_ny_post)) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
                HusFlik.SAMMANSTALLNING -> {
                    // Inget flytande knapp för sammanställningsfliken
                }
            }
        },
    ) { innerPadding ->
        when (HusFlik.entries[valdFlik]) {
            HusFlik.ATT_GORA -> AttGoraFlik(
                uppgifter = uppgifter,
                contentPadding = innerPadding,
                onNyUppgift = onNyUppgift,
                onOppnaUppgift = onOppnaUppgift,
                onMarkeraGjord = onMarkeraGjord,
                paminnelserPa = paminnelserPa,
                onPaminnelserAndras = onPaminnelserAndras,
                behorighetNekad = behorighetNekad,
            )
            HusFlik.LOGG -> LoggFlik(
                poster = poster,
                locale = locale,
                contentPadding = innerPadding,
                onNyPost = onNyPost,
                onOppnaPost = onOppnaPost,
                arPro = arPro,
                onVisaProDialog = onVisaProDialog,
            )
            HusFlik.SAMMANSTALLNING -> {
                SammanstallningSkarm(
                    poster = poster,
                    forsaljningsAr = forsaljningsAr,
                    onForsaljningsArAndras = onForsaljningsArAndras,
                    locale = locale,
                    visaSkatteInfo = visaSkatteInfo,
                    modifier = Modifier.padding(innerPadding),
                    onExportPdf = onExportHistorikPdf,
                    onExportCsv = onExportHistorikCsv,
                )
            }
        }
    }
}

@Composable
private fun AttGoraFlik(
    uppgifter: List<Uppgift>,
    contentPadding: PaddingValues,
    onNyUppgift: () -> Unit,
    onOppnaUppgift: (Long) -> Unit,
    onMarkeraGjord: (Long) -> Unit,
    paminnelserPa: Boolean = false,
    onPaminnelserAndras: (Boolean) -> Unit = {},
    behorighetNekad: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        AttGoraPaminnelseReglage(
            paminnelserPa = paminnelserPa,
            onPaminnelserAndras = onPaminnelserAndras,
            behorighetNekad = behorighetNekad,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )
        if (uppgifter.isEmpty()) {
            TomFlikText(
                text = stringResource(R.string.tom_uppgifter),
                contentPadding = PaddingValues(0.dp),
                onNyUppgift = onNyUppgift,
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(uppgifter, key = { it.id }) { uppgift ->
                    UppgiftListRad(
                        uppgift = uppgift,
                        onClick = { onOppnaUppgift(uppgift.id) },
                        onMarkeraGjord = { onMarkeraGjord(uppgift.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AttGoraPaminnelseReglage(
    paminnelserPa: Boolean,
    onPaminnelserAndras: (Boolean) -> Unit,
    behorighetNekad: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun LoggProRad(
    arPro: Boolean,
    onVisaProDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (arPro) {
        Text(
            text = stringResource(R.string.pro_du_har),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )
    } else {
        TextButton(
            onClick = onVisaProDialog,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(text = stringResource(R.string.pro_las_upp))
        }
    }
}

@Composable
private fun LoggFlik(
    poster: List<Post>,
    locale: Locale,
    contentPadding: PaddingValues,
    onNyPost: () -> Unit,
    onOppnaPost: (Long) -> Unit,
    arPro: Boolean = false,
    onVisaProDialog: () -> Unit = {},
) {
    if (poster.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            LoggProRad(
                arPro = arPro,
                onVisaProDialog = onVisaProDialog,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            TomFlikText(
                text = stringResource(R.string.tom_logg),
                contentPadding = PaddingValues(0.dp),
                onNyPost = onNyPost,
                modifier = Modifier.weight(1f),
            )
        }
        return
    }

    val grupperadePoster = grupperaPerAr(poster)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
    ) {
        item(key = "pro-rad") {
            LoggProRad(
                arPro = arPro,
                onVisaProDialog = onVisaProDialog,
            )
        }
        grupperadePoster.forEach { arGrupp ->
            item {
                val harBelopp = arGrupp.poster.any { it.beloppOre != null }
                val rubrik = if (harBelopp) {
                    "${arGrupp.ar} (${formateraPrisKr(arGrupp.summaOre, locale)})"
                } else {
                    arGrupp.ar.toString()
                }
                Text(
                    text = rubrik,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                )
            }

            items(arGrupp.poster, key = { it.id }) { post ->
                PostListRad(
                    post = post,
                    locale = locale,
                    onClick = { onOppnaPost(post.id) },
                )
            }
        }
    }
}

@Composable
private fun TomFlikText(
    text: String,
    contentPadding: PaddingValues,
    onNyUppgift: (() -> Unit)? = null,
    onNyPost: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            when {
                onNyUppgift != null -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyUppgift,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_ny_uppgift)) },
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .tryckyta48(),
                    )
                }
                onNyPost != null -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyPost,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_ny_post)) },
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .tryckyta48(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallningarSkarm(
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier,
    autoBackup: se.tmconnect.karna.sakerhetskopia.AutoBackupTillstand = se.tmconnect.karna.sakerhetskopia.AutoBackupTillstand(),
    onAutoBackupPa: (Boolean) -> Unit = {},
    onByttAutoBackupMapp: () -> Unit = {},
    onAutoBackupIntervall: (Int) -> Unit = {},
    onKorNuAutoBackup: () -> Unit = {},
    onSparaSakerhetskopia: () -> Unit = {},
    onHamtaSakerhetskopia: () -> Unit = {},
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = stringResource(R.string.installningar_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyLarge,
            )

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
            HusAutoBackupAvsnitt(
                tillstand = autoBackup,
                onPa = onAutoBackupPa,
                onByttMapp = onByttAutoBackupMapp,
                onIntervall = onAutoBackupIntervall,
                onKorNu = onKorNuAutoBackup,
            )
        }
    }
}
