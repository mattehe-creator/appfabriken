package se.tmconnect.flextid.regler

import java.time.LocalTime

fun gaHemTid(start: LocalTime, rastMin: Int, normaltidMin: Int): LocalTime? {
    val rast = rastMin.coerceAtLeast(0)
    val normaltid = normaltidMin.coerceAtLeast(0)
    
    val totalMin = start.toSecondOfDay() / 60 + rast + normaltid
    
    return if (totalMin >= 24 * 60) {
        null
    } else {
        LocalTime.of(totalMin / 60, totalMin % 60)
    }
}

fun gaHemTidForNollsaldo(
    start: LocalTime,
    rastMin: Int,
    normaltidMin: Int,
    saldoForeIdagMin: Int,
): LocalTime? {
    val rast = rastMin.coerceAtLeast(0)
    val normaltid = normaltidMin.coerceAtLeast(0)
    
    // Beräkna arbete som behövs för att nå nollsaldo
    val arbete = normaltid - saldoForeIdagMin
    
    // Om arbete är <= 0 har användaren redan nog med plus
    if (arbete <= 0) {
        return start
    }
    
    val totalMin = start.toSecondOfDay() / 60 + rast + arbete
    
    return if (totalMin >= 24 * 60) {
        null
    } else {
        LocalTime.of(totalMin / 60, totalMin % 60)
    }
}
