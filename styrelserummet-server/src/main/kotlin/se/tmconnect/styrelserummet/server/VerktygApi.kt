package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.regler.FondAr
import se.tmconnect.styrelserummet.regler.NyckeltalRad
import se.tmconnect.styrelserummet.regler.NyckeltalUnderlag
import se.tmconnect.styrelserummet.regler.PlaneradAtgard
import se.tmconnect.styrelserummet.regler.fondUtveckling
import se.tmconnect.styrelserummet.regler.fondplanCsv
import se.tmconnect.styrelserummet.regler.forstaUnderskottsar
import se.tmconnect.styrelserummet.regler.jamnAvsattning
import se.tmconnect.styrelserummet.regler.kronor
import se.tmconnect.styrelserummet.regler.minstaAvsattningUtanUnderskott
import se.tmconnect.styrelserummet.regler.nyckeltalRader

/**
 * Två fria verktyg utan uppladdning och utan lagring: nyckeltal enligt BFNAR 2023:1 och fondplan.
 * Ingenting sparas och ingen AI används; svaret räknas fram av kod direkt.
 */

const val MAX_BELOPP = 1_000_000_000_000L
const val MAX_YTA = 10_000_000L
const val MAX_NYCKELTAL_AR = 4
const val MAX_ATGARDER = 500
const val MAX_FONDPLAN_AR = 60

class VerktygFel(meddelande: String) : Exception(meddelande)

@Serializable
data class NyckeltalArDto(
    val ar: Int,
    val arsavgifter: Long = 0,
    val rantebarandeSkulder: Long = 0,
    val aretsResultat: Long = 0,
    val avskrivningar: Long = 0,
    val utrangeringar: Long = 0,
    val planeratUnderhall: Long = 0,
    val justeringar: Long = 0,
    val energikostnad: Long = 0,
    val rorelseintakter: Long = 0,
    val kvmBostadsratt: Long = 0,
    val kvmHyresratt: Long = 0,
)

@Serializable
data class NyckeltalRequest(val ar: List<NyckeltalArDto>)

@Serializable
data class NyckeltalRadDto(val etikett: String, val varde: String)

@Serializable
data class NyckeltalSvarAr(val ar: Int, val rader: List<NyckeltalRadDto>)

@Serializable
data class NyckeltalSvar(val ar: List<NyckeltalSvarAr>, val anmarkning: String)

private fun kontrolleraBelopp(namn: String, v: Long, tillatNegativt: Boolean) {
    if (v > MAX_BELOPP || v < -MAX_BELOPP) throw VerktygFel("$namn är orimligt stort.")
    if (!tillatNegativt && v < 0) throw VerktygFel("$namn kan inte vara negativt.")
}

fun tillUnderlag(d: NyckeltalArDto): NyckeltalUnderlag {
    if (d.ar !in 1990..2100) throw VerktygFel("Året ${d.ar} är inte giltigt.")
    kontrolleraBelopp("Årsavgifter", d.arsavgifter, false)
    kontrolleraBelopp("Räntebärande skulder", d.rantebarandeSkulder, false)
    kontrolleraBelopp("Årets resultat", d.aretsResultat, true)
    kontrolleraBelopp("Avskrivningar", d.avskrivningar, false)
    kontrolleraBelopp("Utrangeringar", d.utrangeringar, false)
    kontrolleraBelopp("Planerat underhåll", d.planeratUnderhall, false)
    kontrolleraBelopp("Justeringar", d.justeringar, true)
    kontrolleraBelopp("Energikostnad", d.energikostnad, false)
    kontrolleraBelopp("Rörelseintäkter", d.rorelseintakter, false)
    if (d.kvmBostadsratt !in 0..MAX_YTA || d.kvmHyresratt !in 0..MAX_YTA) {
        throw VerktygFel("Ytan ska vara mellan 0 och $MAX_YTA kvm.")
    }
    return NyckeltalUnderlag(
        arsavgifter = d.arsavgifter,
        rantebarandeSkulder = d.rantebarandeSkulder,
        aretsResultat = d.aretsResultat,
        avskrivningar = d.avskrivningar,
        utrangeringar = d.utrangeringar,
        planeratUnderhall = d.planeratUnderhall,
        justeringar = d.justeringar,
        energikostnad = d.energikostnad,
        rorelseintakter = d.rorelseintakter,
        kvmBostadsratt = d.kvmBostadsratt,
        kvmHyresratt = d.kvmHyresratt,
    )
}

