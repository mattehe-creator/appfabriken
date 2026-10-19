package se.tmconnect.styrelserummet.underlag

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

data class ForslagResultat(
    val komponenter: List<RadForslag>,
    val anmarkningar: List<String>,
)

private val json = Json { ignoreUnknownKeys = true }

fun tolkaForslag(jsonText: String, filnamn: String): ForslagResultat {
    val anmarkningar = mutableListOf<String>()
    val komponenter = mutableListOf<RadForslag>()
    try {
        val root = json.parseToJsonElement(jsonText).jsonObject
        root["anmarkningar"]?.jsonArray?.forEach { el ->
            el.jsonPrimitive.contentOrNull?.let { anmarkningar += it }
        }
        val lista = root["komponenter"]?.jsonArray ?: JsonArray(emptyList())
        lista.forEachIndexed { idx, el ->
            tolkaRad(el, filnamn, idx, anmarkningar)?.let { komponenter += it }
        }
    } catch (_: Exception) {
        anmarkningar += "Kunde inte läsa svaret från tolkningen ($filnamn)."
    }
    return ForslagResultat(komponenter, anmarkningar)
}

private fun tolkaRad(
    el: JsonElement,
    filnamn: String,
    idx: Int,
    anmarkningar: MutableList<String>,
): RadForslag? {
    val obj = runCatching { el.jsonObject }.getOrNull()
    if (obj == null) {
        anmarkningar += "Rad ${idx + 1} i $filnamn kunde inte läsas."
        return null
    }
    val namn = obj.str("namn") ?: run {
        anmarkningar += "Rad ${idx + 1} saknar namn."
        return null
    }
    val gruppNamn = obj.str("grupp")
    val grupp = gruppNamn?.let { Komponentgrupp.franNamn(it) }
    if (grupp == null) {
        anmarkningar += "Okänd grupp för $namn."
        return RadForslag(namn, Komponentgrupp.OVRIGT.name, null, null, null, obj.str("kalla") ?: filnamn, Sakerhet.LAG.name)
    }
    val varde = obj.heltal("anskaffningsvarde")
    val ar = obj.heltal("anskaffningsar")?.toInt()
    val period = obj.heltal("nyttjandeperiodAr")?.toInt()
    val sakerhet = obj.str("sakerhet")?.let {
        runCatching { Sakerhet.valueOf(it) }.getOrNull()?.name
    } ?: Sakerhet.LAG.name
    if (varde == null || ar == null || period == null) {
        anmarkningar += "Ofullständiga värden för $namn, markerad som osäker."
        return RadForslag(
            namn,
            grupp.name,
            varde,
            ar,
            period,
            obj.str("kalla") ?: "$filnamn, sida ?",
            Sakerhet.LAG.name,
        )
    }
    return RadForslag(namn, grupp.name, varde, ar, period, obj.str("kalla") ?: filnamn, sakerhet)
}

private fun JsonObject.str(nyckel: String) = this[nyckel]?.jsonPrimitive?.contentOrNull

private fun JsonObject.heltal(nyckel: String): Long? {
    val el = this[nyckel] ?: return null
    if (el.jsonPrimitive.contentOrNull == "null") return null
    return el.jsonPrimitive.longOrNull ?: el.jsonPrimitive.intOrNull?.toLong()
}
