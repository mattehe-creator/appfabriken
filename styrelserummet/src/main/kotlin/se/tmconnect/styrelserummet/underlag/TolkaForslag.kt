package se.tmconnect.styrelserummet.underlag

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

data class ForslagKomponent(
    val namn: String,
    val grupp: Komponentgrupp?,
    val anskaffningsvarde: Long?,
    val anskaffningsar: Int?,
    val nyttjandeperiodAr: Int?,
    val kalla: String,
    val sakerhet: Sakerhet,
)

/**
 * Högsta längd på namn och källa för en komponent. Samma gräns gäller när användaren sparar rader
 * (`raderInomGranser` i servern), så ett tolkat utkast går alltid att spara oförändrat.
 */
const val MAX_TEXT_KOMPONENT = 300

/** Kapar text till [MAX_TEXT_KOMPONENT] tecken utan att dela ett surrogatpar. */
fun kapaText(text: String): String {
    if (text.length <= MAX_TEXT_KOMPONENT) return text
    val slut = if (text[MAX_TEXT_KOMPONENT - 1].isHighSurrogate()) MAX_TEXT_KOMPONENT - 1 else MAX_TEXT_KOMPONENT
    return text.substring(0, slut)
}

/** En åtgärd i underhållsplanen: år, kostnad i hela kronor och kort beskrivning. */
data class ForslagAtgard(val ar: Int, val belopp: Long, val beskrivning: String)

data class ForslagResultat(
    val komponenter: List<ForslagKomponent>,
    val anmarkningar: List<String>,
    val atgarder: List<ForslagAtgard> = emptyList(),
)

const val MAX_ATGARDER_PER_SVAR = 300

/** Läser fältet "atgarder" om det finns. Rader som inte går att lita på hoppas över tyst. */
fun tolkaAtgarder(root: JsonObject): List<ForslagAtgard> {
    val lista = root["atgarder"] as? kotlinx.serialization.json.JsonArray ?: return emptyList()
    return lista.mapNotNull { element ->
        val obj = element as? JsonObject ?: return@mapNotNull null
        val ar = (obj["ar"] as? JsonPrimitive)?.contentOrNull?.trim()?.toIntOrNull() ?: return@mapNotNull null
        val belopp = (obj["belopp"] as? JsonPrimitive)?.contentOrNull?.replace(" ", "")?.toLongOrNull()
            ?: return@mapNotNull null
        if (ar !in 1990..2100 || belopp <= 0 || belopp > 1_000_000_000_000L) return@mapNotNull null
        val text = (obj["beskrivning"] as? JsonPrimitive)?.contentOrNull?.trim().orEmpty().take(200)
        ForslagAtgard(ar, belopp, text)
    }.take(MAX_ATGARDER_PER_SVAR)
}

private val json = Json {
    ignoreUnknownKeys = true
    allowTrailingComma = true
}

fun tolkaForslag(jsonText: String, filnamn: String): ForslagResultat {
    val anmarkningar = mutableListOf<String>()
    val komponenter = mutableListOf<ForslagKomponent>()
    val prepared = forberedJsonText(jsonText)
    val root = try {
        json.parseToJsonElement(prepared).jsonObject
    } catch (_: Exception) {
        return ForslagResultat(
            emptyList(),
            listOf("Ogiltig JSON från tolkningen för $filnamn."),
        )
    }
    root["anmarkningar"]?.jsonArray?.forEach { element ->
        element.jsonPrimitive.contentOrNull?.takeIf { it.isNotBlank() }?.let(anmarkningar::add)
    }
    val lista = root["komponenter"]?.jsonArray
    if (lista == null) {
        anmarkningar += "Saknar fältet komponenter i svaret för $filnamn."
        return ForslagResultat(komponenter, anmarkningar, tolkaAtgarder(root))
    }
    lista.forEachIndexed { index, element ->
        when (val parsed = parseKomponent(element, filnamn, index + 1, anmarkningar)) {
            is ParseResult.Ok -> komponenter += parsed.komponent
            is ParseResult.Skip -> anmarkningar += parsed.orsak
        }
    }
    return ForslagResultat(komponenter, anmarkningar, tolkaAtgarder(root))
}

