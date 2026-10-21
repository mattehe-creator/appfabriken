package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

enum class ForandringTyp { NY, BORTTAGEN, ANDRAD }

data class Forandring(val namn: String, val typ: ForandringTyp)

fun forandring(nu: List<Komponent>, foreg: List<Komponent>): List<Forandring> {
    val resultat = mutableListOf<Forandring>()

    val nuMap = nu.associateBy { it.namn.trim().lowercase() }
    val foregMap = foreg.associateBy { it.namn.trim().lowercase() }

    for ((normaliseratNamn, komponent) in nuMap) {
        if (normaliseratNamn !in foregMap) {
            resultat.add(Forandring(komponent.namn, ForandringTyp.NY))
        } else {
            val foregKomponent = foregMap[normaliseratNamn]!!
            if (komponent.anskaffningsvarde != foregKomponent.anskaffningsvarde ||
                komponent.nyttjandeperiodAr != foregKomponent.nyttjandeperiodAr ||
                komponent.anskaffningsar != foregKomponent.anskaffningsar
            ) {
                resultat.add(Forandring(komponent.namn, ForandringTyp.ANDRAD))
            }
        }
    }

    for ((normaliseratNamn, komponent) in foregMap) {
        if (normaliseratNamn !in nuMap) {
            resultat.add(Forandring(komponent.namn, ForandringTyp.BORTTAGEN))
        }
    }

    return resultat.sortedWith(compareBy { it.namn })
}
