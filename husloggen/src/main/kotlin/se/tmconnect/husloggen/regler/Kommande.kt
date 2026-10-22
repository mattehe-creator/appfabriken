package se.tmconnect.husloggen.regler

import java.time.LocalDate
import java.time.YearMonth
import se.tmconnect.husloggen.data.Uppgift

data class KommandeJobb(
    val uppgiftId: Long,
    val namn: String,
    val datum: LocalDate,
    val forsenad: Boolean,
)

fun kommandePerManad(
    uppgifter: List<Uppgift>,
    idag: LocalDate,
    manader: Int = 12,
): Map<YearMonth, List<KommandeJobb>> {
    val slut = idag.plusMonths(manader.toLong()).minusDays(1)
    val map = mutableMapOf<YearMonth, MutableList<KommandeJobb>>()

    for (uppgift in uppgifter) {
        val datumLista =
            forekomster(
                uppgift.senastGjord,
                uppgift.startDatum,
                uppgift.intervallManader,
                idag,
                slut,
            )
        for (datum in datumLista) {
            val forsenad = datum.isBefore(idag)
            val manad =
                if (forsenad) {
                    YearMonth.from(idag)
                } else {
                    YearMonth.from(datum)
                }
            map.getOrPut(manad) { mutableListOf() }.add(
                KommandeJobb(
                    uppgiftId = uppgift.id,
                    namn = uppgift.namn,
                    datum = datum,
                    forsenad = forsenad,
                ),
            )
        }
    }

    return map
        .mapValues { (_, jobb) -> jobb.sortedWith(compareBy({ it.datum }, { it.namn })) }
        .toSortedMap()
}

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
