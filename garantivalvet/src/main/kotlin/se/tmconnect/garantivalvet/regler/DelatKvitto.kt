package se.tmconnect.garantivalvet.regler

const val PDF_MAL_BREDD = 1_600
const val PDF_MAX_HOJD = 3_200
const val DELAT_KVITTO_MAX_BYTES = 20 * 1024 * 1024

/** Bara bilder och PDF kan tas emot som delat kvitto. */
fun arDelbartKvitto(mime: String?): Boolean =
    mime != null && (mime.startsWith("image/") || mime == "application/pdf")

fun arPdf(mime: String?): Boolean = mime == "application/pdf"

/**
 * Storlek i pixlar för en renderad PDF-sida: [PDF_MAL_BREDD] bred, men aldrig högre än
 * [PDF_MAX_HOJD] (långa kvitton skalas ned), alltid minst 1 pixel åt varje håll.
 */
fun beraknaPdfStorlek(sidBredd: Int, sidHojd: Int): Pair<Int, Int> {
    if (sidBredd <= 0 || sidHojd <= 0) {
        return PDF_MAL_BREDD to PDF_MAL_BREDD
    }
    var skala = PDF_MAL_BREDD.toDouble() / sidBredd
    if (sidHojd * skala > PDF_MAX_HOJD) {
        skala = PDF_MAX_HOJD.toDouble() / sidHojd
    }
    return (sidBredd * skala).toInt().coerceAtLeast(1) to (sidHojd * skala).toInt().coerceAtLeast(1)
}
