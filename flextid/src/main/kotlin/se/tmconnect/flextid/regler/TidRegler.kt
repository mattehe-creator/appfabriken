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

fun dagensUtfall(
    dag: Dag,
    normaltidMinPerDag: Int,
    arbetsdagarMask: Int,
): Int {
    when (dag.typ) {
        DagTyp.LEDIG, DagTyp.SEMESTER, DagTyp.SJUK, DagTyp.HELGDAG -> return 0
        DagTyp.ARBETE -> {
            val arbetat = arbetadeMinuter(dag.start, dag.slut, dag.rastMin)
            if (!arArbetsdag(dag.datum, arbetsdagarMask)) {
                return arbetat
            }
            return arbetat - normaltidMinPerDag
        }
    }
}

fun beraknaSaldo(dagar: List<Dag>, installning: Installning): Int {
    val summaUtfall = dagar.sumOf { dagensUtfall(it, installning.normaltidMinPerDag, installning.arbetsdagarMask) }
    return installning.ingangssaldoMin + summaUtfall
}

fun mandagISammaVecka(datum: LocalDate): LocalDate =
    datum.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

fun sondagISammaVecka(datum: LocalDate): LocalDate =
    mandagISammaVecka(datum).plusDays(6)
