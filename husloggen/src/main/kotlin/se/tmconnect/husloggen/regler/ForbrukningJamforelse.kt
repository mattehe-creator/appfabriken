package se.tmconnect.husloggen.regler

data class ArsJamforelse(val ar: Int, val forbrukning: Long, val forandringTiondelsProcent: Long?)

private fun avrundaKvot(taljare: Long, namnare: Long): Long {
    if (namnare <= 0) {
        throw IllegalArgumentException("Namnaren måste vara större än noll")
    }
    return if (taljare >= 0) {
        (2 * taljare + namnare) / (2 * namnare)
    } else {
        -avrundaKvot(-taljare, namnare)
    }
}

private fun forandringMotForegaendeAr(nu: Long, foregaende: Long?): Long? {
    if (foregaende == null || foregaende <= 0) {
        return null
    }
    return avrundaKvot((nu - foregaende) * 1000, foregaende)
}

fun jamforArsforbrukning(perAr: Map<Int, Long>): List<ArsJamforelse> =
    perAr.entries
        .sortedByDescending { it.key }
        .map { (ar, forbrukning) ->
            ArsJamforelse(
                ar = ar,
                forbrukning = forbrukning,
                forandringTiondelsProcent = forandringMotForegaendeAr(forbrukning, perAr[ar - 1]),
            )
        }
