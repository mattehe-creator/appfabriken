package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class TidslinjeMarkorer(
    val garantiSlutAndel: Float,
    val presumtionSlutAndel: Float,
    val idagAndel: Float,
)

fun beraknaTidslinje(kopdatum: LocalDate, garantiManader: Int, idag: LocalDate): TidslinjeMarkorer? {
    val reklamationSlut = reklamationSlut(kopdatum) ?: return null
    val presumtionSlutDatum = presumtionSlut(kopdatum) ?: return null

    val garantiSlut = garantiSlut(kopdatum, garantiManader)
    val totalDagar = ChronoUnit.DAYS.between(kopdatum, reklamationSlut).toDouble()
    if (totalDagar <= 0) {
        return null
    }

    val garantiSlutDagar = ChronoUnit.DAYS.between(kopdatum, garantiSlut).toDouble()
    val presumtionSlutDagar = ChronoUnit.DAYS.between(kopdatum, presumtionSlutDatum).toDouble()
    val idagDagar = ChronoUnit.DAYS.between(kopdatum, idag).toDouble()
    
    // Konvertera till andelar (0f..1f)
    val garantiSlutAndel = (garantiSlutDagar / totalDagar).toFloat().coerceIn(0f, 1f)
    val presumtionSlutAndel = (presumtionSlutDagar / totalDagar).toFloat().coerceIn(0f, 1f)
    val idagAndel = (idagDagar / totalDagar).toFloat().coerceIn(0f, 1f)
    
    return TidslinjeMarkorer(garantiSlutAndel, presumtionSlutAndel, idagAndel)
}
