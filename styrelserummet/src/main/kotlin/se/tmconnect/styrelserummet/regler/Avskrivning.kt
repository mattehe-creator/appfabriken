package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun avskrivningFor(k: Komponent, ar: Int): Long {
    if (ar < k.anskaffningsar) return 0
    val avskrivningsbart = k.anskaffningsvarde - k.restvarde
    if (avskrivningsbart <= 0 || k.nyttjandeperiodAr < 1) return 0
    val perAr = avskrivningsbart / k.nyttjandeperiodAr
    val sistaAr = k.anskaffningsar + k.nyttjandeperiodAr - 1
    if (ar > sistaAr) return 0
    val arIndex = ar - k.anskaffningsar
    val ackumuleratUtanDettaAr = perAr * arIndex
    val kvar = avskrivningsbart - ackumuleratUtanDettaAr
    return if (ar == sistaAr) kvar else perAr.coerceAtMost(kvar)
}

fun bokfortVarde(k: Komponent, arsslut: Int): Long {
    var ack = 0L
    for (ar in k.anskaffningsar..arsslut) {
        ack += avskrivningFor(k, ar)
    }
    val tak = k.anskaffningsvarde - k.restvarde
    val avskrivet = ack.coerceAtMost(tak)
    return (k.anskaffningsvarde - avskrivet).coerceAtLeast(k.restvarde)
}
