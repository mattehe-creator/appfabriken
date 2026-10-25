package se.tmconnect.husloggen.ui

import se.tmconnect.husloggen.regler.senasteKostnadPerUppgift
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import se.tmconnect.husloggen.regler.filtreraPoster
import se.tmconnect.husloggen.data.PostTyp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale
import se.tmconnect.husloggen.BuildConfig
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.BostadBorttagningSatt
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.Matare
import se.tmconnect.husloggen.data.PlaneradKostnad
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.grupperaPerAr
import se.tmconnect.husloggen.regler.KommandeKostnad
import se.tmconnect.husloggen.regler.kommandePerManad
import se.tmconnect.karna.format.formateraPrisKr
import se.tmconnect.karna.tema.TemaLage

enum class HusFlik { ATT_GORA, LOGG, PLAN, MATARE, DOKUMENT, SAMMANSTALLNING, MER, KOMMANDE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HusStartSkarm(
    uppgifter: List<Uppgift>,
    poster: List<Post>,
    matare: List<Matare>,
    dokument: List<Dokument>,
    planeradeKostnader: List<PlaneradKostnad>,
    bostader: List<Bostad> = emptyList(),
    valdBostad: Bostad? = null,
    onValjBostad: (Long) -> Unit = {},
    onSparaNyBostad: (String, String, String, (Boolean) -> Unit) -> Unit = { _, _, _, _ -> },
    onUppdateraBostad: (Bostad) -> Unit = {},
    onTaBortBostad: (Bostad, BostadBorttagningSatt, flyttTillId: Long?, onSista: () -> Unit, onBorttagen: () -> Unit) -> Unit = { _, _, _, _, _ -> },
    hamtaRaderingInfo: suspend (Long) -> Int = { 0 },
    onInstallningar: () -> Unit,
    onNyUppgift: () -> Unit,
    onNyPost: () -> Unit,
    onNyMatare: () -> Unit,
    onNyttDokument: () -> Unit,
    onNyPlaneradKostnad: () -> Unit,
    onAndraPlaneradKostnad: (Long) -> Unit,
    onGenomfordPlaneradKostnad: (Long) -> Unit,
    onOppnaUppgift: (Long) -> Unit,
    onOppnaPost: (Long) -> Unit,
    onOppnaMatare: (Long) -> Unit,
    onOppnaDokument: (Long) -> Unit,
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
    onVisaKommandeAr: () -> Unit = {},
    senasteKostnadPerUppgift: Map<Long, Long> = emptyMap(),
    kommandeKostnad: KommandeKostnad = KommandeKostnad(0, 0, 0),
    startFlik: HusFlik = HusFlik.ATT_GORA,
    modifier: Modifier = Modifier,
) {
    var valdFlik by rememberSaveable { mutableIntStateOf(startFlik.ordinal) }
    LaunchedEffect(oppnaAttGora) {
        if (oppnaAttGora) {
            valdFlik = HusFlik.ATT_GORA.ordinal
        }
    }
    BackHandler(enabled = HusFlik.entries[valdFlik] in MER_FLIKAR) {
        valdFlik = HusFlik.MER.ordinal
    }
    val locale = LocalConfiguration.current.locales[0]
    val visaSkatteInfo = locale.country.equals("SE", ignoreCase = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(R.string.app_name))
                        BostadValjareRad(
                            bostader = bostader,
                            valdBostad = valdBostad,
                            onValjBostad = onValjBostad,
                        )
                    }
                },
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
            HusNavigering(
                valdFlik = HusFlik.entries[valdFlik],
                onValjFlik = { valdFlik = it.ordinal },
            )
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
                HusFlik.PLAN -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyPlaneradKostnad,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.fab_ny_planerad_kostnad),
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_ny_planerad_kostnad)) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
                HusFlik.MATARE -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyMatare,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.fab_ny_matare),
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_ny_matare)) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
                HusFlik.DOKUMENT -> {
                    ExtendedFloatingActionButton(
                        onClick = onNyttDokument,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.fab_nytt_dokument),
                            )
                        },
                        text = { Text(text = stringResource(R.string.fab_nytt_dokument)) },
                        modifier = Modifier.tryckyta48(),
                    )
                }
                HusFlik.SAMMANSTALLNING, HusFlik.MER, HusFlik.KOMMANDE -> {
                    // Ingen flytande knapp i sammanställningen eller under Mer
                }
            }
        },
    ) { innerPadding ->
        val senasteKostnad = remember(poster) { senasteKostnadPerUppgift(poster) }
        when (HusFlik.entries[valdFlik]) {
            HusFlik.ATT_GORA -> AttGoraFlik(
                uppgifter = uppgifter,
                senasteKostnadPerUppgift = senasteKostnadPerUppgift,
                contentPadding = innerPadding,
                onNyUppgift = onNyUppgift,
                onOppnaUppgift = onOppnaUppgift,
                onMarkeraGjord = onMarkeraGjord,
                paminnelserPa = paminnelserPa,
                onPaminnelserAndras = onPaminnelserAndras,
                behorighetNekad = behorighetNekad,
                senasteKostnad = senasteKostnad,
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
            HusFlik.PLAN -> PlanFlik(
                kostnader = planeradeKostnader,
                locale = locale,
                contentPadding = innerPadding,
                onNyPlaneradKostnad = onNyPlaneradKostnad,
                onAndraPlaneradKostnad = onAndraPlaneradKostnad,
                onGenomford = onGenomfordPlaneradKostnad,
            )
            HusFlik.MATARE -> MatareFlik(
                matare = matare,
                contentPadding = innerPadding,
                onNyMatare = onNyMatare,
                onOppnaMatare = onOppnaMatare,
            )
            HusFlik.DOKUMENT -> {
                val locale = LocalConfiguration.current.locales[0]
                DokumentFlik(
                    dokument = dokument,
                    locale = locale,
                    contentPadding = innerPadding,
                    onNyttDokument = onNyttDokument,
                    onOppnaDokument = onOppnaDokument,
                )
            }
            HusFlik.KOMMANDE -> KommandeFlik(
                uppgifter = uppgifter,
                senasteKostnad = senasteKostnad,
                locale = locale,
                contentPadding = innerPadding,
                onOppnaUppgift = onOppnaUppgift,
            )
            HusFlik.MER -> MerFlik(
                antalKommande = remember(uppgifter) {
                    kommandePerManad(uppgifter, java.time.LocalDate.now()).values.sumOf { it.size }
                },
                antalPlanerade = planeradeKostnader.size,
                antalMatare = matare.size,
                antalDokument = dokument.size,
                contentPadding = innerPadding,
                onValjFlik = { valdFlik = it.ordinal },
            )
            HusFlik.SAMMANSTALLNING -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    BostaderInstallningSektion(
                        bostader = bostader,
                        valdBostadId = valdBostad?.id,
                        onValjBostad = onValjBostad,
                        onSparaNyBostad = onSparaNyBostad,
                        onUppdateraBostad = onUppdateraBostad,
                        onTaBortBostad = onTaBortBostad,
                        hamtaRaderingInfo = hamtaRaderingInfo,
                    )
                    SammanstallningSkarm(
                        poster = poster,
                        kommandeKostnad = kommandeKostnad,
                        forsaljningsAr = forsaljningsAr,
                        onForsaljningsArAndras = onForsaljningsArAndras,
                        locale = locale,
                        visaSkatteInfo = visaSkatteInfo,
                        modifier = Modifier.weight(1f),
                        onExportPdf = onExportHistorikPdf,
                        onExportCsv = onExportHistorikCsv,
                        onVisaKommandeAr = onVisaKommandeAr,
                    )
                }
            }
        }
    }
}

