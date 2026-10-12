package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.regler.TidslinjeMarkorer

@Composable
fun TidslinjeStapel(
    markorer: TidslinjeMarkorer,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val garantiFarg = MaterialTheme.colorScheme.primary
    val presumtionFarg = MaterialTheme.colorScheme.tertiary
    val idagFarg = MaterialTheme.colorScheme.error

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            val linjeHojd = 12.dp.toPx()
            val linjeStartX = 0f
            val linjeSlutX = size.width
            val linjeY = size.height / 2

            drawLine(
                color = trackColor,
                start = Offset(linjeStartX, linjeY),
                end = Offset(linjeSlutX, linjeY),
                strokeWidth = linjeHojd,
                cap = StrokeCap.Round,
            )

            val garantiSlutX = linjeStartX + linjeSlutX * markorer.garantiSlutAndel
            val presumtionSlutX = linjeStartX + linjeSlutX * markorer.presumtionSlutAndel
            val idagX = linjeStartX + linjeSlutX * markorer.idagAndel

            drawLine(
                color = garantiFarg,
                start = Offset(garantiSlutX, linjeY - 12.dp.toPx()),
                end = Offset(garantiSlutX, linjeY + 12.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round,
            )

            drawLine(
                color = presumtionFarg,
                start = Offset(presumtionSlutX, linjeY - 11.dp.toPx()),
                end = Offset(presumtionSlutX, linjeY + 11.dp.toPx()),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )

            drawLine(
                color = idagFarg,
                start = Offset(idagX, linjeY - 10.dp.toPx()),
                end = Offset(idagX, linjeY + 10.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}
