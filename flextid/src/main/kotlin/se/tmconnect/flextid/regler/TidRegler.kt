package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

sealed interface TidValideringFel {
    data object SlutForeStart : TidValideringFel
}

fun arbetadeMinuter(
    start: LocalTime?,
    slut: LocalTime?,
    rastMin: Int,
): Int {
    if (start == null || slut == null) return 0
    if (!slut.isAfter(start)) return 0
    val brutto = ChronoUnit.MINUTES.between(start, slut).toInt()
    return (brutto - rastMin).coerceAtLeast(0)
}

fun valideraTider(start: LocalTime?, slut: LocalTime?): TidValideringFel? {
    if (start == null || slut == null) return null
    if (!slut.isAfter(start)) return TidValideringFel.SlutForeStart
    return null
}
