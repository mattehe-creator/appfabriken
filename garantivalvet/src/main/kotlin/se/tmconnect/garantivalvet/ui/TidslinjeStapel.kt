package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import se.tmconnect.garantivalvet.regler.TidslinjeMarkorer

@Composable
fun TidslinjeStapel(
    markorer: TidslinjeMarkorer,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val bredd = configuration.screenWidthDp.dp
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .height(48.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val linjeHojd = 12.dp.toPx()
            val linjeStartX = 0f
            val linjeSlutX = bredd.toPx()
            val linjeY = size.height / 2
            
            // Rita huvudlinjen
            drawLine(
                color = Color.Gray,
                start = Offset(linjeStartX, linjeY),
                end = Offset(linjeSlutX, linjeY),
                strokeWidth = linjeHojd,
                cap = StrokeCap.Round
            )
            
            // Rita markörer
            val garantiSlutX = linjeStartX + (linjeSlutX - linjeStartX) * markorer.garantiSlutAndel
            val presumtionSlutX = linjeStartX + (linjeSlutX - linjeStartX) * markorer.presumtionSlutAndel
            val idagX = linjeStartX + (linjeSlutX - linjeStartX) * markorer.idagAndel
            
            // Garanti slut markör (blå)
            drawLine(
                color = Color.Blue,
                start = Offset(garantiSlutX, linjeY - 12.dp.toPx()),
                end = Offset(garantiSlutX, linjeY + 12.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Presumtion slut markör (orange)
            drawLine(
                color = Color(0xFFFFA500), // Orange
                start = Offset(presumtionSlutX, linjeY - 11.dp.toPx()),
                end = Offset(presumtionSlutX, linjeY + 11.dp.toPx()),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Idag markör (röd)
            drawLine(
                color = Color.Red,
                start = Offset(idagX, linjeY - 10.dp.toPx()),
                end = Offset(idagX, linjeY + 10.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
