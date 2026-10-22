package se.tmconnect.husloggen.regler

import java.time.LocalDate
import se.tmconnect.husloggen.data.Avlasning

fun arsprognos(avlasningar: List<Avlasning>, ar: Int, idag: LocalDate): Long? {
    val sorterade = sorteraAvlasningar(avlasningar)
    
    // Hitta första avläsningen före 1 januari år
    val forsta = sorterade.lastOrNull { it.datum.isBefore(LocalDate.of(ar, 1, 1)) }
        ?: sorterade.firstOrNull { it.datum.year == ar }
    
    // Hitta sista avläsningen till och med idag inom året
    val sista = sorterade.lastOrNull { 
        it.datum.year == ar && !it.datum.isAfter(idag) 
    }
    
    // Behöver både första och sista avläsning
    if (forsta == null || sista == null) {
        return null
    }
    
    // Om det är samma avläsning
    if (forsta.id == sista.id) {
        return null
    }
    
    val forstaDatum = forsta.datum
    val sistaDatum = sista.datum
    
    // Kontrollera att perioden är minst 14 dagar
    val dagar = java.time.temporal.ChronoUnit.DAYS.between(forstaDatum, sistaDatum).toInt()
    if (dagar < 14) {
        return null
    }
    
    val forstaVarde = forsta.varde
    val sistaVarde = sista.varde
    
    // Negativ diff (mätarbyte)
    if (sistaVarde < forstaVarde) {
        return null
    }
    
    val diff = sistaVarde - forstaVarde
    
    // Beräkna förbrukning per dag och sedan årsprognos
    // Använd formeln: (diff * dagarIAret * 2 + dagar) / (2 * dagar)
    val dagarIAret = if (LocalDate.of(ar, 1, 1).isLeapYear) 366 else 365
    return (diff * dagarIAret * 2 + dagar) / (2 * dagar)
}
