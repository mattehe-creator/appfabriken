package se.tmconnect.garantivalvet.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KvittoHelskarm(
    kvittoUri: Uri,
    onTillbaka: () -> Unit,
    onDela: (Uri) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    // Empty navigation icon to maintain consistent layout
                },
                actions = {
                    // We'll add the share button in a separate action
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // For now, we'll show a simple image - zoom functionality will be added in later task
            Image(
                bitmap = kvittoUri.asImageBitmap(),
                contentDescription = stringResource(R.string.kvitto_fornhandsvisning),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
