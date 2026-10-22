package se.tmconnect.styrelserummet.regler

fun fondUtvecklingStigande(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    forstaAvsattning: Long,
    okningTiondelsProcent: Int,
    ingaendeFond: Long,
): List<FondAr> {
    if (forstaAvsattning < 0) {
        throw IllegalArgumentException("forstaAvsattning får inte vara negativ")
    }
    val uttagPerAr = summaPerAr(atgarder, franAr, antalAr)
    val resultat = mutableListOf<FondAr>()
    var utgaende = ingaendeFond
    for (i in 0 until antalAr) {
        val ar = franAr + i
        val avsattning = indexeratBelopp(forstaAvsattning, ar, franAr, okningTiondelsProcent)
        val uttag = uttagPerAr[i]
        utgaende = utgaende + avsattning - uttag
        resultat.add(FondAr(ar, avsattning, uttag, utgaende))
    }
    return resultat
}
