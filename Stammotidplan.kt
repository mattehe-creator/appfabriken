package se.tmconnect.styrelserummet.regler

import java.time.LocalDate

data class Stammotidplan(
    val arsslut: LocalDate,
    val stammodatum: LocalDate,
    val senasteStammodag: LocalDate,
    val tidigasteKallelsedag: LocalDate,
    val senasteKallelsedag: LocalDate,
    val senasteRevisionTillStyrelsen: LocalDate,
    val forstaDagArsredovisningTillganglig: LocalDate,
    val stammaInomLagligFrist: Boolean,
)

fun tidplan(
    arsslut: LocalDate,
    stammodatum: LocalDate,
    kallelsefristVeckor: IntRange,
): Stammotidplan {
    if (kallelsefristVeckor.isEmpty()) {
        throw IllegalArgumentException("Kallelsefristveckor får inte vara tomt")
    }
    
    if (kallelsefristVeckor.first !in 2..6 || kallelsefristVeckor.last !in 2..6) {
        throw IllegalArgumentException("Antal veckor före stämman måste vara mellan 2 och 6")
    }
    
    val senasteStammodag = senasteStammodag(arsslut)
    val tidigasteKallelsedag = tidigasteKallelsedag(stammodatum)
    val senasteKallelsedag = senasteKallelsedag(stammodatum, kallelsefristVeckor.first)
    val senasteRevisionTillStyrelsen = senasteRevisionTillStyrelsen(stammodatum)
    val forstaDagArsredovisningTillganglig = forstaDagArsredovisningTillganglig(stammodatum)
    val stammaInomLagligFrist = !stammodatum.isAfter(senasteStammodag)
    
    return Stammotidplan(
        arsslut,
        stammodatum,
        senasteStammodag,
        tidigasteKallelsedag,
        senasteKallelsedag,
        senasteRevisionTillStyrelsen,
        forstaDagArsredovisningTillganglig,
        stammaInomLagligFrist
    )
}

fun senasteStammodag(arsslut: LocalDate): LocalDate {
    return arsslut.plusMonths(6)
}

fun tidigasteKallelsedag(stammodatum: LocalDate): LocalDate {
    return stammodatum.minusWeeks(6L)
}

fun senasteKallelsedag(stammodatum: LocalDate, veckorForeStamma: Int): LocalDate {
    return stammodatum.minusWeeks(veckorForeStamma.toLong())
}

fun senasteRevisionTillStyrelsen(stammodatum: LocalDate): LocalDate {
    return stammodatum.minusWeeks(3L)
}

fun forstaDagArsredovisningTillganglig(stammodatum: LocalDate): LocalDate {
    return stammodatum.minusWeeks(2L)
}
