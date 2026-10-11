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

fun sorteraEfterNarmasteFrist(lista: List<Kop>, idag: LocalDate): List<Kop> {
    fun garantiSlut(kop: Kop) = garantiSlut(kop.kopdatum, kop.garantiManader)
    fun reklamationSlut(kop: Kop) = reklamationSlut(kop.kopdatum)
    
    fun närmasteFrist(kop: Kop): LocalDate? {
        val garantiSlutDatum = garantiSlut(kop)
        
        // Om garantin inte är utgången, är närmaste frist garantislut
        if (!idag.isAfter(garantiSlutDatum)) {
            return garantiSlutDatum
        }
        
        // Annars är närmaste frist reklamationsslut om den existerar och inte är utgången
        val reklamationSlutDatum = reklamationSlut(kop)
        if (reklamationSlutDatum != null && !idag.isAfter(reklamationSlutDatum)) {
            return reklamationSlutDatum
        }
        
        // Om båda är utgångna, räknas köpet som utgånget
        return null
    }
    
    fun senastPasseradFrist(kop: Kop): LocalDate? {
        val garantiSlutDatum = garantiSlut(kop)
        val reklamationSlutDatum = reklamationSlut(kop)
        
        // Returnera den senaste av de två fristerna som är passerade
        return when {
            reklamationSlutDatum != null && idag.isAfter(reklamationSlutDatum) -> reklamationSlutDatum
            else -> garantiSlutDatum
        }
    }
    
    val (aktiva, utgangna) = lista.partition { kop ->
        närmasteFrist(kop) != null
    }
    
    val sorteradeAktiva = aktiva.sortedWith(
        compareBy<Kop> { närmasteFrist(it) }.thenBy { it.vad }
    )
    
    val sorteradeUtgangna = utgangna.sortedWith(
        compareByDescending<Kop> { senastPasseradFrist(it) }.thenBy { it.vad }
    )
    
    return sorteradeAktiva + sorteradeUtgangna
}
