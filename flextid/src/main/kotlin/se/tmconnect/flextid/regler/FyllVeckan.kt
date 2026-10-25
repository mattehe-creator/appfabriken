package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag

/**
 * Arbetsdagar i veckan som börjar [mandag], till och med [idag], som saknar post: en post med de
 * vanliga tiderna. Framtida dagar, helger, röda dagar och lediga aftnar fylls aldrig. Tom lista
 * om sluttiden inte är efter starttiden.
 */
fun dagarAttFyllaMedVanligaTider(
    mandag: LocalDate,
    idag: LocalDate,
    arbetstid: Arbetstid,
    befintligaDatum: Set<LocalDate>,
    vanligStart: LocalTime,
    vanligSlut: LocalTime,
    vanligRastMin: Int,
): List<Dag> {
    if (!vanligSlut.isAfter(vanligStart)) return emptyList()
    return (0L..6L)
        .map { mandag.plusDays(it) }
        .filter { !it.isAfter(idag) && arbetstid.arArbetsdag(it) && it !in befintligaDatum }
        .map { Dag(datum = it, start = vanligStart, slut = vanligSlut, rastMin = vanligRastMin.coerceAtLeast(0), typ = DagTyp.ARBETE) }
}
