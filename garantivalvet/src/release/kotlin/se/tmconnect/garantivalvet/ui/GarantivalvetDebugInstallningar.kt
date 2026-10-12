package se.tmconnect.garantivalvet.ui

import androidx.compose.runtime.Composable

@Composable
fun GarantivalvetDebugInstallningar(
    arPro: Boolean,
    onTestlageProAndras: (Boolean) -> Unit,
) {
    // Ingen testlägesrad i release-bygget.
}
