package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponentgrupp

data class NyttjandeperiodRad(
    val grupp: Komponentgrupp,
    val minAr: Int,
    val maxAr: Int,
    val forslagAr: Int,
    val andelProcent: Int,
)

fun lasNyttjandeperioder(): List<NyttjandeperiodRad> {
    val stream = checkNotNull(
        NyttjandeperiodRad::class.java.classLoader.getResourceAsStream("nyttjandeperioder.csv"),
    ) { "Saknar nyttjandeperioder.csv" }
    return stream.bufferedReader().useLines { lines ->
        lines.drop(1).mapNotNull { rad ->
            val del = rad.split(';')
            if (del.size < 5) return@mapNotNull null
            val grupp = Komponentgrupp.franNamn(del[0].trim()) ?: return@mapNotNull null
            val intervall = del[1].trim()
            val (min, max) = intervall.split('-').let { p ->
                p[0].trim().toInt() to p.getOrElse(1) { p[0] }.trim().toInt()
            }
            NyttjandeperiodRad(
                grupp = grupp,
                minAr = min,
                maxAr = max,
                forslagAr = del[2].trim().toInt(),
                andelProcent = del[3].trim().toInt(),
            )
        }.toList()
    }
}

fun standardAndelar(): List<Pair<Komponentgrupp, Int>> =
    lasNyttjandeperioder().map { it.grupp to it.andelProcent }

fun forslagNyttjandeperiod(grupp: Komponentgrupp): Int? =
    lasNyttjandeperioder().firstOrNull { it.grupp == grupp }?.forslagAr

fun arInomIntervall(grupp: Komponentgrupp, ar: Int): Boolean {
    val rad = lasNyttjandeperioder().firstOrNull { it.grupp == grupp } ?: return true
    return ar in rad.minAr..rad.maxAr
}
