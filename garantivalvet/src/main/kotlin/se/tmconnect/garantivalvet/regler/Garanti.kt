package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class GarantiStatus {
    GALLER,
    GAR_UT_SNART,
    UTGANGEN
}

const val SNART_DAGAR = 30

fun garantiSlut(kopdatum: LocalDate, manader: Int): LocalDate {
    if (manader < 0) {
        throw IllegalArgumentException("Antal månader får inte vara negativt")
    }
    return kopdatum.plusMonths(manader.toLong())
}

fun garantiStatus(kopdatum: LocalDate, manader: Int, idag: LocalDate): GarantiStatus {
    val slut = garantiSlut(kopdatum, manader)
    
    if (idag.isAfter(slut)) {
        return GarantiStatus.UTGANGEN
    }
    
    // Räkna antal dagar från idag till slut (inkluderande slutdagen)
    val dagarKvar = ChronoUnit.DAYS.between(idag, slut).toInt()
    return if (dagarKvar <= SNART_DAGAR) {
        GarantiStatus.GAR_UT_SNART
    } else {
        GarantiStatus.GALLER
    }
}