fun beraknaNyckeltalSvar(req: NyckeltalRequest): NyckeltalSvar {
    if (req.ar.isEmpty()) throw VerktygFel("Ange minst ett år.")
    if (req.ar.size > MAX_NYCKELTAL_AR) throw VerktygFel("Högst $MAX_NYCKELTAL_AR år (räkenskapsåret och tre år bakåt).")
    if (req.ar.map { it.ar }.toSet().size != req.ar.size) throw VerktygFel("Samma år förekommer två gånger.")
    val ar = req.ar.sortedByDescending { it.ar }.map { d ->
        NyckeltalSvarAr(d.ar, nyckeltalRader(tillUnderlag(d)).map { r: NyckeltalRad -> NyckeltalRadDto(r.etikett, r.varde) })
    }
    return NyckeltalSvar(
        ar = ar,
        anmarkning = "Utkast enligt definitionerna i BFNAR 2023:1. Kontrollera mot bokslutet; föreningen och revisorn ansvarar för förvaltningsberättelsen.",
    )
}

@Serializable
data class AtgardDto(val ar: Int, val belopp: Long, val beskrivning: String = "")

@Serializable
data class FondplanRequest(
    val franAr: Int,
    val antalAr: Int,
    val ingaendeFond: Long = 0,
    val arligAvsattning: Long? = null,
    val atgarder: List<AtgardDto> = emptyList(),
)

@Serializable
data class FondArDto(val ar: Int, val avsattning: String, val uttag: String, val utgaende: String, val underskott: Boolean)

@Serializable
data class FondplanSvar(
    val jamnAvsattning: String,
    val minstaAvsattning: String,
    val valdAvsattning: String,
    val forstaUnderskottsar: Int?,
    val utveckling: List<FondArDto>,
    val csv: String,
    val anmarkning: String,
)

fun beraknaFondplanSvar(req: FondplanRequest): FondplanSvar {
    if (req.franAr !in 1990..2100) throw VerktygFel("Startåret är inte giltigt.")
    if (req.antalAr !in 1..MAX_FONDPLAN_AR) throw VerktygFel("Perioden ska vara 1–$MAX_FONDPLAN_AR år.")
    if (req.atgarder.size > MAX_ATGARDER) throw VerktygFel("Högst $MAX_ATGARDER åtgärder.")
    kontrolleraBelopp("Fonden", req.ingaendeFond, true)
    req.arligAvsattning?.let { kontrolleraBelopp("Avsättningen", it, false) }
    val atgarder = req.atgarder.map { a ->
        kontrolleraBelopp("Beloppet för ${a.ar}", a.belopp, false)
        PlaneradAtgard(a.ar, a.belopp, a.beskrivning.take(200))
    }
    val jamn = jamnAvsattning(atgarder, req.franAr, req.antalAr, req.ingaendeFond)
    val minsta = minstaAvsattningUtanUnderskott(atgarder, req.franAr, req.antalAr, req.ingaendeFond)
    val vald = req.arligAvsattning ?: minsta
    val utv: List<FondAr> = fondUtveckling(atgarder, req.franAr, req.antalAr, vald, req.ingaendeFond)
    return FondplanSvar(
        jamnAvsattning = kronor(jamn),
        minstaAvsattning = kronor(minsta),
        valdAvsattning = kronor(vald),
        forstaUnderskottsar = forstaUnderskottsar(utv),
        utveckling = utv.map { FondArDto(it.ar, kronor(it.avsattning), kronor(it.uttag), kronor(it.utgaende), it.utgaende < 0) },
        csv = fondplanCsv(utv),
        anmarkning = "Utkast. Avsättningen räknas in före årets uttag, utan ränta och utan prisökning. Föreningen beslutar om avsättning och avgifter.",
    )
}
