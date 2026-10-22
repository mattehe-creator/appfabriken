package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.regler.rantescenarioRader

/** Gratisverktyget Räntor: vad en högre ränta betyder för föreningen. Räknas i minnet, inget sparas. */

@Serializable
data class RantorRequest(
    val skulder: Long,
    val arsavgifter: Long,
    val kvmBostadsratt: Long = 0,
    val steg: List<Int>? = null,
)

@Serializable
data class RantorRadDto(val hojning: String, val perAr: String, val perKvmManad: String, val avgiftshojning: String)

@Serializable
data class RantorSvar(val rader: List<RantorRadDto>, val anmarkning: String)

fun beraknaRantorSvar(req: RantorRequest): RantorSvar {
    if (req.skulder !in 0..MAX_BELOPP) throw VerktygFel("Skulderna ska vara mellan 0 och ${MAX_BELOPP} kr.")
    if (req.arsavgifter !in 0..MAX_BELOPP) throw VerktygFel("Årsavgifterna ska vara mellan 0 och ${MAX_BELOPP} kr.")
    if (req.kvmBostadsratt !in 0..MAX_YTA) throw VerktygFel("Ytan ska vara mellan 0 och $MAX_YTA kvm.")
    val steg = req.steg ?: listOf(5, 10, 20, 30)
    if (steg.isEmpty() || steg.size > 10 || steg.any { it !in 1..200 }) {
        throw VerktygFel("Räntehöjningen ska vara mellan 0,1 och 20 procentenheter, högst tio steg.")
    }
    val rader = rantescenarioRader(req.skulder, req.arsavgifter, req.kvmBostadsratt, steg)
        .map { RantorRadDto(it.hojning, it.perAr, it.perKvmManad, it.avgiftshojning) }
    return RantorSvar(
        rader = rader,
        anmarkning = "Förenklat: hela skulden antas få den högre räntan direkt, utan hänsyn till bindningstider, amortering eller andra kostnader. Utkast, inte ett råd om avgifter.",
    )
}
