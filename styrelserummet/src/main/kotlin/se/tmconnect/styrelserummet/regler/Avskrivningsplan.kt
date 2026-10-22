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
