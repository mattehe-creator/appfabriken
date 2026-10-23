package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.regler.Avgiftstyp
import se.tmconnect.styrelserummet.regler.STAMMA_RADGIVNING_RAD
import se.tmconnect.styrelserummet.regler.StammaRad
import se.tmconnect.styrelserummet.regler.avgiftFelmeddelande
import se.tmconnect.styrelserummet.regler.hogstaOverlatelseavgift
import se.tmconnect.styrelserummet.regler.hogstaPantsattningsavgift
import se.tmconnect.styrelserummet.regler.kronor
import se.tmconnect.styrelserummet.regler.lasStammaDataFranResurs
import se.tmconnect.styrelserummet.regler.prisbasbeloppFor
import se.tmconnect.styrelserummet.regler.tidplan
import se.tmconnect.styrelserummet.regler.tidplanCsv
import se.tmconnect.styrelserummet.regler.tidplanRader
import java.time.LocalDate

/** Gratisverktyget Stämma: tidplan och kontroll av överlåtelse- och pantsättningsavgift. Räknas i minnet. */

private val stammaData: Map<String, StammaRad> by lazy { lasStammaDataFranResurs() }

@Serializable
data class StammaRequest(
    val arsslut: String,
    val stammodatum: String,
    val kallelseVeckorFran: Int,
    val kallelseVeckorTill: Int,
    val overlatelseAvgift: Long? = null,
    val pantsattningsAvgift: Long? = null,
    val prisbasbeloppAr: Int? = null,
)

@Serializable
data class StammaRadDto(val etikett: String, val datum: String, val anmarkning: String, val kalla: String)

@Serializable
data class StammaAvgiftDto(val hogsta: String? = null, val fel: String? = null)

@Serializable
data class StammaSvar(
    val rader: List<StammaRadDto>,
    val radgivning: String,
    val csv: String,
    val overlatelse: StammaAvgiftDto? = null,
    val pantsattning: StammaAvgiftDto? = null,
    val prisbasbeloppInfo: String? = null,
)

fun beraknaStammaSvar(req: StammaRequest): StammaSvar {
    val arsslut = parseDatum(req.arsslut, "Räkenskapsårets slut")
    val stammodatum = parseDatum(req.stammodatum, "Stämmodatum")
    if (req.kallelseVeckorFran !in 2..6 || req.kallelseVeckorTill !in 2..6) {
        throw VerktygFel("Kallelsefristen ska vara mellan 2 och 6 veckor.")
    }
    if (req.kallelseVeckorFran > req.kallelseVeckorTill) {
        throw VerktygFel("Kortaste fristen kan inte vara längre än längsta fristen.")
    }
    val plan = try {
        tidplan(arsslut, stammodatum, req.kallelseVeckorFran..req.kallelseVeckorTill)
    } catch (e: IllegalArgumentException) {
        throw VerktygFel(e.message ?: "Ogiltig tidplan.")
    }
    val tabell = tidplanRader(plan, req.kallelseVeckorFran)
    val rader = tabell.map { StammaRadDto(it.etikett, it.datum, it.anmarkning, it.kalla) }
    val csv = tidplanCsv(tabell)

    val behoverAvgift = req.overlatelseAvgift != null || req.pantsattningsAvgift != null
    var overlatelse: StammaAvgiftDto? = null
    var pantsattning: StammaAvgiftDto? = null
    var prisbasbeloppInfo: String? = null

    if (behoverAvgift) {
        val ar = req.prisbasbeloppAr ?: throw VerktygFel("Ange vilket år prisbasbeloppet gäller.")
        if (ar !in 2000..2100) throw VerktygFel("Året ska vara mellan 2000 och 2100.")
        val pbb = prisbasbeloppFor(ar, stammaData)
        if (pbb == null) {
            prisbasbeloppInfo = "Prisbasbelopp för $ar finns inte i vår datafil. Kontrollera året eller hämta värdet från SCB."
        } else {
            prisbasbeloppInfo = "Prisbasbelopp $ar: ${kronor(pbb)} (enligt datafilen stamma.csv)."
            req.overlatelseAvgift?.let { avgift ->
                kontrolleraAvgift(avgift)?.let { throw VerktygFel(it) }
                overlatelse = kontrollDto(avgift, pbb, Avgiftstyp.OVERLATELSE)
            }
            req.pantsattningsAvgift?.let { avgift ->
                kontrolleraAvgift(avgift)?.let { throw VerktygFel(it) }
                pantsattning = kontrollDto(avgift, pbb, Avgiftstyp.PANTSATTNING)
            }
        }
    }

    return StammaSvar(
        rader = rader,
        radgivning = STAMMA_RADGIVNING_RAD,
        csv = csv,
        overlatelse = overlatelse,
        pantsattning = pantsattning,
        prisbasbeloppInfo = prisbasbeloppInfo,
    )
}

private fun parseDatum(text: String, etikett: String): LocalDate {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) throw VerktygFel("Ange $etikett.")
    return try {
        LocalDate.parse(trimmed)
    } catch (_: Exception) {
        throw VerktygFel("$etikett ska anges som ÅÅÅÅ-MM-DD.")
    }
}

private fun kontrolleraAvgift(avgift: Long): String? {
    if (avgift !in 0..MAX_BELOPP) return "Avgiften ska vara mellan 0 och ${MAX_BELOPP} kr."
    return null
}

private fun kontrollDto(avgift: Long, pbb: Long, typ: Avgiftstyp): StammaAvgiftDto {
    val hogsta = when (typ) {
        Avgiftstyp.OVERLATELSE -> hogstaOverlatelseavgift(pbb, stammaData)
        Avgiftstyp.PANTSATTNING -> hogstaPantsattningsavgift(pbb, stammaData)
        Avgiftstyp.ANDRAHAND -> throw VerktygFel("Internt fel.")
    }
    return StammaAvgiftDto(
        hogsta = kronor(hogsta),
        fel = avgiftFelmeddelande(avgift, pbb, typ, stammaData),
    )
}
