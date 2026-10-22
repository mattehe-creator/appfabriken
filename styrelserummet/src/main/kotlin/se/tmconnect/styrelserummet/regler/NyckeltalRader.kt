package se.tmconnect.styrelserummet.regler

import kotlin.math.abs

data class NyckeltalRad(val etikett: String, val varde: String)

private const val INGEN = "\u2013"

private fun krPerKvm(belopp: Long?): String {
    if (belopp == null) return INGEN
    return kronor(belopp).replace(" kr", " kr/kvm")
}

private fun tiondelar(t: Long?): String {
    if (t == null) return INGEN
    val tecken = if (t < 0) "\u2212" else ""
    val a = abs(t)
    return "$tecken${a / 10},${a % 10}"
}

private fun tiondelsProcent(p: Long?): String {
    if (p == null) return INGEN
    val tecken = if (p < 0) "\u2212" else ""
    val a = abs(p)
    return "$tecken${a / 10},${a % 10} %"
}

fun nyckeltalRader(u: NyckeltalUnderlag): List<NyckeltalRad> =
    listOf(
        NyckeltalRad(
            "Årsavgift per kvm upplåten med bostadsrätt",
            krPerKvm(arsavgiftPerKvmBostadsratt(u)),
        ),
        NyckeltalRad(
            "Skuldsättning per kvm",
            krPerKvm(skuldsattningPerKvm(u)),
        ),
        NyckeltalRad(
            "Skuldsättning per kvm upplåten med bostadsrätt",
            krPerKvm(skuldsattningPerKvmBostadsratt(u)),
        ),
        NyckeltalRad(
            "Sparande per kvm",
            krPerKvm(sparandePerKvm(u)),
        ),
        NyckeltalRad(
            "Räntekänslighet",
            tiondelar(rantekanslighetTiondelar(u)),
        ),
        NyckeltalRad(
            "Energikostnad per kvm",
            krPerKvm(energikostnadPerKvm(u)),
        ),
        NyckeltalRad(
            "Årsavgifternas andel av totala rörelseintäkter",
            tiondelsProcent(arsavgiftsandelTiondelsProcent(u)),
        ),
    )
