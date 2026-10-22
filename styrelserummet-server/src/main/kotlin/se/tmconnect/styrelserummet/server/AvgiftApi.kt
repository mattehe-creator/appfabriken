package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.regler.AvgiftUnderlag
import se.tmconnect.styrelserummet.regler.avgiftPerKvmOchManad
import se.tmconnect.styrelserummet.regler.avgiftsforandringTiondelsProcent
import se.tmconnect.styrelserummet.regler.behovdaArsavgifter
import se.tmconnect.styrelserummet.regler.fordelaEfterAndelstal
import se.tmconnect.styrelserummet.regler.kronor

/** Gratisverktyget Avgift: vilka årsavgifter budgeten kräver. Räknas i minnet, inget sparas. */

@Serializable
data class AvgiftRequest(
    val driftkostnader: Long = 0,
    val kapitalkostnader: Long = 0,
    val avsattning: Long = 0,
    val ovrigaIntakter: Long = 0,
    val nuvarandeArsavgifter: Long = 0,
    val kvmBostadsratt: Long = 0,
    /** Andelstal per lägenhet som heltal (sidan skalar bort decimalerna). Tom = ingen fördelning. */
    val andelstal: List<Long> = emptyList(),
)

@Serializable
data class AvgiftSvar(
    val behov: String,
    val behovPerKvmManad: String,
    val nuvarandePerKvmManad: String,
    val forandring: String,
    val anmarkning: String,
    val lagenheter: List<LagenhetAvgift> = emptyList(),
)

@Serializable
data class LagenhetAvgift(val nr: Int, val ar: String, val manad: String)

const val MAX_LAGENHETER = 2_000

/** Fördelar årsavgifterna efter andelstal. Månadsavgiften är årsavgiften delad med 12, avrundad. */
fun fordelaAvgift(arsavgifter: Long, andelstal: List<Long>): List<LagenhetAvgift> {
    if (andelstal.isEmpty()) return emptyList()
    if (andelstal.size > MAX_LAGENHETER) throw VerktygFel("Högst $MAX_LAGENHETER lägenheter.")
    if (andelstal.any { it < 0 }) throw VerktygFel("Andelstalen kan inte vara negativa.")
    val summa = andelstal.fold(java.math.BigInteger.ZERO) { acc, v -> acc + java.math.BigInteger.valueOf(v) }
    if (summa.signum() == 0) throw VerktygFel("Andelstalen summerar till noll.")
    if (summa * java.math.BigInteger.valueOf(maxOf(arsavgifter, 1)) > java.math.BigInteger.valueOf(Long.MAX_VALUE)) {
        throw VerktygFel("Andelstalen har för många siffror. Ange dem med färre decimaler.")
    }
    val fordelat = fordelaEfterAndelstal(arsavgifter, andelstal) ?: throw VerktygFel("Det gick inte att fördela avgiften.")
    return fordelat.mapIndexed { i, ar -> LagenhetAvgift(i + 1, kronor(ar), kronor((ar * 2 + 12) / 24)) }
}

private fun tiondelsProcent(t: Long?): String {
    if (t == null) return "–"
    val tecken = if (t > 0) "+" else if (t < 0) "−" else ""
    val a = kotlin.math.abs(t)
    return "$tecken${a / 10},${a % 10} %"
}

private fun perKvm(v: Long?): String = v?.let { "${kronor(it).removeSuffix(" kr")} kr/kvm/mån" } ?: "–"

fun beraknaAvgiftSvar(req: AvgiftRequest): AvgiftSvar {
    listOf(req.driftkostnader, req.kapitalkostnader, req.avsattning, req.ovrigaIntakter, req.nuvarandeArsavgifter).forEach {
        if (it !in 0..MAX_BELOPP) throw VerktygFel("Beloppen ska vara mellan 0 och ${MAX_BELOPP} kr.")
    }
    if (req.kvmBostadsratt !in 0..MAX_YTA) throw VerktygFel("Ytan ska vara mellan 0 och $MAX_YTA kvm.")
    val u = AvgiftUnderlag(
        driftkostnader = req.driftkostnader,
        kapitalkostnader = req.kapitalkostnader,
        avsattning = req.avsattning,
        ovrigaIntakter = req.ovrigaIntakter,
        nuvarandeArsavgifter = req.nuvarandeArsavgifter,
        kvmBostadsratt = req.kvmBostadsratt,
    )
    val behov = behovdaArsavgifter(u)
    return AvgiftSvar(
        behov = kronor(behov),
        behovPerKvmManad = perKvm(avgiftPerKvmOchManad(behov, req.kvmBostadsratt)),
        nuvarandePerKvmManad = perKvm(avgiftPerKvmOchManad(req.nuvarandeArsavgifter, req.kvmBostadsratt)),
        forandring = tiondelsProcent(avgiftsforandringTiondelsProcent(u)),
        lagenheter = fordelaAvgift(behov, req.andelstal),
        anmarkning = "Utkast. Kapitalkostnader är räntor och amortering. Föreningen beslutar om budget och avgifter.",
    )
}
