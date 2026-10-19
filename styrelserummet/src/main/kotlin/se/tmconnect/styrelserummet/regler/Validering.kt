package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Fel
import se.tmconnect.styrelserummet.modell.Komponent

fun validera(k: Komponent, nuAr: Int): List<Fel> {
    val fel = mutableListOf<Fel>()
    if (k.nyttjandeperiodAr < 1) {
        fel += Fel("nyttjandeperiod", "Nyttjandeperiod måste vara minst 1 år.")
    }
    if (k.anskaffningsvarde < 0) {
        fel += Fel("varde", "Anskaffningsvärdet får inte vara negativt.")
    }
    if (k.restvarde > k.anskaffningsvarde) {
        fel += Fel("restvarde", "Restvärdet får inte överstiga anskaffningsvärdet.")
    }
    if (k.anskaffningsar < 1800) {
        fel += Fel("ar", "Anskaffningsåret är för tidigt.")
    }
    if (k.anskaffningsar > nuAr + 1) {
        fel += Fel("ar", "Anskaffningsåret ligger för långt fram i tiden.")
    }
    return fel
}
