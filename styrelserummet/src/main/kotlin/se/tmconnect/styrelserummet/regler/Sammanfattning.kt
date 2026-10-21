package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Sakerhet

fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String> {
    val antal = komponenter.size
    val summa = kronor(komponenter.sumOf { it.anskaffningsvarde })
    val uppskattade = komponenter.count {
        it.sakerhet == Sakerhet.UPPSKATTNING || it.sakerhet == Sakerhet.LAG
    }
    val osakerhetMening =
        "$uppskattade komponenter (${procent(uppskattade.toLong(), antal.toLong())}) har uppskattat värde eller låg säkerhet."

    val avstamningMening = if (byggnadensVarde == null) {
        "Byggnadens värde saknas, så avstämning görs inte."
    } else {
        val avstamningsresultat = stamAv(komponenter, byggnadensVarde)
        if (avstamningsresultat.stammer) {
            "Komponenternas anskaffningsvärde stämmer med byggnadens värde."
        } else {
            "Skillnaden mot byggnadens värde är ${kronor(kotlin.math.abs(avstamningsresultat.differens))}."
        }
    }

    val avskrivningSumma = komponenter.sumOf { avskrivningFor(it, arsslut) }
    val avskrivningMening =
        "Avskrivningen för räkenskapsåret är ${kronor(avskrivningSumma)} totalt."
    val utkastMening =
        "Detta är ett utkast – kontrollera uppgifterna mot handlingarna innan beslut."

    return listOf(
        "Listan innehåller $antal komponenter.",
        "Anskaffningsvärdet är $summa sammanlagt.",
        osakerhetMening,
        avstamningMening,
        avskrivningMening,
        utkastMening,
    )
}
