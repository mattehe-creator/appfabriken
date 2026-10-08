package se.tmconnect.karna.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LjusFargschema = lightColorScheme(
    primary = AppfabrikFarger.LjusPrimar,
    onPrimary = AppfabrikFarger.LjusTextPaPrimar,
    background = AppfabrikFarger.LjusBakgrund,
    onBackground = AppfabrikFarger.LjusText,
    surface = AppfabrikFarger.LjusYta,
    onSurface = AppfabrikFarger.LjusText,
    onSurfaceVariant = AppfabrikFarger.LjusSekundarText,
    error = AppfabrikFarger.LjusFel,
    onError = AppfabrikFarger.LjusTextPaPrimar,
)

private val MorkFargschema = darkColorScheme(
    primary = AppfabrikFarger.MorkPrimar,
    onPrimary = AppfabrikFarger.MorkTextPaPrimar,
    background = AppfabrikFarger.MorkBakgrund,
    onBackground = AppfabrikFarger.MorkText,
    surface = AppfabrikFarger.MorkYta,
    onSurface = AppfabrikFarger.MorkText,
    onSurfaceVariant = AppfabrikFarger.MorkSekundarText,
    error = AppfabrikFarger.MorkFel,
    onError = AppfabrikFarger.MorkTextPaPrimar,
)

@Composable
fun AppfabrikTema(
    morkt: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (morkt) MorkFargschema else LjusFargschema,
        content = content,
    )
}
