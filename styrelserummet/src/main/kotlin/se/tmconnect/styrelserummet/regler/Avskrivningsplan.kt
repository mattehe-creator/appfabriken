package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

data class PlanAr(val ar: Int, val avskrivning: Long, val bokfortVarde: Long)

fun planForKomponent(k: Komponent, franAr: Int, antalAr: Int): List<PlanAr> {
    if (antalAr <= 0) {
        return emptyList()
    }
    
    if (antalAr > 100) {
        throw IllegalArgumentException("Antal år får inte vara större än 100")
    }
    
    val plan = mutableListOf<PlanAr>()
    
    for (ar in franAr until franAr + antalAr) {
        val avskrivning = avskrivningFor(k, ar)
        val bokfortVarde = bokfortVarde(k, ar)
        plan.add(PlanAr(ar, avskrivning, bokfortVarde))
    }
    
    return plan
}

fun avskrivningsplan(komponenter: List<Komponent>, franAr: Int, antalAr: Int): List<PlanAr> {
    if (antalAr <= 0) {
        return emptyList()
    }
    
    if (antalAr > 100) {
        throw IllegalArgumentException("Antal år får inte vara större än 100")
    }
    
    val plan = mutableListOf<PlanAr>()
    
    for (ar in franAr until franAr + antalAr) {
        val totalAvskrivning = komponenter.sumOf { avskrivningFor(it, ar) }
        val totalBokfortVarde = komponenter.sumOf { bokfortVarde(it, ar) }
        plan.add(PlanAr(ar, totalAvskrivning, totalBokfortVarde))
    }
    
    return plan
}

// Implementera funktionerna baserat på testens resultat
fun avskrivningFor(k: Komponent, ar: Int): Long {
    val nyttjandeAr = ar - k.anskaffningsar
    if (nyttjandeAr < 0 || nyttjandeAr >= k.nyttjandeperiodAr) {
        return 0L
    }
    
    // Linjär avskrivning med heltalsräkning
    val totalAvskrivningsvärde = k.anskaffningsvarde - k.restvarde
    val avskrivningsvärdePerÅr = totalAvskrivningsvärde / k.nyttjandeperiodAr
    
    // Om det är en restavskrivning, använd den exakta beräkningen
    if (nyttjandeAr == k.nyttjandeperiodAr - 1) {
        // Sista året: avskriv det som återstår
        val totalAvskrivet = avskrivningsvärdePerÅr * nyttjandeAr
        return k.anskaffningsvarde - totalAvskrivet - k.restvarde
    }
    
    return avskrivningsvärdePerÅr
}

fun bokfortVarde(k: Komponent, ar: Int): Long {
    val nyttjandeAr = ar - k.anskaffningsar
    if (nyttjandeAr < 0) {
        return k.anskaffningsvarde
    }
    
    if (nyttjandeAr >= k.nyttjandeperiodAr) {
        return k.restvarde
    }
    
    // Linjär avskrivning med heltalsräkning
    val totalAvskrivningsvärde = k.anskaffningsvarde - k.restvarde
    val avskrivningsvärdePerÅr = totalAvskrivningsvärde / k.nyttjandeperiodAr
    val totalAvskrivning = avskrivningsvärdePerÅr * nyttjandeAr
    
    return k.anskaffningsvarde - totalAvskrivning
}
