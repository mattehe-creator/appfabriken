package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.ForandringTyp

fun forandring(nu: List<Komponent>, foreg: List<Komponent>): List<Forandring> {
    val resultat = mutableListOf<Forandring>()
    
    // Skapa mappningar från normaliserat namn till komponenter för båda listorna
    val nuMap = nu.associateBy { it.namn.trim().lowercase() }
    val foregMap = foreg.associateBy { it.namn.trim().lowercase() }
    
    // Hitta nya komponenter (finns i nu men inte i foreg)
    for ((normaliseratNamn, komponent) in nuMap) {
        if (normaliseratNamn !in foregMap) {
            resultat.add(Forandring(komponent.namn, ForandringTyp.NY))
        } else {
            // Kontrollera om komponenten har ändrats
            val foregKomponent = foregMap[normaliseratNamn]!!
            if (komponent.anskaffningsvarde != foregKomponent.anskaffningsvarde ||
                komponent.nyttjandeperiodAr != foregKomponent.nyttjandeperiodAr ||
                komponent.anskaffningsar != foregKomponent.anskaffningsar) {
                resultat.add(Forandring(komponent.namn, ForandringTyp.ANDRAD))
            }
        }
    }
    
    // Hitta borttagna komponenter (finns i foreg men inte i nu)
    for ((normaliseratNamn, komponent) in foregMap) {
        if (normaliseratNamn !in nuMap) {
            resultat.add(Forandring(komponent.namn, ForandringTyp.BORTTAGEN))
        }
    }
    
    // Sortera resultatet alfabetiskt
    return resultat.sortedWith(compareBy<Forandring> { it.namn })
}
