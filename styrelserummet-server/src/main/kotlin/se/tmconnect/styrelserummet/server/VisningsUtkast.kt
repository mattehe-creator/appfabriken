package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.regler.NyttjandeperiodRad
import se.tmconnect.styrelserummet.regler.arInomIntervall
import se.tmconnect.styrelserummet.regler.arsintervall
import se.tmconnect.styrelserummet.regler.forslagFor
import se.tmconnect.styrelserummet.regler.forandring
import se.tmconnect.styrelserummet.regler.kronor
import se.tmconnect.styrelserummet.regler.lasNyttjandeperioderFranResurs
import se.tmconnect.styrelserummet.regler.sammanfatta
import se.tmconnect.styrelserummet.underlag.gissaGrupp

@Serializable
data class KomponentVisningDto(
    val namn: String,
    val grupp: String,
    val anskaffningsvarde: Long,
    val anskaffningsvardeText: String,
    val anskaffningsar: Int,
    val nyttjandeperiodAr: Int,
    val nyttjandeperiodForslag: Int? = null,
    val nyttjandeperiodIntervall: String? = null,
    val nyttjandeperiodHjalp: String? = null,
    val nyttjandeperiodVarning: Boolean = false,
    val gruppForslag: String? = null,
    val restvarde: Long = 0,
    val kalla: String,
    val sakerhet: String,
)

@Serializable
data class AvstamningVisningDto(
    val summa: Long,
    val summaText: String,
    val byggnadensVarde: Long,
    val byggnadensVardeText: String,
    val differens: Long,
    val differensText: String,
    val stammer: Boolean,
    val text: String,
)

@Serializable
data class UtskriftsRadDto(
    val grupp: String,
    val namn: String,
    val anskaffningsvarde: String,
    val nyttjandeperiod: String,
    val avskrivningAret: String,
    val bokfortVarde: String,
    val arSumma: Boolean = false,
)

@Serializable
data class ForandringDto(val namn: String, val typ: String)

@Serializable
data class UtkastVisningDto(
    val sammanfattning: List<String>,
    val komponenter: List<KomponentVisningDto>,
    val avstamning: AvstamningVisningDto,
    val utskrift: List<UtskriftsRadDto>,
)

@Serializable
data class JamforelseResponse(val forandringar: List<ForandringDto>)

fun tillVisning(utkast: UtkastDto, tabell: List<NyttjandeperiodRad> = lasNyttjandeperioderFranResurs()): UtkastVisningDto {
    val komponenter = utkast.komponenter.map(::franDto)
    return UtkastVisningDto(
        sammanfattning = sammanfatta(komponenter, utkast.byggnadensVarde.takeIf { it > 0 }, utkast.arsslut),
        komponenter = komponenter.map { komponentVisning(it, tabell) },
        avstamning = avstamningVisning(utkast),
        utskrift = utskriftsRader(komponenter, utkast.arsslut).map(::tillUtskriftsDto),
    )
}

fun jamforelseMotCsv(utkast: UtkastDto, csv: String): JamforelseResponse {
    val nu = utkast.komponenter.map(::franDto)
    val foreg = franExportCsv(csv)
    return JamforelseResponse(
        forandringar = forandring(nu, foreg).map {
            ForandringDto(it.namn, it.typ.name)
        },
    )
}

private fun komponentVisning(k: Komponent, tabell: List<NyttjandeperiodRad>): KomponentVisningDto {
    val rad = tabell.find { it.grupp == k.grupp }
    val forslag = forslagFor(k.grupp, tabell)
    val intervallText = rad?.intervallAr?.let { arsintervall(it.first, it.last) }
    val varning = rad != null && !arInomIntervall(k.nyttjandeperiodAr, rad)
    val hjalp = nyttjandeperiodHjalp(forslag, intervallText)
    val gruppForslag =
        if (k.grupp == Komponentgrupp.OVRIGT) {
            gissaGrupp(k.namn)?.name
        } else {
            null
        }
    return KomponentVisningDto(
        namn = k.namn,
        grupp = k.grupp.name,
        anskaffningsvarde = k.anskaffningsvarde,
        anskaffningsvardeText = kronor(k.anskaffningsvarde),
        anskaffningsar = k.anskaffningsar,
        nyttjandeperiodAr = k.nyttjandeperiodAr,
        nyttjandeperiodForslag = forslag,
        nyttjandeperiodIntervall = intervallText,
        nyttjandeperiodHjalp = hjalp,
        nyttjandeperiodVarning = varning,
        gruppForslag = gruppForslag,
        restvarde = k.restvarde,
        kalla = k.kalla,
        sakerhet = k.sakerhet.name,
    )
}

internal fun nyttjandeperiodHjalp(forslag: Int?, intervallText: String?): String? {
    if (forslag == null) return null
    return if (intervallText != null) {
        "Förslag enligt standardtabell: $forslag år (typintervall $intervallText). UTKAST."
    } else {
        "Förslag enligt standardtabell: $forslag år. UTKAST."
    }
}

private fun avstamningVisning(utkast: UtkastDto): AvstamningVisningDto {
    val a = utkast.avstamning
    val byggText = kronor(a.byggnadensVarde)
    val summaText = kronor(a.summa)
    val diffText = kronor(kotlin.math.abs(a.differens))
    val text =
        if (a.byggnadensVarde <= 0) {
            "Byggnadens värde saknas i underlaget — avstämning görs inte."
        } else if (a.stammer) {
            "Byggnadens värde: $byggText. Summa komponenter: $summaText. Stämmer."
        } else {
            "Byggnadens värde: $byggText. Summa komponenter: $summaText. Differens: $diffText."
        }
    return AvstamningVisningDto(
        summa = a.summa,
        summaText = summaText,
        byggnadensVarde = a.byggnadensVarde,
        byggnadensVardeText = byggText,
        differens = a.differens,
        differensText = diffText,
        stammer = a.stammer,
        text = text,
    )
}

private fun tillUtskriftsDto(r: UtskriftsRad): UtskriftsRadDto =
    UtskriftsRadDto(
        grupp = r.grupp,
        namn = r.namn,
        anskaffningsvarde = r.anskaffningsvarde,
        nyttjandeperiod = r.nyttjandeperiod,
        avskrivningAret = r.avskrivningAret,
        bokfortVarde = r.bokfortVarde,
        arSumma = r.arSumma,
    )
