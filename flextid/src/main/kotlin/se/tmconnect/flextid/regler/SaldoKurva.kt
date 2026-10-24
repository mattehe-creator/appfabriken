package se.tmconnect.flextid.regler

import java.time.LocalDate
import se.tmconnect.flextid.data.Dag

data class SaldoPunkt(val datum: LocalDate, val saldoMin: Int)

fun saldoKurva(
    dagar: List<Dag>,
    ingangssaldoMin: Int,
    arbetstid: Arbetstid,
    fran: LocalDate,
    till: LocalDate,
): List<SaldoPunkt> {
    // Om fran är efter till, returnera tom lista
    if (fran.isAfter(till)) return emptyList()
    
    // Hitta alla dagar före fran
    val startSaldo = dagar.filter { it.datum.isBefore(fran) }.sumOf { dagensUtfall(it, arbetstid) }
    val totaltStartSaldo = ingangssaldoMin + startSaldo
    
    // Beräkna saldot för varje dag i perioden
    val resultat = mutableListOf<SaldoPunkt>()
    var aktuelltSaldo = totaltStartSaldo
    
    var datum = fran
    while (!datum.isAfter(till)) {
        // Hitta den specifika dagen i listan
        val dag = dagar.find { it.datum == datum }
        
        // Lägg till utfallet för den dagen (eller 0 om ingen post finns)
        aktuelltSaldo += dagensUtfall(dag ?: Dag(datum), arbetstid)
        
        resultat.add(SaldoPunkt(datum, aktuelltSaldo))
        datum = datum.plusDays(1)
    }
    
    return resultat
}
