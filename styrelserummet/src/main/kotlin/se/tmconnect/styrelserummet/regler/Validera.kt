package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

data class Fel(val kod: String, val meddelande: String)

fun validera(k: Komponent, nuAr: Int): List<Fel> {
    val fel = mutableListOf<Fel>()
    if (k.nyttjandeperiodAr < 1) {
        fel += Fel("NYTTJANDEPERIOD", "Nyttjandeperiod måste vara minst 1 år.")
    }
    if (k.anskaffningsvarde < 0) {
        fel += Fel("NEGATIVT_VARDE", "Anskaffningsvärdet får inte vara negativt.")
    }
    if (k.restvarde > k.anskaffningsvarde) {
        fel += Fel("RESTVARDE", "Restvärdet får inte överstiga anskaffningsvärdet.")
    }
    if (k.anskaffningsar < 1800 || k.anskaffningsar > nuAr + 1) {
        fel += Fel("ANSKAFFNINGSAR", "Anskaffningsår är utanför tillåtet intervall.")
    }
    return fel
}
