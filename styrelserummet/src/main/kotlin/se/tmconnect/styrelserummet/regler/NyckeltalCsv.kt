package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

fun nyckeltalCsv(perAr: List<Pair<Int, List<NyckeltalRad>>>): String {
    if (perAr.isEmpty()) {
        return "nyckeltal\n$UTKAST_RAD\n"
    }
    val ar = perAr.map { it.first }
    val header = listOf("nyckeltal") + ar.map { it.toString() }
    val etiketter = perAr.first().second.map { it.etikett }
    val dataRader = etiketter.mapIndexed { i, etikett ->
        val varden = perAr.map { (_, rader) -> rader[i].varde }
        listOf(etikett) + varden
    }
    val csv = (listOf(header) + dataRader).joinToString("\n") { rad ->
        rad.joinToString(";") { csvCell(it) }
    }
    return "$csv\n$UTKAST_RAD\n"
}

