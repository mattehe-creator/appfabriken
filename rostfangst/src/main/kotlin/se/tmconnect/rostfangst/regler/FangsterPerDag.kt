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
    
    // Skapa en lista med 30 dagar bakåt inklusive idag
    val resultat = mutableListOf<Pair<LocalDate, Int>>()
    
    for (i in 0..29) {
        val dag = start.plusDays(i.toLong())
        var antal = 0
        
        // Räkna antal fångster för den dagen
        for (tid in tider) {
            val fangstDag = tid.atZone(zon).toLocalDate()
            if (fangstDag == dag) {
                antal++
            }
        }
        
        resultat.add(dag to antal)
    }
    
    return resultat
}
