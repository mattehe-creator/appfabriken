package se.tmconnect.karna.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppfabrikStatus {
    GALLER,
    SNART,
    REKLAMATION,
    UTGANGEN,
}

data class StatusChipFarger(
    val container: Color,
    val content: Color,
)

@Composable
fun statusChipFarger(status: AppfabrikStatus): StatusChipFarger {
    val scheme = MaterialTheme.colorScheme
    return when (status) {
        AppfabrikStatus.GALLER -> StatusChipFarger(
            container = scheme.primaryContainer,
            content = scheme.onPrimaryContainer,
        )
        AppfabrikStatus.SNART -> StatusChipFarger(
            container = scheme.tertiaryContainer,
            content = scheme.onTertiaryContainer,
        )
        AppfabrikStatus.REKLAMATION -> StatusChipFarger(
            container = scheme.secondaryContainer,
            content = scheme.onSecondaryContainer,
        )
        AppfabrikStatus.UTGANGEN -> StatusChipFarger(
            container = scheme.errorContainer,
            content = scheme.onErrorContainer,
        )
    }
}
