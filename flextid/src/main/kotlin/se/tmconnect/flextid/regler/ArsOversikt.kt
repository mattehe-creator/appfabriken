package se.tmconnect.flextid.regler

import java.time.YearMonth
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

data class ManadsRad(val manad: Int, val arbetadMin: Int, val utfallMin: Int, val saldoVidSlutMin: Int)

fun arsOversikt(
    dagar: List<Dag>,
    ar: Int,
    installning: Installning,
    aftnarLediga: Boolean = true,
    arbetstid: Arbetstid = installning.arbetstid(aftnarLediga),
): List<ManadsRad> {
    return (1..12).map { manad ->
        val iManad = dagar.filter { it.datum.year == ar && it.datum.monthValue == manad }
        val arbetadMin =
            iManad
                .filter { it.typ == DagTyp.ARBETE }
                .sumOf { arbetadeMinuter(it.start, it.slut, it.rastMin) }
        val utfallMin = iManad.sumOf { dagensUtfall(it, arbetstid) }
        val slutManad = YearMonth.of(ar, manad).atEndOfMonth()
        val saldoUtfall =
            dagar
                .filter { !it.datum.isAfter(slutManad) }
                .sumOf { dagensUtfall(it, arbetstid) }
        ManadsRad(manad, arbetadMin, utfallMin, installning.ingangssaldoMin + saldoUtfall)
    }
}
