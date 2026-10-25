package se.tmconnect.rostfangst.regler

import java.time.Instant
import java.util.Locale
import org.json.JSONObject

fun tolkaSvar(json: String, originaltext: String): Forslag {
    return try {
        val obj = JSONObject(json.trim())
        val typStr = obj.optString("typ", "").trim().uppercase(Locale.ROOT)
        val typ = parseTyp(typStr) ?: return anteckning(originaltext)
        val titel = obj.optString("titel", "").trim()
        if (titel.isEmpty()) {
            return anteckning(originaltext)
        }
        Forslag(
            typ = typ,
            titel = titel,
            start = obj.optNullableString("start")?.let { Instant.parse(it) },
            slut = obj.optNullableString("slut")?.let { Instant.parse(it) },
            plats = obj.optNullableString("plats"),
            svar = obj.optNullableString("svar"),
        )
    } catch (_: Exception) {
        anteckning(originaltext)
    }
}

private fun anteckning(text: String): Forslag =
    Forslag(typ = ForslagTyp.ANTECKNING, titel = text)

private fun parseTyp(raw: String): ForslagTyp? = when (raw) {
    "KALENDER" -> ForslagTyp.KALENDER
    "PAMINNELSE" -> ForslagTyp.PAMINNELSE
    "ATT_GORA" -> ForslagTyp.ATT_GORA
    "ANTECKNING" -> ForslagTyp.ANTECKNING
    "FRAGA" -> ForslagTyp.FRAGA
    "HEM" -> ForslagTyp.HEM
    else -> null
}

private fun JSONObject.optNullableString(nyckel: String): String? =
    if (!has(nyckel) || isNull(nyckel)) {
        null
    } else {
        optString(nyckel).takeIf { it.isNotBlank() && it != "null" }
    }
