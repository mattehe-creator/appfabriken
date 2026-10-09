package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

fun sorteraEfterGaranti(lista: List<Kop>, idag: LocalDate): List<Kop> {
    fun slut(kop: Kop) = garantiSlut(kop.kopdatum, kop.garantiManader)

    val (aktiva, utgangna) = lista.partition { kop -> !idag.isAfter(slut(kop)) }

    val sorteradeAktiva = aktiva.sortedWith(
        compareBy<Kop> { slut(it) }.thenBy { it.vad },
    )
    val sorteradeUtgangna = utgangna.sortedWith(
        compareByDescending<Kop> { slut(it) }.thenBy { it.vad },
    )

    return sorteradeAktiva + sorteradeUtgangna
}
