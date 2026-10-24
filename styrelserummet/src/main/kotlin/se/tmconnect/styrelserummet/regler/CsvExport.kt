package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

fun tillCsv(komponenter: List<Komponent>, arsslut: Int): String {
    val header = listOf(
        "namn",
        "grupp",
        "anskaffningsvärde",
        "anskaffningsår",
        "nyttjandeperiod",
        "restvärde",
        "avskrivning året",
        "bokfört värde",
        "källa",
        "säkerhet",
    )
    val rader = komponenter.map { k ->
        listOf(
            k.namn,
            k.grupp.svNamn,
            k.anskaffningsvarde.toString(),
            k.anskaffningsar.toString(),
            k.nyttjandeperiodAr.toString(),
            k.restvarde.toString(),
            avskrivningFor(k, arsslut).toString(),
            bokfortVarde(k, arsslut).toString(),
            k.kalla,
            k.sakerhet.name,
        )
    }
    val csv = (listOf(header) + rader).joinToString("\n") { rad ->
        rad.joinToString(";") { csvCell(it) }
    }
    return "$csv\n$UTKAST_RAD\n"
}

