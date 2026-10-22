package se.tmconnect.husloggen.regler

import java.time.LocalDate

fun forekomster(
    senastGjord: LocalDate?,
    startDatum: LocalDate,
    intervallManader: Int,
    fran: LocalDate,
    till: LocalDate,
): List<LocalDate> {
    if (intervallManader < 1) {
        throw IllegalArgumentException()
    }

    val result = mutableListOf<LocalDate>()

    for (k in 1..120) {
        val occurrence =
            if (senastGjord != null) {
                senastGjord.plusMonths(k * intervallManader.toLong())
            } else {
                startDatum.plusMonths(((k - 1) * intervallManader).toLong())
            }

        if (k == 1 && occurrence.isBefore(fran)) {
            result.add(occurrence)
            continue
        }

        if (occurrence.isAfter(till)) {
            break
        }

        if (!occurrence.isBefore(fran)) {
            result.add(occurrence)
        }
    }

    return result
}
