package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr

private enum class HusFlik { ATT_GORA, LOGG }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HusStartSkarm(
    uppgifter: List<Uppgift>,
    poster: List<Post>,
    onInstallningar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var valdFlik by rememberSaveable { mutableIntStateOf(HusFlik.ATT_GORA.ordinal) }
    val locale = LocalConfiguration.current.locales[0]

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
            }
        },
    ) { innerPadding ->
        when (HusFlik.entries[valdFlik]) {
            HusFlik.ATT_GORA ->             AttGoraFlik(
                uppgifter = uppgifter,
                contentPadding = innerPadding,
            )
            HusFlik.LOGG -> LoggFlik(
                poster = poster,
                locale = locale,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun AttGoraFlik(
    uppgifter: List<Uppgift>,
    contentPadding: PaddingValues,
) {
    if (uppgifter.isEmpty()) {
        TomFlikText(
            text = stringResource(R.string.tom_uppgifter),
            contentPadding = contentPadding,
        )
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(uppgifter, key = { it.id }) { uppgift ->
            UppgiftListRad(uppgift = uppgift, onClick = { })
        }
    }
}

@Composable
private fun LoggFlik(
    poster: List<Post>,
    locale: Locale,
    contentPadding: PaddingValues,
) {
    if (poster.isEmpty()) {
        TomFlikText(
            text = stringResource(R.string.tom_logg),
            contentPadding = contentPadding,
        )
        return
    }
    
    val grupperadePoster = grupperaPerAr(poster)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
    ) {
        grupperadePoster.forEach { arGrupp ->
            item {
                Text(
                    text = "${arGrupp.ar} (${formateraPrisKr(arGrupp.summaOre, locale)})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                )
            }
            
            items(arGrupp.poster) { post ->
                PostListRad(post = post, locale = locale)
            }
        }
    }
}

@Composable
private fun TomFlikText(
    text: String,
    contentPadding: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallningarSkarm(
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier,
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
        ) {
            Text(
                text = stringResource(R.string.installningar_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
