package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Sakerhet

fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String> {
    val antal = komponenter.size
    val summa = kronor(komponenter.sumOf { it.anskaffningsvarde })
    
    // Räkna komponenter med uppskattat värde eller låg säkerhet
    val uppskattade = komponenter.count { it.sakerhet == Sakerhet.UPPSKATTNING || it.sakerhet == Sakerhet.LAG }
    val procent = if (antal > 0) {
        String.format("%.1f", uppskattade.toDouble() / antal * 100)
    } else {
        "0,0"
    }
    
    // Avstämning
    val avstamning = if (byggnadensVarde == null) {
        listOf("Byggnadens värde saknas, så avstämning görs inte.")
    } else {
        val avstamningsresultat = stamAv(komponenter, byggnadensVarde)
        if (avstamningsresultat.stammer) {
            listOf("Komponenternas anskaffningsvärde stämmer med byggnadens värde.")
        } else {
            listOf("Skillnaden mot byggnadens värde är ${kronor(kotlin.math.abs(avstamningsresultat.differens))}.")
        }
    }
    
    return listOf(
        "Listan innehåller $antal komponenter.",
        "Anskaffningsvärdet är $summa sammanlagt.",
        "P komponenter (Q) har uppskattat värde eller låg säkerhet." 
            .replace("P", uppskattade.toString())
            .replace("Q", "$procent %"),
        avstamning[0]
    )
}
