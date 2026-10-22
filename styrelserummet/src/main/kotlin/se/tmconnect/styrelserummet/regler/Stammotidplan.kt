package se.tmconnect.styrelserummet.regler

import java.time.LocalDate

fun senasteStammodag(arsslut: LocalDate): LocalDate {
    return arsslut.plusMonths(6)
}

fun tidigasteKallelsedag(stammodatum: LocalDate): LocalDate {
    return stammodatum.minusWeeks(6)
}

fun senasteKallelsedag(stammodatum: LocalDate, veckorForeStamma: Int): LocalDate {
    if (veckorForeStamma !in 2..6) {
        throw IllegalArgumentException("Antal veckor före stämman måste vara mellan 2 och 6")
    }
    return stammodatum.minusWeeks(veckorForeStamma)
}
