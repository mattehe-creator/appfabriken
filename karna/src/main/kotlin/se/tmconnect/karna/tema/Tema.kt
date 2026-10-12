package se.tmconnect.karna.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val AppfabrikKortForm = RoundedCornerShape(16.dp)

private val AppfabrikFormer = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = AppfabrikKortForm,
    large = AppfabrikKortForm,
    extraLarge = AppfabrikKortForm,
)

private val LjusFargschema = lightColorScheme(
    primary = ColorOf(AppfabrikFarger.LjusPrimarArgb),
    onPrimary = ColorOf(AppfabrikFarger.LjusTextPaPrimarArgb),
    primaryContainer = ColorOf(AppfabrikFarger.LjusPrimarContainerArgb),
    onPrimaryContainer = ColorOf(AppfabrikFarger.LjusTextPaPrimarContainerArgb),
    secondary = ColorOf(AppfabrikFarger.LjusSekundarArgb),
    onSecondary = ColorOf(AppfabrikFarger.LjusTextPaSekundarArgb),
    secondaryContainer = ColorOf(AppfabrikFarger.LjusSekundarContainerArgb),
    onSecondaryContainer = ColorOf(AppfabrikFarger.LjusTextPaSekundarContainerArgb),
    tertiary = ColorOf(AppfabrikFarger.LjusTertiarArgb),
    onTertiary = ColorOf(AppfabrikFarger.LjusTextPaTertiarArgb),
    tertiaryContainer = ColorOf(AppfabrikFarger.LjusTertiarContainerArgb),
    onTertiaryContainer = ColorOf(AppfabrikFarger.LjusTextPaTertiarContainerArgb),
    background = ColorOf(AppfabrikFarger.LjusBakgrundArgb),
    onBackground = ColorOf(AppfabrikFarger.LjusTextArgb),
    surface = ColorOf(AppfabrikFarger.LjusYtaArgb),
    onSurface = ColorOf(AppfabrikFarger.LjusTextArgb),
    surfaceVariant = ColorOf(AppfabrikFarger.LjusYtaVariantArgb),
    onSurfaceVariant = ColorOf(AppfabrikFarger.LjusSekundarTextArgb),
    surfaceDim = ColorOf(AppfabrikFarger.LjusSurfaceDimArgb),
    surfaceBright = ColorOf(AppfabrikFarger.LjusSurfaceBrightArgb),
    surfaceContainerLowest = ColorOf(AppfabrikFarger.LjusSurfaceContainerLowestArgb),
    surfaceContainerLow = ColorOf(AppfabrikFarger.LjusSurfaceContainerLowArgb),
    surfaceContainer = ColorOf(AppfabrikFarger.LjusSurfaceContainerArgb),
    surfaceContainerHigh = ColorOf(AppfabrikFarger.LjusSurfaceContainerHighArgb),
    surfaceContainerHighest = ColorOf(AppfabrikFarger.LjusSurfaceContainerHighestArgb),
    error = ColorOf(AppfabrikFarger.LjusFelArgb),
    onError = ColorOf(AppfabrikFarger.LjusTextPaFelArgb),
    errorContainer = ColorOf(AppfabrikFarger.LjusFelContainerArgb),
    onErrorContainer = ColorOf(AppfabrikFarger.LjusTextPaFelContainerArgb),
    outline = ColorOf(AppfabrikFarger.LjusKantArgb),
    outlineVariant = ColorOf(AppfabrikFarger.LjusKantVariantArgb),
)

private val MorkFargschema = darkColorScheme(
    primary = ColorOf(AppfabrikFarger.MorkPrimarArgb),
    onPrimary = ColorOf(AppfabrikFarger.MorkTextPaPrimarArgb),
    primaryContainer = ColorOf(AppfabrikFarger.MorkPrimarContainerArgb),
    onPrimaryContainer = ColorOf(AppfabrikFarger.MorkTextPaPrimarContainerArgb),
    secondary = ColorOf(AppfabrikFarger.MorkSekundarArgb),
    onSecondary = ColorOf(AppfabrikFarger.MorkTextPaSekundarArgb),
    secondaryContainer = ColorOf(AppfabrikFarger.MorkSekundarContainerArgb),
    onSecondaryContainer = ColorOf(AppfabrikFarger.MorkTextPaSekundarContainerArgb),
    tertiary = ColorOf(AppfabrikFarger.MorkTertiarArgb),
    onTertiary = ColorOf(AppfabrikFarger.MorkTextPaTertiarArgb),
    tertiaryContainer = ColorOf(AppfabrikFarger.MorkTertiarContainerArgb),
    onTertiaryContainer = ColorOf(AppfabrikFarger.MorkTextPaTertiarContainerArgb),
    background = ColorOf(AppfabrikFarger.MorkBakgrundArgb),
    onBackground = ColorOf(AppfabrikFarger.MorkTextArgb),
    surface = ColorOf(AppfabrikFarger.MorkYtaArgb),
    onSurface = ColorOf(AppfabrikFarger.MorkTextArgb),
    surfaceVariant = ColorOf(AppfabrikFarger.MorkYtaVariantArgb),
    onSurfaceVariant = ColorOf(AppfabrikFarger.MorkSekundarTextArgb),
    surfaceDim = ColorOf(AppfabrikFarger.MorkSurfaceDimArgb),
    surfaceBright = ColorOf(AppfabrikFarger.MorkSurfaceBrightArgb),
    surfaceContainerLowest = ColorOf(AppfabrikFarger.MorkSurfaceContainerLowestArgb),
    surfaceContainerLow = ColorOf(AppfabrikFarger.MorkSurfaceContainerLowArgb),
    surfaceContainer = ColorOf(AppfabrikFarger.MorkSurfaceContainerArgb),
    surfaceContainerHigh = ColorOf(AppfabrikFarger.MorkSurfaceContainerHighArgb),
    surfaceContainerHighest = ColorOf(AppfabrikFarger.MorkSurfaceContainerHighestArgb),
    error = ColorOf(AppfabrikFarger.MorkFelArgb),
    onError = ColorOf(AppfabrikFarger.MorkTextPaFelArgb),
    errorContainer = ColorOf(AppfabrikFarger.MorkFelContainerArgb),
    onErrorContainer = ColorOf(AppfabrikFarger.MorkTextPaFelContainerArgb),
    outline = ColorOf(AppfabrikFarger.MorkKantArgb),
    outlineVariant = ColorOf(AppfabrikFarger.MorkKantVariantArgb),
)

fun appfabrikLjusFargschema() = LjusFargschema

fun appfabrikMorkFargschema() = MorkFargschema

private fun ColorOf(argb: Long) = androidx.compose.ui.graphics.Color(argb)

@Composable
fun AppfabrikTema(
    morkt: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (morkt) MorkFargschema else LjusFargschema,
        typography = Typography(),
        shapes = AppfabrikFormer,
        content = content,
    )
}
