package se.tmconnect.husloggen.regler

data class ArsJamforelse(val ar: Int, val forbrukning: Long, val forandringTiondelsProcent: Long?)

private fun avrunda(taljare: Long, namnare: Long): Long =
    if (taljare >= 0) (2 * taljare + namnare) / (2 * namnare) else -avrunda(-taljare, namnare)

fun jamforArsforbrukning(perAr: Map<Int, Long>): List<ArsJamforelse> =
    perAr.keys.sortedDescending().map { ar ->
        val nu = perAr.getValue(ar)
        val foreg = perAr[ar - 1]
        ArsJamforelse(
            ar = ar,
            forbrukning = nu,
            forandringTiondelsProcent = if (foreg != null && foreg > 0) avrunda((nu - foreg) * 1000, foreg) else null,
        )
    }
