package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.regler.bokfortVarde
import se.tmconnect.styrelserummet.regler.csvCell
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

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
        add(rubriker.joinToString(";") { csvCell(it) })
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
                ).joinToString(";") { csvCell(it) },
            )
        }
        add(csvCell(UTKAST_RAD))
    }
    return rader.joinToString("\n")
}

