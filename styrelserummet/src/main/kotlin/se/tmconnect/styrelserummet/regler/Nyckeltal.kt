package se.tmconnect.styrelserummet.regler

data class NyckeltalUnderlag(
    val arsavgifter: Long,
    val rantebarandeSkulder: Long,
    val aretsResultat: Long,
    val avskrivningar: Long,
    val utrangeringar: Long,
    val planeratUnderhall: Long,
    val justeringar: Long,
    val energikostnad: Long,
    val rorelseintakter: Long,
    val kvmBostadsratt: Long,
    val kvmHyresratt: Long,
)

fun avrundaKvot(taljare: Long, namnare: Long): Long {
    if (namnare <= 0) {
        throw IllegalArgumentException("Namnaren måste vara större än noll")
    }

    return if (taljare >= 0) {
        (2 * taljare + namnare) / (2 * namnare)
    } else {
        -avrundaKvot(-taljare, namnare)
    }
}

fun totalYta(u: NyckeltalUnderlag): Long = u.kvmBostadsratt + u.kvmHyresratt

fun arsavgiftPerKvmBostadsratt(u: NyckeltalUnderlag): Long? {
    if (u.kvmBostadsratt <= 0) return null
    return avrundaKvot(u.arsavgifter, u.kvmBostadsratt)
}

fun skuldsattningPerKvm(u: NyckeltalUnderlag): Long? {
    val yta = totalYta(u)
    if (yta <= 0) return null
    return avrundaKvot(u.rantebarandeSkulder, yta)
}

fun skuldsattningPerKvmBostadsratt(u: NyckeltalUnderlag): Long? {
    if (u.kvmBostadsratt <= 0) return null
    return avrundaKvot(u.rantebarandeSkulder, u.kvmBostadsratt)
}
