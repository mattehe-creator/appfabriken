package se.tmconnect.styrelserummet.regler

data class PlaneradAtgard(val ar: Int, val belopp: Long, val beskrivning: String)

fun summaPerAr(atgarder: List<PlaneradAtgard>, franAr: Int, antalAr: Int): List<Long> {
    if (antalAr < 1 || antalAr > 100) {
        throw IllegalArgumentException("antalAr måste vara mellan 1 och 100")
    }
    
    val resultat = MutableList(antalAr) { 0L }
    
    for (atgard in atgarder) {
        if (atgard.belopp < 0) {
            throw IllegalArgumentException("Belopp får inte vara negativt")
        }
        
        val arsIndex = atgard.ar - franAr
        if (arsIndex >= 0 && arsIndex < antalAr) {
            resultat[arsIndex] += atgard.belopp
        }
    }
    
    return resultat
}

fun jamnAvsattning(atgarder: List<PlaneradAtgard>, franAr: Int, antalAr: Int, ingaendeFond: Long): Long {
    val behov = summaPerAr(atgarder, franAr, antalAr).sum() - ingaendeFond
    if (behov <= 0) {
        return 0L
    }
    
    // Avrunda uppåt till hel krona: (behov + antalAr - 1) / antalAr
    return (behov + antalAr - 1) / antalAr
}

data class FondAr(val ar: Int, val avsattning: Long, val uttag: Long, val utgaende: Long)

fun fondUtveckling(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    arligAvsattning: Long,
    ingaendeFond: Long,
): List<FondAr> {
    if (arligAvsattning < 0) {
        throw IllegalArgumentException("arligAvsattning får inte vara negativ")
    }
    val uttagPerAr = summaPerAr(atgarder, franAr, antalAr)
    val resultat = mutableListOf<FondAr>()
    var utgaende = ingaendeFond
    for (i in 0 until antalAr) {
        val ar = franAr + i
        val uttag = uttagPerAr[i]
        utgaende = utgaende + arligAvsattning - uttag
        resultat.add(FondAr(ar, arligAvsattning, uttag, utgaende))
    }
    return resultat
}