private fun forberedJsonText(raw: String): String {
    var text = raw.trim()
    val fence = Regex("```json\\s*", RegexOption.IGNORE_CASE).find(text)
    if (fence != null) {
        val innerStart = fence.range.last + 1
        val rest = text.substring(innerStart)
        val fenceEnd = rest.indexOf("```")
        if (fenceEnd >= 0) {
            text = rest.substring(0, fenceEnd).trim()
        }
    }
    return extraheraJsonObjekt(text)
}

private fun extraheraJsonObjekt(text: String): String {
    val start = text.indexOf('{')
    if (start < 0) return text.trim()
    var depth = 0
    var inString = false
    var escape = false
    for (i in start until text.length) {
        when (val c = text[i]) {
            '\\' -> if (inString) escape = true
            '"' -> if (!escape) inString = !inString
            '{' -> if (!inString) depth++
            '}' -> if (!inString) {
                depth--
                if (depth == 0) return text.substring(start, i + 1)
            }
        }
        if (escape) escape = false
    }
    return text.substring(start).trim()
}

private sealed class ParseResult {
    data class Ok(val komponent: ForslagKomponent) : ParseResult()
    data class Skip(val orsak: String) : ParseResult()
}

private fun parseKomponent(
    element: JsonElement,
    filnamn: String,
    rad: Int,
    anmarkningar: MutableList<String>,
): ParseResult {
    val obj = try {
        element.jsonObject
    } catch (_: Exception) {
        return ParseResult.Skip("Rad $rad i $filnamn: komponenten är inte ett JSON-objekt.")
    }
    val tolkatNamn = obj.string("namn")
    if (tolkatNamn.isNullOrBlank()) {
        return ParseResult.Skip("Rad $rad i $filnamn: saknar namn.")
    }
    val namn = kapaText(tolkatNamn)
    var lag = false
    val grupp = obj.string("grupp")?.let { text ->
        runCatching { Komponentgrupp.valueOf(text.trim()) }.getOrElse {
            anmarkningar += "Rad $rad ($namn): okänd grupp \"$text\"."
            lag = true
            null
        }
    }
    val anskaffningsvarde = obj.heltalsVarde("anskaffningsvarde", "anskaffningsvarde", rad, namn, anmarkningar) { lag = true }
    val anskaffningsar = obj.heltalsVarde("anskaffningsar", "anskaffningsår", rad, namn, anmarkningar) { lag = true }?.toInt()
    val nyttjandeperiod = obj.heltalsVarde("nyttjandeperiodAr", "nyttjandeperiod", rad, namn, anmarkningar) { lag = true }?.toInt()
    val kalla = kapaText(obj.string("kalla") ?: "$filnamn, sida okänd")
    var sakerhet = obj.string("sakerhet")?.let { text ->
        runCatching { Sakerhet.valueOf(text.trim()) }.getOrElse {
            anmarkningar += "Rad $rad ($namn): okänd säkerhet."
            lag = true
            Sakerhet.LAG
        }
    } ?: run {
        lag = true
        Sakerhet.LAG
    }
    if (lag) sakerhet = Sakerhet.LAG
    if (grupp == null) {
        return ParseResult.Skip("Rad $rad ($namn): kunde inte tolka gruppen.")
    }
    return ParseResult.Ok(
        ForslagKomponent(
            namn = namn,
            grupp = grupp,
            anskaffningsvarde = anskaffningsvarde,
            anskaffningsar = anskaffningsar,
            nyttjandeperiodAr = nyttjandeperiod,
            kalla = kalla,
            sakerhet = sakerhet,
        ),
    )
}

private fun JsonObject.heltalsVarde(
    nyckel: String,
    etikett: String,
    rad: Int,
    namn: String,
    anmarkningar: MutableList<String>,
    markeraLag: () -> Unit,
): Long? {
    if (!containsKey(nyckel)) return null
    val element = get(nyckel) ?: return null
    if (element is JsonNull) return null
    if (element is JsonPrimitive && element.contentOrNull == "null") return null
    val varde = when (element) {
        is JsonPrimitive -> element.contentOrNull?.replace(" ", "")?.toLongOrNull()
        else -> null
    }
    if (varde == null) {
        anmarkningar += "Rad $rad ($namn): $etikett är inte ett heltal."
        markeraLag()
        return null
    }
    if (varde < 0L && nyckel == "anskaffningsvarde") {
        anmarkningar += "Rad $rad ($namn): anskaffningsvarde är negativt."
        markeraLag()
        return null
    }
    return varde
}

private fun JsonObject.string(nyckel: String): String? =
    this[nyckel]?.jsonPrimitive?.contentOrNull
