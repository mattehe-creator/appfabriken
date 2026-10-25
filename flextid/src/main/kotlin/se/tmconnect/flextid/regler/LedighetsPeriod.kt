package se.tmconnect.flextid.regler

import java.time.LocalDate
import se.tmconnect.flextid.data.Dag

/** Längsta period som registreras i ett svep. */
const val MAX_LEDIGHETSPERIOD_DAGAR = 366

/** Typer som kan registreras för en period (inte Arbete och inte Helgdag). */
val LEDIGHETSTYPER_FOR_PERIOD = listOf(DagTyp.SEMESTER, DagTyp.FLEX, DagTyp.SJUK, DagTyp.LEDIG)

/**
 * Dagposter för ledighet från [fran] till och med [till]: bara arbetsdagar enligt [arbetstid]
 * (inte helger, röda dagar eller lediga aftnar) och bara datum som inte redan har en post.
 * Tom lista om perioden är baklänges, längre än [MAX_LEDIGHETSPERIOD_DAGAR] eller [typ] inte är en ledighet.
 */
fun dagarForLedighetsperiod(
    fran: LocalDate,
    till: LocalDate,
    typ: DagTyp,
    arbetstid: Arbetstid,
    befintligaDatum: Set<LocalDate>,
): List<Dag> {
    if (typ !in LEDIGHETSTYPER_FOR_PERIOD) return emptyList()
    if (till.isBefore(fran)) return emptyList()
    if (java.time.temporal.ChronoUnit.DAYS.between(fran, till) >= MAX_LEDIGHETSPERIOD_DAGAR) return emptyList()
    val ut = mutableListOf<Dag>()
    var datum = fran
    while (!datum.isAfter(till)) {
        if (arbetstid.arArbetsdag(datum) && datum !in befintligaDatum) {
            ut += Dag(datum = datum, rastMin = 0, typ = typ)
        }
        datum = datum.plusDays(1)
    }
    return ut
}
