package se.tmconnect.husloggen.regler

import java.time.LocalDate
import se.tmconnect.husloggen.data.Avlasning

data class AvlasningMedForbrukning(
    val avlasning: Avlasning,
    val periodForbrukning: Long?,
)

fun sorteraAvlasningar(avlasningar: List<Avlasning>): List<Avlasning> =
    avlasningar.sortedWith(compareBy<Avlasning> { it.datum }.thenBy { it.id })

fun beraknaAvlasningarMedForbrukning(avlasningar: List<Avlasning>): List<AvlasningMedForbrukning> {
    val sorterade = sorteraAvlasningar(avlasningar)
    var foregaendeVarde: Long? = null
    return sorterade.map { avlasning ->
        val period =
            if (foregaendeVarde != null && avlasning.varde >= foregaendeVarde) {
                avlasning.varde - foregaendeVarde!!
            } else {
                null
            }
        foregaendeVarde = avlasning.varde
        AvlasningMedForbrukning(avlasning, period)
    }
}

fun beraknaArsforbrukning(medForbrukning: List<AvlasningMedForbrukning>): Map<Int, Long> {
    val perAr = linkedMapOf<Int, Long>()
    for (rad in medForbrukning) {
        val forbrukning = rad.periodForbrukning ?: continue
        val ar = rad.avlasning.datum.year
        perAr[ar] = (perAr[ar] ?: 0L) + forbrukning
    }
    return perAr
}

fun senasteAvlasningForeDatum(datum: LocalDate, avlasningar: List<Avlasning>): Avlasning? =
    sorteraAvlasningar(avlasningar).lastOrNull { !it.datum.isAfter(datum) }