@Composable
private fun AttGoraFlik(
    uppgifter: List<Uppgift>,
    senasteKostnadPerUppgift: Map<Long, Long>,
    contentPadding: PaddingValues,
    onNyUppgift: () -> Unit,
    onOppnaUppgift: (Long) -> Unit,
    onMarkeraGjord: (Long) -> Unit,
    paminnelserPa: Boolean = false,
    onPaminnelserAndras: (Boolean) -> Unit = {},
    behorighetNekad: Boolean = false,
    senasteKostnad: Map<Long, Long> = emptyMap(),
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
                        senasteKostnadOre = senasteKostnadPerUppgift[uppgift.id],
                        onClick = { onOppnaUppgift(uppgift.id) },
                        onMarkeraGjord = { onMarkeraGjord(uppgift.id) },
                        senastKostnadOre = senasteKostnad[uppgift.id],
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

    var sok by rememberSaveable { mutableStateOf("") }
    var typFilter by rememberSaveable { mutableStateOf<String?>(null) }
    val filtrerade = remember(poster, sok, typFilter) {
        filtreraPoster(poster, sok, typFilter?.let { PostTyp.valueOf(it) })
    }
    val grupperadePoster = grupperaPerAr(filtrerade)

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
        item(key = "sok") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sok,
                    onValueChange = { sok = it },
                    label = { Text(stringResource(R.string.logg_sok)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    FilterChip(
                        selected = typFilter == null,
                        onClick = { typFilter = null },
                        label = { Text(stringResource(R.string.logg_filter_alla)) },
                    )
                    PostTyp.entries.forEach { typ ->
                        FilterChip(
                            selected = typFilter == typ.name,
                            onClick = { typFilter = if (typFilter == typ.name) null else typ.name },
                            label = { Text(stringResource(postTypKortRes(typ))) },
                        )
                    }
                }
            }
        }
        if (filtrerade.isEmpty()) {
            item(key = "inga-traffar") {
                Text(
                    text = stringResource(R.string.logg_inga_traffar),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
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
    temaLage: TemaLage = TemaLage.SYSTEM,
    onTemaLage: (TemaLage) -> Unit = {},
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
                                .heightIn(min = 48.dp)
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

private fun postTypKortRes(typ: PostTyp): Int =
    when (typ) {
        PostTyp.UNDERHALL -> R.string.logg_filter_underhall
        PostTyp.REPARATION -> R.string.logg_filter_reparation
        PostTyp.OMBYGGNAD -> R.string.logg_filter_ombyggnad
    }
