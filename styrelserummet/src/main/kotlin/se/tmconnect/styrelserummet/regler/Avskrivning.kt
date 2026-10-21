package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

fun avskrivningFor(k: Komponent, ar: Int): Long {
    // Om nyttjandeperiod är 0 eller mindre, returnera 0
    if (k.nyttjandeperiodAr <= 0) {
        return 0L
    }
    
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
    
    // Heltalsräkning med avrundning uppåt vid exakt halv krona
    val n = k.nyttjandeperiodAr.toLong()
    val arsAvskrivning = (2 * avskrivningsbartBelopp + n) / (2 * n)
    
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
