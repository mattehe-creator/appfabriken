package se.tmconnect.husloggen.regler

import java.time.LocalDate

fun nastaDatum(senastGjord: LocalDate?, startDatum: LocalDate, intervallManader: Int): LocalDate {
    if (senastGjord == null) {
        return startDatum
    }
    
    return senastGjord.plusMonths(intervallManader.toLong())
}
