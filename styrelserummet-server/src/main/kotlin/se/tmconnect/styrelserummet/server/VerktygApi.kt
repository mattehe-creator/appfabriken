package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.regler.FondAr
import se.tmconnect.styrelserummet.regler.NyckeltalRad
import se.tmconnect.styrelserummet.regler.NyckeltalUnderlag
import se.tmconnect.styrelserummet.regler.PlaneradAtgard
import se.tmconnect.styrelserummet.regler.arsavgiftPerKvmBostadsratt
import se.tmconnect.styrelserummet.regler.arsavgiftsandelTiondelsProcent
import se.tmconnect.styrelserummet.regler.energikostnadPerKvm
import se.tmconnect.styrelserummet.regler.fondUtveckling
import se.tmconnect.styrelserummet.regler.fondUtvecklingStigande
import se.tmconnect.styrelserummet.regler.fondplanCsv
import se.tmconnect.styrelserummet.regler.forandringTiondelsProcent
import se.tmconnect.styrelserummet.regler.formateraForandring
import se.tmconnect.styrelserummet.regler.forstaUnderskottsar
import se.tmconnect.styrelserummet.regler.indexeradeAtgarder
import se.tmconnect.styrelserummet.regler.jamnAvsattning
import se.tmconnect.styrelserummet.regler.kronor
import se.tmconnect.styrelserummet.regler.minstaAvsattningUtanUnderskott
import se.tmconnect.styrelserummet.regler.nyckeltalRader
import se.tmconnect.styrelserummet.regler.rantekanslighetTiondelar
import se.tmconnect.styrelserummet.regler.skuldsattningPerKvm
import se.tmconnect.styrelserummet.regler.skuldsattningPerKvmBostadsratt
import se.tmconnect.styrelserummet.regler.sparandePerKvm

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
data class NyckeltalForegaendeDto(
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
data class NyckeltalRequest(
    val ar: List<NyckeltalArDto>,
    val foregaendeAr: NyckeltalForegaendeDto? = null,
)

@Serializable
data class NyckeltalRadDto(val etikett: String, val varde: String, val forandring: String? = null)

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
    return nyckeltalUnderlagFranFalt(
        d.arsavgifter,
        d.rantebarandeSkulder,
        d.aretsResultat,
        d.avskrivningar,
        d.utrangeringar,
        d.planeratUnderhall,
        d.justeringar,
        d.energikostnad,
        d.rorelseintakter,
        d.kvmBostadsratt,
        d.kvmHyresratt,
    )
}

private fun nyckeltalUnderlagFranFalt(
    arsavgifter: Long,
    rantebarandeSkulder: Long,
    aretsResultat: Long,
    avskrivningar: Long,
    utrangeringar: Long,
    planeratUnderhall: Long,
    justeringar: Long,
    energikostnad: Long,
    rorelseintakter: Long,
    kvmBostadsratt: Long,
    kvmHyresratt: Long,
): NyckeltalUnderlag {
    kontrolleraBelopp("Årsavgifter", arsavgifter, false)
    kontrolleraBelopp("Räntebärande skulder", rantebarandeSkulder, false)
    kontrolleraBelopp("Årets resultat", aretsResultat, true)
    kontrolleraBelopp("Avskrivningar", avskrivningar, false)
    kontrolleraBelopp("Utrangeringar", utrangeringar, false)
    kontrolleraBelopp("Planerat underhåll", planeratUnderhall, false)
    kontrolleraBelopp("Justeringar", justeringar, true)
    kontrolleraBelopp("Energikostnad", energikostnad, false)
    kontrolleraBelopp("Rörelseintäkter", rorelseintakter, false)
    if (kvmBostadsratt !in 0..MAX_YTA || kvmHyresratt !in 0..MAX_YTA) {
        throw VerktygFel("Ytan ska vara mellan 0 och $MAX_YTA kvm.")
    }
    return NyckeltalUnderlag(
        arsavgifter = arsavgifter,
        rantebarandeSkulder = rantebarandeSkulder,
        aretsResultat = aretsResultat,
        avskrivningar = avskrivningar,
        utrangeringar = utrangeringar,
        planeratUnderhall = planeratUnderhall,
        justeringar = justeringar,
        energikostnad = energikostnad,
        rorelseintakter = rorelseintakter,
        kvmBostadsratt = kvmBostadsratt,
        kvmHyresratt = kvmHyresratt,
    )
}

private fun tillForegaendeUnderlag(d: NyckeltalForegaendeDto): NyckeltalUnderlag =
    nyckeltalUnderlagFranFalt(
        d.arsavgifter,
        d.rantebarandeSkulder,
        d.aretsResultat,
        d.avskrivningar,
        d.utrangeringar,
        d.planeratUnderhall,
        d.justeringar,
        d.energikostnad,
        d.rorelseintakter,
        d.kvmBostadsratt,
        d.kvmHyresratt,
    )

private fun nyckeltalTal(u: NyckeltalUnderlag): List<Long?> =
    listOf(
        arsavgiftPerKvmBostadsratt(u),
        skuldsattningPerKvm(u),
        skuldsattningPerKvmBostadsratt(u),
        sparandePerKvm(u),
        rantekanslighetTiondelar(u),
        energikostnadPerKvm(u),
        arsavgiftsandelTiondelsProcent(u),
    )

