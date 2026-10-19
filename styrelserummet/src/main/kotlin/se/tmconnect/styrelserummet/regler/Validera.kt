package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

data class Fel(val kod: String, val meddelande: String)

fun validera(k: Komponent, nuAr: Int): List<Fel> {
    val fel = mutableListOf<Fel>()
    
    if (k.nyttjandeperiodAr < 1) {
        fel.add(Fel("NYTTJANDEPERIOD", "Nyttjandeperiod måste vara minst 1 år"))
    }
    
    if (k.anskaffningsvarde < 0) {
        fel.add(Fel("NEGATIVT_VARDE", "Anskaffningsvärde får inte vara negativt"))
    }
    
    if (k.restvarde > k.anskaffningsvarde) {
        fel.add(Fel("RESTVARDE", "Restvärde får inte vara större än anskaffningsvärde"))
    }
    
    if (k.anskaffningsar < 1800 || k.anskaffningsar > nuAr + 1) {
        fel.add(Fel("ANSKAFFNINGSAR", "Anskaffningsår måste vara mellan 1800 och nuvarande år + 1"))
    }
    
    return fel
}
