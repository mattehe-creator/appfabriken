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

// Här lägger jag till de saknade funktionerna
fun avskrivningFor(k: Komponent, ar: Int): Long {
    val nyttjandeAr = ar - k.anskaffningsar
    if (nyttjandeAr < 0 || nyttjandeAr >= k.nyttjandeperiodAr) {
        return 0L
    }
    
    // Linjär avskrivning med heltalsavrundning
    val avskrivningsvärde = (k.anskaffningsvarde - k.restvarde).toDouble() / k.nyttjandeperiodAr.toDouble()
    return avskrivningsvärde.toLong()
}

fun bokfortVarde(k: Komponent, ar: Int): Long {
    val nyttjandeAr = ar - k.anskaffningsar
    if (nyttjandeAr < 0) {
        return k.anskaffningsvarde
    }
    
    if (nyttjandeAr >= k.nyttjandeperiodAr) {
        return k.restvarde
    }
    
    // Linjär avskrivning med heltalsavrundning
    val avskrivningsvärde = (k.anskaffningsvarde - k.restvarde).toDouble() / k.nyttjandeperiodAr.toDouble()
    val totalAvskrivning = avskrivningsvärde * nyttjandeAr
    return (k.anskaffningsvarde - totalAvskrivning).toLong()
}
