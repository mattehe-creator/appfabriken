package se.tmconnect.styrelserummet.regler

import java.time.LocalDate

fun senasteStammodag(arsslut: LocalDate): LocalDate {
    return arsslut.plusMonths(6)
}
