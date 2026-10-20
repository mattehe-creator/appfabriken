package se.tmconnect.styrelserummet.underlag

import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.NyttjandeperiodRad

fun anmarkningarFor(utkast: Utkast, tabell: List<NyttjandeperiodRad>, arsslut: Int): List<String> {
    val anmarkningar = utkast.anmarkningar.toMutableList()
    utkast.avstamning?.let { avstamning ->
        if (!avstamning.stammer) {
            anmarkningar += "Anskaffningsvärdena summerar till ${avstamning.summa} kr men byggnadens redovisade värde är ${utkast.byggnadensVarde ?: "?"} kr (differens ${avstamning.differens} kr)."
        }
    }
    utkast.komponenter.forEach { komponent ->
        if (komponent.sakerhet == Sakerhet.LAG) {
            anmarkningar += "Komponenten \"${komponent.namn}\" har låg säkerhet och bör kontrolleras."
        }
        if (komponent.sakerhet == Sakerhet.UPPSKATTNING) {
            anmarkningar += "Komponenten \"${komponent.namn}\" har uppskattat anskaffningsvärde enligt standardtabellen."
        }
        val rad = tabell.find { it.grupp == komponent.grupp }
        val intervall = rad?.intervallAr
        if (intervall != null && komponent.nyttjandeperiodAr !in intervall) {
            anmarkningar += "Komponenten \"${komponent.namn}\" har nyttjandeperiod ${komponent.nyttjandeperiodAr} år utanför tabellens intervall ${intervall.first}–${intervall.last} år för ${komponent.grupp.svNamn}."
        }
        if (komponent.anskaffningsar > arsslut) {
            anmarkningar += "Komponenten \"${komponent.namn}\" har anskaffningsår ${komponent.anskaffningsar} som ligger efter räkenskapsårets slut (${arsslut})."
        }
    }
    return anmarkningar.distinct()
}
