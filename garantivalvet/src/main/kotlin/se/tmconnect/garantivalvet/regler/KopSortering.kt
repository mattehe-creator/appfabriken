package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

fun sorteraEfterGaranti(lista: List<Kop>, idag: LocalDate): List<Kop> {
    val aktiva = mutableListOf<Kop>()
    val utgångna = mutableListOf<Kop>()
    
    for (kop in lista) {
        val slut = garantiSlut(kop.kopdatum, kop.garantiManader)
        if (idag.isAfter(slut)) {
            utgångna.add(kop)
        } else {
            aktiva.add(kop)
        }
    }
    
    // Sortera aktiva efter garantislut (äldst först) och sedan efter vad
    aktiva.sortWith(
        compareByDescending<Kop> { garantiSlut(it.kopdatum, it.garantiManader) }
            .thenBy { it.vad }
    )
    
    // Sortera utgångna efter garantislut (senast först) och sedan efter vad
    utgångna.sortWith(
        compareBy<Kop> { garantiSlut(it.kopdatum, it.garantiManader) }
            .thenBy { it.vad }
    )
    
    return aktiva + utgångna
}
