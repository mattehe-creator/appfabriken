package se.tmconnect.garantivalvet.regler

import java.time.LocalDate

data class TidslinjeMarkorer(
    val garantiSlutAndel: Float,
    val presumtionSlutAndel: Float,
    val idagAndel: Float,
)

fun beraknaTidslinje(kopdatum: LocalDate, garantiManader: Int, idag: LocalDate): TidslinjeMarkorer? {
    if (kopdatum.isBefore(KONSUMENTKOP_IKRAFT)) {
        return null
    }
    
    val garantiSlut = garantiSlut(kopdatum, garantiManader)
    val reklamationSlut = reklamationSlut(kopdatum)!!
    
    // Beräkna totala intervallet i dagar
    val totalDagar = java.time.temporal.ChronoUnit.DAYS.between(kopdatum, reklamationSlut).toDouble()
    
    // Om det inte finns något intervall, returnera null
    if (totalDagar <= 0) {
        return null
    }
    
    // Beräkna positioner i intervallet
    val garantiSlutDagar = java.time.temporal.ChronoUnit.DAYS.between(kopdatum, garantiSlut).toDouble()
    val presumtionSlutDagar = java.time.temporal.ChronoUnit.DAYS.between(kopdatum, presumtionSlut(kopdatum)!!).toDouble()
    val idagDagar = java.time.temporal.ChronoUnit.DAYS.between(kopdatum, idag).toDouble()
    
    // Konvertera till andelar (0f..1f)
    val garantiSlutAndel = (garantiSlutDagar / totalDagar).toFloat().coerceIn(0f, 1f)
    val presumtionSlutAndel = (presumtionSlutDagar / totalDagar).toFloat().coerceIn(0f, 1f)
    val idagAndel = (idagDagar / totalDagar).toFloat().coerceIn(0f, 1f)
    
    return TidslinjeMarkorer(garantiSlutAndel, presumtionSlutAndel, idagAndel)
}
