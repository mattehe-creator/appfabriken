package se.tmconnect.styrelserummet.regler

import java.time.DayOfWeek
import java.time.LocalDate

private fun svenskVeckodag(dag: DayOfWeek): String = when (dag) {
    DayOfWeek.MONDAY -> "måndag"
    DayOfWeek.TUESDAY -> "tisdag"
    DayOfWeek.WEDNESDAY -> "onsdag"
    DayOfWeek.THURSDAY -> "torsdag"
    DayOfWeek.FRIDAY -> "fredag"
    DayOfWeek.SATURDAY -> "lördag"
    DayOfWeek.SUNDAY -> "söndag"
}

fun datumMedVeckodag(d: LocalDate): String {
    return "${d} (${svenskVeckodag(d.dayOfWeek)})"
}
