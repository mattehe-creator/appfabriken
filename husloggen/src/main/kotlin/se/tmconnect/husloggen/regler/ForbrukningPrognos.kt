package se.tmconnect.husloggen.regler

import java.time.LocalDate
import se.tmconnect.husloggen.data.Avlasning

fun arsprognos(avlasningar: List<Avlasning>, ar: Int, idag: LocalDate): Long? {
    val sorterade = sorteraAvlasningar(avlasningar)
    
    // Hitta första avläsningen före 1 januari år
    val forsta = sorterade.lastOrNull { it.datum.isBefore(LocalDate.of(ar, 1, 1)) }
        ?: sorterade.firstOrNull { it.datum.year == ar }
        ?: return null
    
    // Hitta sista avläsningen till och med idag inom året
    val sista = sorterade.lastOrNull { 
        it.datum.year == ar && !it.datum.isAfter(idag) 
    } ?: return null
    
    // Kontrollera att vi har två olika punkter och att perioden är minst 14 dagar
    if (forsta.id == sista.id || forsta.varde == sista.varde) {
        return null
    }
    
    val diff = sista.varde - forsta.varde
    if (diff < 0) {
        return null // Negativ diff (mätarbyte)
    }
    
    val antalDagar = java.time.temporal.ChronoUnit.DAYS.between(forsta.datum, sista.datum).toInt()
    
    if (antalDagar < 14) {
        return null
    }
    
    // Beräkna förbrukning per dag och sedan årets prognos
    // Använd formeln: (diff * dagarIAret * 2 + dagar) / (2 * dagar) för att få heltal
    val dagarIAret = if (LocalDate.of(ar, 1, 1).isLeapYear) 366 else 365
    return (diff * dagarIAret * 2 + antalDagar) / (2 * antalDagar)
}
