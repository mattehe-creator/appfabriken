package se.tmconnect.flextid.regler

import java.time.LocalDate
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

data class PeriodSammanfattning(val arbetadMin: Int, val normaltidMin: Int, val utfallMin: Int)

fun periodSammanfattning(
    dagar: List<Dag>,
    fran: LocalDate,
    till: LocalDate,
    installning: Installning,
    aftnarLediga: Boolean,
): PeriodSammanfattning {
    val iIntervall = dagar.filter { !it.datum.isBefore(fran) && !it.datum.isAfter(till) }
    val arbetadMin =
        iIntervall
            .filter { it.typ == DagTyp.ARBETE }
            .sumOf { arbetadeMinuter(it.start, it.slut, it.rastMin) }
    val utfallMin =
        iIntervall.sumOf {
            dagensUtfall(it, installning.normaltidMinPerDag, installning.arbetsdagarMask)
        }
    val normaltidMin =
        normaltidForIntervallMin(
            fran,
            till,
            installning.arbetsdagarMask,
            installning.normaltidMinPerDag,
            aftnarLediga,
        )
    return PeriodSammanfattning(arbetadMin, normaltidMin, utfallMin)
}
