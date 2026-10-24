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

    /** En tid eller rasten i dagformuläret går inte att tolka. */
    data object OgiltigTid : TidValideringFel

    /** Rasten är längre än tiden mellan start och slut. */
    data object RastLangreAnArbetstid : TidValideringFel
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

/**
 * Utfallet för en dag: arbetad tid minus dagens normaltid enligt [arbetstid].
 * Röda dagar och lediga aftnar har ingen normaltid, så arbete då blir plus.
 */
fun dagensUtfall(dag: Dag, arbetstid: Arbetstid): Int =
    when (dag.typ) {
        DagTyp.LEDIG, DagTyp.SEMESTER, DagTyp.SJUK, DagTyp.HELGDAG -> 0
        // Flexledig dag: saldot minskar med dagens normaltid (0 på lediga dagar).
        DagTyp.FLEX -> -arbetstid.normaltidFor(dag.datum)
        DagTyp.ARBETE -> {
            // En arbetsdag utan både in- och utstämpling är inte avslutad och räknas inte,
            // annars visar saldot minus en hel normaltid hela dagen tills man stämplar ut.
            if (dag.start == null || dag.slut == null) {
                0
            } else {
                arbetadeMinuter(dag.start, dag.slut, dag.rastMin) - arbetstid.normaltidFor(dag.datum)
            }
        }
    }

fun dagensUtfall(
    dag: Dag,
    normaltidMinPerDag: Int,
    arbetsdagarMask: Int,
    aftnarLediga: Boolean = true,
): Int = dagensUtfall(dag, Arbetstid(normaltidMinPerDag, arbetsdagarMask, aftnarLediga))

fun beraknaSaldo(dagar: List<Dag>, ingangssaldoMin: Int, arbetstid: Arbetstid): Int =
    ingangssaldoMin + dagar.sumOf { dagensUtfall(it, arbetstid) }

fun beraknaSaldo(dagar: List<Dag>, installning: Installning, aftnarLediga: Boolean = true): Int =
    beraknaSaldo(dagar, installning.ingangssaldoMin, installning.arbetstid(aftnarLediga))

fun mandagISammaVecka(datum: LocalDate): LocalDate =
    datum.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

fun sondagISammaVecka(datum: LocalDate): LocalDate =
    mandagISammaVecka(datum).plusDays(6)
