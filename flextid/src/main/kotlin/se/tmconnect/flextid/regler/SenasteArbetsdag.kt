package se.tmconnect.flextid.regler

import java.time.LocalDate
import se.tmconnect.flextid.data.Dag

/**
 * Senaste avslutade arbetsdag (typ ARBETE med både in- och utstämpling) före [datum].
 * Används för att fylla i en ny dag med samma tider som senast.
 */
fun senasteArbetsdagFore(dagar: List<Dag>, datum: LocalDate): Dag? =
    dagar
        .filter { it.datum.isBefore(datum) && it.typ == DagTyp.ARBETE && it.start != null && it.slut != null }
        .maxByOrNull { it.datum }
