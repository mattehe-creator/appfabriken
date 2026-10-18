package se.tmconnect.husloggen.regler

enum class PlaneradKostnadFel {
    TITEL_SAKNAS,
    BELOPP_SAKNAS_ELLER_NOLL,
    AR_FORE_INNEVARANDE,
}

fun valideraPlaneradKostnad(
    titel: String,
    beloppOre: Long?,
    planeratAr: Int?,
    innevarandeAr: Int,
): PlaneradKostnadFel? {
    if (titel.isBlank()) return PlaneradKostnadFel.TITEL_SAKNAS
    if (beloppOre == null || beloppOre <= 0L) return PlaneradKostnadFel.BELOPP_SAKNAS_ELLER_NOLL
    if (planeratAr == null || planeratAr < innevarandeAr) return PlaneradKostnadFel.AR_FORE_INNEVARANDE
    return null
}
