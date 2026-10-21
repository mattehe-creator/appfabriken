package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponentgrupp

fun forslagFor(grupp: Komponentgrupp, tabell: List<NyttjandeperiodRad>): Int? {
    if (grupp == Komponentgrupp.MARK_OCH_GARAGE) {
        return null
    }

    val rad = tabell.find { it.grupp == grupp } ?: return null
    return rad.forslagAr
}

fun arInomIntervall(ar: Int, rad: NyttjandeperiodRad): Boolean {
    if (rad.intervallAr != null) {
        return ar in rad.intervallAr
    } else {
        return ar == rad.forslagAr
    }
}
