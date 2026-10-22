package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

fun fondplanCsv(utveckling: List<FondAr>): String {
    val header = "år;avsättning;uttag;fond vid årets slut"
    val rader = utveckling.map { "${it.ar};${it.avsattning};${it.uttag};${it.utgaende}" }
    val csv = (listOf(header) + rader).joinToString("\n")
    return "$csv\n$UTKAST_RAD\n"
}
