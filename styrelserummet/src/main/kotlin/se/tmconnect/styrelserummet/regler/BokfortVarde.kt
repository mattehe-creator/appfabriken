package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun bokfortVarde(k: Komponent, arsslut: Int): Long {
    var summaAvskrivning = 0L
    for (ar in k.anskaffningsar..arsslut) {
        summaAvskrivning += avskrivningFor(k, ar)
    }
    return maxOf(k.restvarde, k.anskaffningsvarde - summaAvskrivning)
}
