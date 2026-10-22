package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

fun avskrivningsplanCsv(plan: List<PlanAr>): String {
    val header = "år;avskrivning;bokfört värde vid årets slut"
    val rader = plan.map { "${it.ar};${it.avskrivning};${it.bokfortVarde}" }
    val csv = (listOf(header) + rader).joinToString("\n")
    return "$csv\n$UTKAST_RAD\n"
}
