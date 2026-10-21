package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.regler.avskrivningFor
import se.tmconnect.styrelserummet.regler.bokfortVarde
import se.tmconnect.styrelserummet.regler.kronor

data class UtskriftsRad(
    val grupp: String,
    val namn: String,
    val anskaffningsvarde: String,
    val nyttjandeperiod: String,
    val avskrivningAret: String,
    val bokfortVarde: String,
    val arSumma: Boolean = false,
)

fun utskriftsRader(komponenter: List<Komponent>, arsslut: Int): List<UtskriftsRad> {
    val sorterade = komponenter.sortedWith(compareBy({ it.grupp.svNamn }, { it.namn }))
    val komponentRader = sorterade.map { k ->
        UtskriftsRad(
            grupp = k.grupp.svNamn,
            namn = k.namn,
            anskaffningsvarde = kronor(k.anskaffningsvarde),
            nyttjandeperiod = "${k.nyttjandeperiodAr} år",
            avskrivningAret = kronor(avskrivningFor(k, arsslut)),
            bokfortVarde = kronor(bokfortVarde(k, arsslut)),
        )
    }
    if (komponentRader.isEmpty()) {
        return komponentRader
    }
    val summaRad = UtskriftsRad(
        grupp = "",
        namn = "Summa",
        anskaffningsvarde = kronor(sorterade.sumOf { it.anskaffningsvarde }),
        nyttjandeperiod = "",
        avskrivningAret = kronor(sorterade.sumOf { avskrivningFor(it, arsslut) }),
        bokfortVarde = kronor(sorterade.sumOf { bokfortVarde(it, arsslut) }),
        arSumma = true,
    )
    return komponentRader + summaRad
}
