package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Sakerhet

fun utrangeringsbelopp(k: Komponent, utbytesAr: Int): Long {
    if (utbytesAr <= k.anskaffningsar) {
        return k.anskaffningsvarde
    }
    return bokfortVarde(k, utbytesAr - 1)
}

data class Utbyte(val gammal: Komponent, val ny: Komponent, val utrangering: Long)

fun ersattKomponent(
    gammal: Komponent,
    utbytesAr: Int,
    nyttAnskaffningsvarde: Long,
    nyNyttjandeperiodAr: Int = gammal.nyttjandeperiodAr,
    kalla: String,
): Utbyte {
    if (nyttAnskaffningsvarde < 0) {
        throw IllegalArgumentException()
    }
    if (nyNyttjandeperiodAr <= 0) {
        throw IllegalArgumentException()
    }
    val ny = gammal.copy(
        anskaffningsvarde = nyttAnskaffningsvarde,
        anskaffningsar = utbytesAr,
        nyttjandeperiodAr = nyNyttjandeperiodAr,
        restvarde = 0,
        kalla = kalla,
        sakerhet = Sakerhet.MEDEL,
    )
    val utrangering = utrangeringsbelopp(gammal, utbytesAr)
    return Utbyte(gammal = gammal, ny = ny, utrangering = utrangering)
}
