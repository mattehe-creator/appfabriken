package se.tmconnect.styrelserummet.regler

data class RantescenarioRad(val hojning: String, val perAr: String, val perKvmManad: String, val avgiftshojning: String)

val STANDARDSTEG = listOf(5, 10, 20, 30)

private fun tiondelar(t: Long): String {
    val tecken = if (t < 0) "−" else ""
    val a = kotlin.math.abs(t)
    return "$tecken${a / 10},${a % 10}"
}

fun rantescenarioRader(
    skulder: Long,
    arsavgifter: Long,
    kvmBostadsratt: Long,
    steg: List<Int> = STANDARDSTEG,
): List<RantescenarioRad> =
    steg.map { h ->
        val extra = extraRantekostnadPerAr(skulder, h)
        val enhet = if (h <= 10) "procentenhet" else "procentenheter"
        RantescenarioRad(
            hojning = "+${tiondelar(h.toLong())} $enhet",
            perAr = kronor(extra),
            perKvmManad = extraPerKvmOchManad(extra, kvmBostadsratt)?.let { "${kronor(it).removeSuffix(" kr")} kr/kvm/mån" } ?: "–",
            avgiftshojning = avgiftshojningTiondelsProcent(skulder, arsavgifter, h)?.let { "+${tiondelar(it)} %" } ?: "–",
        )
    }
