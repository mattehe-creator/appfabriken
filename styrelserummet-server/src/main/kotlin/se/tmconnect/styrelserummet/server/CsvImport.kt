package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

fun franExportCsv(text: String): List<Komponent> {
    val rader = text.lines().filter { it.isNotBlank() && !it.trim().startsWith(UTKAST_RAD) }
    if (rader.size <= 1) return emptyList()
    val dataRader = rader.drop(1)
    return dataRader.mapNotNull { rad ->
        val falt = lasCsvRad(rad)
        if (falt.size < 8) return@mapNotNull null
        val grupp = gruppFranExport(falt[1]) ?: Komponentgrupp.OVRIGT
        val sakerhet = runCatching { Sakerhet.valueOf(falt[7].trim()) }.getOrDefault(Sakerhet.MEDEL)
        Komponent(
            namn = falt[0],
            grupp = grupp,
            anskaffningsvarde = falt[2].trim().replace(" ", "").toLongOrNull() ?: return@mapNotNull null,
            anskaffningsar = falt[3].trim().toIntOrNull() ?: return@mapNotNull null,
            nyttjandeperiodAr = falt[4].trim().toIntOrNull() ?: return@mapNotNull null,
            restvarde = 0,
            kalla = falt[6],
            sakerhet = sakerhet,
        )
    }
}

private fun gruppFranExport(text: String): Komponentgrupp? =
    Komponentgrupp.entries.find { it.svNamn.equals(text.trim(), ignoreCase = false) }
        ?: runCatching { Komponentgrupp.valueOf(text.trim()) }.getOrNull()

private fun lasCsvRad(rad: String): List<String> {
    val out = mutableListOf<String>()
    val buf = StringBuilder()
    var i = 0
    var iCitat = false
    while (i < rad.length) {
        val c = rad[i]
        when {
            iCitat && c == '"' && i + 1 < rad.length && rad[i + 1] == '"' -> {
                buf.append('"')
                i += 2
                continue
            }
            c == '"' -> {
                iCitat = !iCitat
                i++
            }
            c == ';' && !iCitat -> {
                out += buf.toString()
                buf.clear()
                i++
            }
            else -> {
                buf.append(c)
                i++
            }
        }
    }
    out += buf.toString()
    return out
}
