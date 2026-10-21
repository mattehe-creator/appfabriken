package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String> {
    val antal = komponenter.size
    val summa = kronor(komponenter.sumOf { it.anskaffningsvarde })
    return listOf(
        "Listan innehåller $antal komponenter.",
        "Anskaffningsvärdet är $summa sammanlagt.",
    )
}
