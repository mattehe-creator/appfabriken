package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun avskrivningFor(k: Komponent, ar: Int): Long {
    // Om året är före anskaffningsåret eller efter sista avskrivningsåret, returnera 0
    val sistaAvskrivningsar = k.anskaffningsar + k.nyttjandeperiodAr - 1
    if (ar < k.anskaffningsar || ar > sistaAvskrivningsar) {
        return 0L
    }
    
    // Beräkna avskrivningsbart belopp
    val avskrivningsbartBelopp = k.anskaffningsvarde - k.restvarde
    
    // Om det inte finns något att avskriva, returnera 0
    if (avskrivningsbartBelopp <= 0) {
        return 0L
    }
    
    // Normal årsbelopp (avrundat till närmaste hela krona)
    val normaltAr = avskrivningsbartBelopp.toDouble() / k.nyttjandeperiodAr
    val arsAvskrivning = kotlin.math.round(normaltAr).toLong()
    
    // Sista året ska ta resten så att summan blir exakt
    if (ar == sistaAvskrivningsar) {
        var totaltAvskrivet = 0L
        for (i in k.anskaffningsar until ar) {
            totaltAvskrivet += avskrivningFor(k, i)
        }
        return avskrivningsbartBelopp - totaltAvskrivet
    }
    
    return arsAvskrivning
}
