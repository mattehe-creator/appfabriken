package se.tmconnect.rostfangst.ha

import se.tmconnect.rostfangst.regler.Forslag

/** Bygger händelsens JSON för Home Assistant. Ren Kotlin: org.json finns inte i enhetstester. */
fun bygghaHandelseJson(forslag: Forslag, original: String): String {
    val delar = listOf(
        "typ" to jsonText(forslag.typ.name),
        "titel" to jsonText(forslag.titel),
        "start" to (forslag.start?.let { jsonText(it.toString()) } ?: "null"),
        "slut" to (forslag.slut?.let { jsonText(it.toString()) } ?: "null"),
        "plats" to (forslag.plats?.let { jsonText(it) } ?: "null"),
        "original" to jsonText(original),
    )
    return delar.joinToString(prefix = "{", postfix = "}", separator = ",") { (nyckel, varde) ->
        jsonText(nyckel) + ":" + varde
    }
}

private fun jsonText(text: String): String {
    val sb = StringBuilder("\"")
    for (c in text) {
        when (c) {
            '"' -> sb.append("\\\"")
            '\\' -> sb.append("\\\\")
            '\n' -> sb.append("\\n")
            '\r' -> sb.append("\\r")
            '\t' -> sb.append("\\t")
            else -> if (c < ' ') sb.append("\\u%04x".format(c.code)) else sb.append(c)
        }
    }
    return sb.append('"').toString()
}
