package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

fun sorteraEfterGaranti(lista: List<Kop>, idag: LocalDate): List<Kop> {
    return lista.sortedWith(
        compareByDescending<Kop> { kop ->
            val slut = garantiSlut(kop.kopdatum, kop.garantiManader)
            // Om garantin är utgången, använd en mycket tidig datum för att placera den sist
            if (idag.isAfter(slut)) {
                LocalDate.MIN
            } else {
                slut
            }
        }
        .thenBy { kop ->
            val slut = garantiSlut(kop.kopdatum, kop.garantiManader)
            // För utgångna köp, sortera efter slutdatum (äldst först)
            if (idag.isAfter(slut)) {
                slut
            } else {
                // För aktiva köp, använd en mycket sen datum för att placera dem först
                LocalDate.MAX
            }
        }
        .thenBy { kop ->
            kop.vad
        }
    )
}
