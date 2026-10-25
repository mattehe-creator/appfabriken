package se.tmconnect.rostfangst.regler

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun fangsterPerDag(
    tider: List<Instant>,
    idag: LocalDate,
    zon: ZoneId,
): List<Pair<LocalDate, Int>> {
    val start = idag.minusDays(29)
    val resultat = mutableMapOf<LocalDate, Int>()
    
    // Initiera alla dagar med 0
    for (i in 0..29) {
        resultat[start.plusDays(i.toLong())] = 0
    }
    
    // Räkna fångster per dag
    for (tid in tider) {
        val dag = tid.atZone(zon).toLocalDate()
        if (!dag.isBefore(start) && !dag.isAfter(idag)) {
            resultat[dag] = (resultat[dag] ?: 0) + 1
        }
    }
    
    // Returnera som lista av par
    return (0..29).map { offset ->
        val dag = start.plusDays(offset.toLong())
        dag to resultat[dag]!!
    }
}
