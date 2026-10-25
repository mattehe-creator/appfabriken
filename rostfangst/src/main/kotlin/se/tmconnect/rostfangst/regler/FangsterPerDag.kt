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
    val antal = (0L..29L).associate { dag ->
        start.plusDays(dag) to 0
    }.toMutableMap()
    
    for (tid in tider) {
        val dag = tid.atZone(zon).toLocalDate()
        if (!dag.isBefore(start) && !dag.isAfter(idag)) {
            antal[dag] = (antal[dag] ?: 0) + 1
        }
    }
    
    return (0L..29L).map { offset ->
        val dag = start.plusDays(offset)
        dag to (antal[dag] ?: 0)
    }
}
