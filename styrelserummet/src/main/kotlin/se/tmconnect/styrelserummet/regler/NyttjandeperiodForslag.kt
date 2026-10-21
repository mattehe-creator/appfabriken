package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponentgrupp

fun forslagFor(grupp: Komponentgrupp, tabell: List<NyttjandeperiodRad>): Int? {
    if (grupp == Komponentgrupp.MARK_OCH_GARAGE) {
        return null
    }
    
    val rad = tabell.find { it.grupp == grupp } ?: return null
    return rad.forslagAr
}
