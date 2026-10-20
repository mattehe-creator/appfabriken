package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp

data class GruppSumma(
    val grupp: Komponentgrupp,
    val anskaffning: Long,
    val avskrivningAret: Long,
    val bokfort: Long,
)

fun summeraPerGrupp(komponenter: List<Komponent>, arsslut: Int): List<GruppSumma> {
    if (komponenter.isEmpty()) return emptyList()
    val perGrupp = komponenter.groupBy { it.grupp }
    return Komponentgrupp.entries
        .filter { perGrupp.containsKey(it) }
        .map { grupp ->
            val iGruppen = perGrupp.getValue(grupp)
            GruppSumma(
                grupp = grupp,
                anskaffning = iGruppen.sumOf { it.anskaffningsvarde },
                avskrivningAret = iGruppen.sumOf { avskrivningFor(it, arsslut) },
                bokfort = iGruppen.sumOf { bokfortVarde(it, arsslut) },
            )
        }
}
