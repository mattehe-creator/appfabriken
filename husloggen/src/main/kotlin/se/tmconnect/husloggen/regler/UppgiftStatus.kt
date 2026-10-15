package se.tmconnect.husloggen.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class UppgiftStatus {
    FORSENAD,
    SNART,
    SENARE
}

const val UPPGIFT_SNART_DAGAR = 30

fun uppgiftStatus(nasta: LocalDate, idag: LocalDate): UppgiftStatus {
    if (nasta.isBefore(idag)) {
        return UppgiftStatus.FORSENAD
    }
    
    // Räkna antal dagar från idag till nästa datum (inkluderande nästa datum)
    val dagarKvar = ChronoUnit.DAYS.between(idag, nasta).toInt()
    return if (dagarKvar <= UPPGIFT_SNART_DAGAR) {
        UppgiftStatus.SNART
    } else {
        UppgiftStatus.SENARE
    }
}
