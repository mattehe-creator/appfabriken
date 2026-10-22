package se.tmconnect.styrelserummet.regler

data class AvgiftUnderlag(
    val driftkostnader: Long,
    val kapitalkostnader: Long,
    val avsattning: Long,
    val ovrigaIntakter: Long,
    val nuvarandeArsavgifter: Long,
    val kvmBostadsratt: Long,
)

fun behovdaArsavgifter(u: AvgiftUnderlag): Long {
    if (u.driftkostnader < 0 ||
        u.kapitalkostnader < 0 ||
        u.avsattning < 0 ||
        u.ovrigaIntakter < 0 ||
        u.nuvarandeArsavgifter < 0
    ) {
        throw IllegalArgumentException()
    }
    val behov = u.driftkostnader + u.kapitalkostnader + u.avsattning - u.ovrigaIntakter
    return maxOf(0L, behov)
}

fun avgiftPerKvmOchManad(arsavgifter: Long, kvmBostadsratt: Long): Long? {
    if (kvmBostadsratt <= 0) return null
    return avrundaKvot(arsavgifter, kvmBostadsratt * 12)
}

fun avgiftsforandringTiondelsProcent(u: AvgiftUnderlag): Long? {
    if (u.nuvarandeArsavgifter <= 0) return null
    val behov = behovdaArsavgifter(u)
    return avrundaKvot((behov - u.nuvarandeArsavgifter) * 1000, u.nuvarandeArsavgifter)
}
