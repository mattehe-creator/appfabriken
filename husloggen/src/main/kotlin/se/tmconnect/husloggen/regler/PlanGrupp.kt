package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.PlaneradKostnad

data class PlanArGrupp(
    val ar: Int,
    val kostnader: List<PlaneradKostnad>,
    val summaOre: Long,
)

fun grupperaPlaneradeKostnaderPerAr(kostnader: List<PlaneradKostnad>): List<PlanArGrupp> =
    kostnader
        .groupBy { it.planeratAr }
        .toSortedMap()
        .map { (ar, rader) ->
            PlanArGrupp(
                ar = ar,
                kostnader = rader.sortedBy { it.titel.lowercase() },
                summaOre = rader.sumOf { it.beloppOre },
            )
        }
