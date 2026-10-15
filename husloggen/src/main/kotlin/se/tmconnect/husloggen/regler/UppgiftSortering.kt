package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Uppgift
import java.time.LocalDate

fun sorteraUppgifter(uppgifter: List<Uppgift>, idag: LocalDate): List<Uppgift> {
    return uppgifter.sortedWith(
        compareBy<Uppgift> { nastaDatum(it.senastGjord, it.startDatum, it.intervallManader) }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.namn }
    )
}
