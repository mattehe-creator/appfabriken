package se.tmconnect.styrelserummet.regler

fun fondUtvecklingStigande(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    forstaAvsattning: Long,
    okningTiondelsProcent: Int,
    ingaendeFond: Long,
): List<FondAr> {
    require(forstaAvsattning >= 0) { "forstaAvsattning får inte vara negativ" }
    val uttagPerAr = summaPerAr(atgarder, franAr, antalAr)
    var utgaende = ingaendeFond
    return (0 until antalAr).map { i ->
        val ar = franAr + i
        val avsattning = indexeratBelopp(forstaAvsattning, ar, franAr, okningTiondelsProcent)
        utgaende = utgaende + avsattning - uttagPerAr[i]
        FondAr(ar, avsattning, uttagPerAr[i], utgaende)
    }
}
