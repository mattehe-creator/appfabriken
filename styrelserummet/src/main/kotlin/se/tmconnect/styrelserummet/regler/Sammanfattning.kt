package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Sakerhet

fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String> {
    val antal = komponenter.size
    val summa = kronor(komponenter.sumOf { it.anskaffningsvarde })
    
    // Räkna komponenter med uppskattat värde eller låg säkerhet
    val uppskattadeEllerLågSakerhet = komponenter.count { 
        it.sakerhet == Sakerhet.UPPSKATTNING || it.sakerhet == Sakerhet.LAG 
    }
    val procent = procent(uppskattadeEllerLågSakerhet, antal)
    
    // Avstämning
    val avstamning = if (byggnadensVarde == null) {
        "Byggnadens värde saknas, så avstämning görs inte."
    } else {
        val avstamningsresultat = stamAv(komponenter, byggnadensVarde)
        if (avstamningsresultat.stammer) {
            "Komponenternas anskaffningsvärde stämmer med byggnadens värde."
        } else {
            "Skillnaden mot byggnadens värde är ${kronor(avstamningsresultat.differens.absoluteValue)}."
        }
    }
    
    return listOf(
        "Listan innehåller $antal komponenter.",
        "Anskaffningsvärdet är $summa sammanlagt.",
        "P komponenter (Q) har uppskattat värde eller låg säkerhet." 
            .replace("P", uppskattadeEllerLågSakerhet.toString())
            .replace("Q", "$procent %"),
        avstamning
    )
}

// Hjälpfunktion för att beräkna procent
fun procent(del: Int, hela: Int): Double {
    return if (hela == 0) 0.0 else (del.toDouble() / hela * 100)
}
