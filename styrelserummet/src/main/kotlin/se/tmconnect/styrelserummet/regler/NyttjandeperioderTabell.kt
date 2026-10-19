package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponentgrupp

data class NyttjandeperiodRad(
    val grupp: Komponentgrupp,
    val intervallAr: IntRange?,
    val forslagAr: Int,
    val standardandelProcent: Int,
)

fun lasNyttjandeperioder(rader: List<String>): List<NyttjandeperiodRad> {
    return rader.drop(1).mapNotNull { rad ->
        val delar = rad.split(";")
        if (delar.size < 4) return@mapNotNull null
        val grupp = runCatching { Komponentgrupp.valueOf(delar[0].trim()) }.getOrNull() ?: return@mapNotNull null
        val intervall = parseIntervall(delar[1].trim())
        val forslag = delar[2].trim().toIntOrNull() ?: return@mapNotNull null
        val andel = delar[3].trim().toIntOrNull() ?: return@mapNotNull null
        NyttjandeperiodRad(grupp, intervall, forslag, andel)
    }
}

fun lasNyttjandeperioderFranResurs(): List<NyttjandeperiodRad> {
    val text = checkNotNull(
        NyttjandeperioderTabell::class.java.classLoader.getResourceAsStream("nyttjandeperioder.csv"),
    ) { "nyttjandeperioder.csv saknas" }.bufferedReader().use { it.readText() }
    return lasNyttjandeperioder(text.lines())
}

private fun parseIntervall(text: String): IntRange? {
    if (text == "0" || text.isBlank()) return null
    val match = Regex("""(\d+)\s*-\s*(\d+)""").find(text) ?: return null
    val (min, max) = match.destructured
    return min.toInt()..max.toInt()
}

object NyttjandeperioderTabell
