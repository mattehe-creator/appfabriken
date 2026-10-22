package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp

data class GruppPlan(val grupp: Komponentgrupp, val avskrivningPerAr: List<Long>)

fun avskrivningsplanPerGrupp(komponenter: List<Komponent>, franAr: Int, antalAr: Int): List<GruppPlan> {
    require(antalAr <= 100) { "Högst 100 år." }
    if (antalAr <= 0) return emptyList()
    val perGrupp = komponenter.groupBy { it.grupp }
    return Komponentgrupp.entries.mapNotNull { grupp ->
        val lista = perGrupp[grupp] ?: return@mapNotNull null
        GruppPlan(grupp, (0 until antalAr).map { i -> lista.sumOf { avskrivningFor(it, franAr + i) } })
    }
}
