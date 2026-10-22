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

fun justeratResultat(u: NyckeltalUnderlag): Long =
    u.aretsResultat +
        u.avskrivningar +
        u.utrangeringar +
        u.planeratUnderhall +
        u.justeringar

fun sparandePerKvm(u: NyckeltalUnderlag): Long? {
    val yta = totalYta(u)
    if (yta <= 0) return null
    return avrundaKvot(justeratResultat(u), yta)
}

fun rantekanslighetTiondelar(u: NyckeltalUnderlag): Long? {
    if (u.arsavgifter <= 0) return null
    return avrundaKvot(u.rantebarandeSkulder * 10, u.arsavgifter)
}

fun energikostnadPerKvm(u: NyckeltalUnderlag): Long? {
    val yta = totalYta(u)
    if (yta <= 0) return null
    return avrundaKvot(u.energikostnad, yta)
}

fun arsavgiftsandelTiondelsProcent(u: NyckeltalUnderlag): Long? {
    if (u.rorelseintakter <= 0) return null
    return avrundaKvot(u.arsavgifter * 1000, u.rorelseintakter)
}