private fun nyckeltalRadDtos(nu: NyckeltalUnderlag, fore: NyckeltalUnderlag?): List<NyckeltalRadDto> {
    val rader = nyckeltalRader(nu)
    if (fore == null) {
        return rader.map { NyckeltalRadDto(it.etikett, it.varde) }
    }
    val nuTal = nyckeltalTal(nu)
    val foreTal = nyckeltalTal(fore)
    return rader.mapIndexed { i, rad ->
        NyckeltalRadDto(
            rad.etikett,
            rad.varde,
            formateraForandring(forandringTiondelsProcent(nuTal[i], foreTal[i])),
        )
    }
}

fun beraknaNyckeltalSvar(req: NyckeltalRequest): NyckeltalSvar {
    if (req.ar.isEmpty()) throw VerktygFel("Ange minst ett år.")
    if (req.ar.size > MAX_NYCKELTAL_AR) throw VerktygFel("Högst $MAX_NYCKELTAL_AR år (räkenskapsåret och tre år bakåt).")
    if (req.ar.map { it.ar }.toSet().size != req.ar.size) throw VerktygFel("Samma år förekommer två gånger.")
    val fore = req.foregaendeAr?.let { tillForegaendeUnderlag(it) }
    val ar = req.ar.sortedByDescending { it.ar }.mapIndexed { index, d ->
        val jamforelse = if (index == 0) fore else null
        NyckeltalSvarAr(d.ar, nyckeltalRadDtos(tillUnderlag(d), jamforelse))
    }
    return NyckeltalSvar(
        ar = ar,
        anmarkning = "Utkast enligt definitionerna i BFNAR 2023:1. Kontrollera mot bokslutet; föreningen och revisorn ansvarar för förvaltningsberättelsen.",
    )
}

@Serializable
data class AtgardDto(val ar: Int, val belopp: Long, val beskrivning: String = "")

/** Underhållsplanens åtgärder ur ett jobb, för att fylla i Fondplan. */
@Serializable
data class JobbAtgarder(val franAr: Int, val atgarder: List<AtgardDto>)

@Serializable
data class FondplanRequest(
    val franAr: Int,
    val antalAr: Int,
    val ingaendeFond: Long = 0,
    val arligAvsattning: Long? = null,
    val arligPrisupprakningProcent: Double? = null,
    val atgarder: List<AtgardDto> = emptyList(),
    val avsattningOkningProcent: Double? = null,
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

private fun prisupprakningTiondelar(procent: Double?, namn: String = "Prisuppräkningen"): Int {
    if (procent == null) return 0
    if (procent < 0 || procent > 10) {
        throw VerktygFel("$namn ska vara mellan 0 och 10 procent.")
    }
    return kotlin.math.round(procent * 10).toInt()
}

fun beraknaFondplanSvar(req: FondplanRequest): FondplanSvar {
    if (req.franAr !in 1990..2100) throw VerktygFel("Startåret är inte giltigt.")
    if (req.antalAr !in 1..MAX_FONDPLAN_AR) throw VerktygFel("Perioden ska vara 1–$MAX_FONDPLAN_AR år.")
    if (req.atgarder.size > MAX_ATGARDER) throw VerktygFel("Högst $MAX_ATGARDER åtgärder.")
    kontrolleraBelopp("Fonden", req.ingaendeFond, true)
    req.arligAvsattning?.let { kontrolleraBelopp("Avsättningen", it, false) }
    val planerade = req.atgarder.map { a ->
        kontrolleraBelopp("Beloppet för ${a.ar}", a.belopp, false)
        PlaneradAtgard(a.ar, a.belopp, a.beskrivning.take(200))
    }
    val prisTiondelar = prisupprakningTiondelar(req.arligPrisupprakningProcent)
    val atgarder =
        if (prisTiondelar == 0) {
            planerade
        } else {
            indexeradeAtgarder(planerade, req.franAr, prisTiondelar)
        }
    val jamn = jamnAvsattning(atgarder, req.franAr, req.antalAr, req.ingaendeFond)
    val minsta = minstaAvsattningUtanUnderskott(atgarder, req.franAr, req.antalAr, req.ingaendeFond)
    val vald = req.arligAvsattning ?: minsta
    val okningTiondelar =
        if (req.arligAvsattning == null) 0 else prisupprakningTiondelar(req.avsattningOkningProcent, "Ökningen av avsättningen")
    val utv: List<FondAr> =
        if (okningTiondelar == 0) {
            fondUtveckling(atgarder, req.franAr, req.antalAr, vald, req.ingaendeFond)
        } else {
            fondUtvecklingStigande(atgarder, req.franAr, req.antalAr, vald, okningTiondelar, req.ingaendeFond)
        }
    return FondplanSvar(
        jamnAvsattning = kronor(jamn),
        minstaAvsattning = kronor(minsta),
        valdAvsattning = kronor(vald),
        forstaUnderskottsar = forstaUnderskottsar(utv),
        utveckling = utv.map { FondArDto(it.ar, kronor(it.avsattning), kronor(it.uttag), kronor(it.utgaende), it.utgaende < 0) },
        csv = fondplanCsv(utv),
        anmarkning =
            if (prisTiondelar == 0) {
                "Utkast. Avsättningen räknas in före årets uttag, utan ränta och utan prisökning. Föreningen beslutar om avsättning och avgifter."
            } else {
                val procentText =
                    if (prisTiondelar % 10 == 0) {
                        "${prisTiondelar / 10} %"
                    } else {
                        "${prisTiondelar / 10},${prisTiondelar % 10} %"
                    }
                "Utkast. Framtida åtgärder har räknats upp med $procentText per år från ${req.franAr}, utan ränta på fonden. Föreningen beslutar om avsättning och avgifter."
            },
    )
}
