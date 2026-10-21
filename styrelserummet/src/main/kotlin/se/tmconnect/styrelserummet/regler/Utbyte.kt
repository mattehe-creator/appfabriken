package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun utrangeringsbelopp(k: Komponent, utbytesAr: Int): Long {
    if (utbytesAr <= k.anskaffningsar) {
        return k.anskaffningsvarde
    }
    return bokfortVarde(k, utbytesAr - 1)
}
