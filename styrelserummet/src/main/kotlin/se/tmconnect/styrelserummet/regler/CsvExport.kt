package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun tillCsv(komponenter: List<Komponent>, arsslut: Int): String {
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
            add(
                listOf(
                    k.namn,
                    k.gruppEnum().svensktNamn,
                    k.anskaffningsvarde.toString(),
                    k.anskaffningsar.toString(),
                    k.nyttjandeperiodAr.toString(),
                    bokfortVarde(k, arsslut).toString(),
                    k.kalla,
                    k.sakerhet,
                ).joinToString(";") { csvFalt(it) },
            )
        }
        add(csvFalt("UTKAST. Beslutas av förening och revisor."))
    }
    return rader.joinToString("\n")
}

private fun csvFalt(text: String): String {
    val behoverCitat = text.contains(';') || text.contains('"') || text.contains('\n') || text.contains('\r')
    if (!behoverCitat) return text
    return "\"" + text.replace("\"", "\"\"") + "\""
}
