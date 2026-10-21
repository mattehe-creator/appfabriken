package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.regler.bokfortVarde

fun tillCsv(komponenter: List<KomponentDto>, arsslut: Int): String {
    val rubriker = listOf(
        "Komponent",
        "Grupp",
        "Anskaffningsvärde",
        "Anskaffningsår",
        "Nyttjandeperiod",
        "Bokfört värde",
        "Källa",
        "Säkerhet",
    )
    val rader = buildList {
        add(rubriker.joinToString(";") { csvFalt(it) })
        komponenter.forEach { k ->
            val modell = franDto(k)
            add(
                listOf(
                    k.namn,
                    modell.grupp.svNamn,
                    k.anskaffningsvarde.toString(),
                    k.anskaffningsar.toString(),
                    k.nyttjandeperiodAr.toString(),
                    bokfortVarde(modell, arsslut).toString(),
                    k.kalla,
                    k.sakerhet,
                ).joinToString(";") { csvFalt(it) },
            )
        }
        add(csvFalt(UTKAST_RAD))
    }
    return rader.joinToString("\n")
}

private fun csvFalt(text: String): String {
    val behoverCitat = text.contains(';') || text.contains('"') || text.contains('\n') || text.contains('\r')
    if (!behoverCitat) return text
    return "\"" + text.replace("\"", "\"\"") + "\""
}
