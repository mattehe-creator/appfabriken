package se.tmconnect.garantivalvet.export

import java.time.LocalDate
import java.time.format.DateTimeParseException

sealed interface SakerhetskopiaJsonLasResult {
    data class Ok(val innehall: SakerhetskopiaInnehall) : SakerhetskopiaJsonLasResult

    sealed interface Fel : SakerhetskopiaJsonLasResult {
        data object OkandVersion : Fel

        data object TrasigJson : Fel
    }
}

fun skrivSakerhetskopiaJson(innehall: SakerhetskopiaInnehall): String =
    buildString {
        append('{')
        append("\"version\":")
        append(innehall.version)
        append(',')
        append("\"skapad\":")
        append(jsonStrang(innehall.skapad))
        append(',')
        append("\"kop\":[")
        innehall.kop.forEachIndexed { index, kop ->
            if (index > 0) {
                append(',')
            }
            append(skrivKopObjekt(kop))
        }
        append(']')
        append('}')
    }

private fun skrivKopObjekt(kop: SakerhetskopiaKop): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) {
                append(',')
            }
            forsta = false
            append('"')
            append(namn)
            append("\":")
            append(varde)
        }
        laggTillFalt("vad", jsonStrang(kop.vad))
        kop.varKopt?.let { laggTillFalt("varKopt", jsonStrang(it)) }
        laggTillFalt("kopdatum", jsonStrang(kop.kopdatum.toString()))
        laggTillFalt("garantiManader", kop.garantiManader.toString())
        kop.prisOre?.let { laggTillFalt("prisOre", it.toString()) }
        kop.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        kop.kvittoFil?.let { laggTillFalt("kvittoFil", jsonStrang(it)) }
        kop.oppetKopDagar?.let { laggTillFalt("oppetKopDagar", it.toString()) }
        kop.varutyp?.let { laggTillFalt("varutyp", jsonStrang(it)) }
        kop.felUpptackt?.let { laggTillFalt("felUpptackt", jsonStrang(it.toString())) }
        kop.felBeskrivning?.let { laggTillFalt("felBeskrivning", jsonStrang(it)) }
        kop.reklamerad?.let { laggTillFalt("reklamerad", jsonStrang(it.toString())) }
        kop.butikEpost?.let { laggTillFalt("butikEpost", jsonStrang(it)) }
        kop.butikTelefon?.let { laggTillFalt("butikTelefon", jsonStrang(it)) }
        kop.butikOrgnr?.let { laggTillFalt("butikOrgnr", jsonStrang(it)) }
        kop.reklamationUtfall?.let { laggTillFalt("reklamationUtfall", jsonStrang(it)) }
        kop.reklamationUtfallDatum?.let { laggTillFalt("reklamationUtfallDatum", jsonStrang(it.toString())) }
        if (kop.felBilder.isNotEmpty()) {
            laggTillFalt("felBilder", kop.felBilder.joinToString(",", "[", "]") { jsonStrang(it) })
        }
        append('}')
    }

private fun jsonStrang(text: String): String =
    buildString {
        append('"')
        for (c in text) {
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else ->
                    if (c.code < 0x20) {
                        append("\\u")
                        append("%04x".format(c.code))
                    } else {
                        append(c)
                    }
            }
        }
        append('"')
    }

fun lasSakerhetskopiaJson(text: String): SakerhetskopiaJsonLasResult {
    return try {
        val parser = JsonParser(text.trim())
        val rot = parser.lasObjekt() ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        val version = rot["version"] as? Number ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        if (version.toInt() > SAKERHETSKOPIA_VERSION) {
            return SakerhetskopiaJsonLasResult.Fel.OkandVersion
        }
        if (version.toInt() != SAKERHETSKOPIA_VERSION) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val skapad = rot["skapad"] as? String ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        val rawKop = rot["kop"] as? List<*> ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        if (rawKop.size > SAKERHETSKOPIA_MAX_KOP) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val kop =
            rawKop.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasKopObjekt(falt as Map<String, Any?>)
            }
        if (kop.size != rawKop.size) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        SakerhetskopiaJsonLasResult.Ok(
            SakerhetskopiaInnehall(
                version = version.toInt(),
                skapad = skapad,
                kop = kop,
            ),
        )
    } catch (_: Exception) {
        SakerhetskopiaJsonLasResult.Fel.TrasigJson
    }
}

private fun lasDatum(varde: Any?): LocalDate? =
    (varde as? String)?.let {
        try {
            LocalDate.parse(it)
        } catch (_: DateTimeParseException) {
            null
        }
    }

private fun lasKopObjekt(falt: Map<String, Any?>): SakerhetskopiaKop? {
    val vad = falt["vad"] as? String ?: return null
    val kopdatumStr = falt["kopdatum"] as? String ?: return null
    val garanti = falt["garantiManader"] as? Number ?: return null
    val kopdatum =
        try {
            LocalDate.parse(kopdatumStr)
        } catch (_: DateTimeParseException) {
            return null
        }
    val prisOre =
        when (val p = falt["prisOre"]) {
            null -> null
            is Number -> p.toLong()
            else -> return null
        }
    return SakerhetskopiaKop(
        vad = vad,
        varKopt = falt["varKopt"] as? String,
        kopdatum = kopdatum,
        garantiManader = garanti.toInt(),
        prisOre = prisOre,
        anteckning = falt["anteckning"] as? String,
        kvittoFil = falt["kvittoFil"] as? String,
        oppetKopDagar = (falt["oppetKopDagar"] as? Number)?.toInt(),
        varutyp = falt["varutyp"] as? String,
        felUpptackt = lasDatum(falt["felUpptackt"]),
        felBeskrivning = falt["felBeskrivning"] as? String,
        reklamerad = lasDatum(falt["reklamerad"]),
        butikEpost = falt["butikEpost"] as? String,
        butikTelefon = falt["butikTelefon"] as? String,
        butikOrgnr = falt["butikOrgnr"] as? String,
        felBilder = (falt["felBilder"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
        reklamationUtfall = falt["reklamationUtfall"] as? String,
        reklamationUtfallDatum = lasDatum(falt["reklamationUtfallDatum"]),
    )
}

private class JsonParser(private val text: String) {
    private var index = 0

    fun lasObjekt(): Map<String, Any?>? {
        hoppaOverWhitespace()
        if (!match('{')) {
            return null
        }
        hoppaOverWhitespace()
        if (match('}')) {
            return emptyMap()
        }
        val result = linkedMapOf<String, Any?>()
        while (true) {
            hoppaOverWhitespace()
            val nyckel = lasStrang() ?: return null
            hoppaOverWhitespace()
            if (!match(':')) {
                return null
            }
            hoppaOverWhitespace()
            val varde = lasVarde() ?: return null
            result[nyckel] = varde
            hoppaOverWhitespace()
            when {
                match('}') -> return result
                match(',') -> continue
                else -> return null
            }
        }
    }

    private fun lasVarde(): Any? {
        hoppaOverWhitespace()
        return when (peek()) {
            '"' -> lasStrang()
            '{' -> lasObjekt()
            '[' -> lasLista()
            else -> lasNumber()
        }
    }

    private fun lasLista(): List<Any?>? {
        if (!match('[')) {
            return null
        }
        hoppaOverWhitespace()
        if (match(']')) {
            return emptyList()
        }
        val result = mutableListOf<Any?>()
        while (true) {
            hoppaOverWhitespace()
            val varde = lasVarde() ?: return null
            result.add(varde)
            hoppaOverWhitespace()
            when {
                match(']') -> return result
                match(',') -> continue
                else -> return null
            }
        }
    }

    private fun lasStrang(): String? {
        if (!match('"')) {
            return null
        }
        val builder = StringBuilder()
        while (index < text.length) {
            when (val c = text[index++]) {
                '"' -> return builder.toString()
                '\\' -> {
                    if (index >= text.length) {
                        return null
                    }
                    when (val esc = text[index++]) {
                        '"', '\\', '/' -> builder.append(esc)
                        'b' -> builder.append('\b')
                        'f' -> builder.append('\u000C')
                        'n' -> builder.append('\n')
                        'r' -> builder.append('\r')
                        't' -> builder.append('\t')
                        'u' -> {
                            if (index + 4 > text.length) {
                                return null
                            }
                            val hex = text.substring(index, index + 4)
                            index += 4
                            builder.append(hex.toInt(16).toChar())
                        }
                        else -> return null
                    }
                }
                else -> builder.append(c)
            }
        }
        return null
    }

    private fun lasNumber(): Number? {
        val start = index
        if (peek() == '-') {
            index++
        }
        while (index < text.length && text[index].isDigit()) {
            index++
        }
        if (index == start || (index == start + 1 && text[start] == '-')) {
            return null
        }
        return text.substring(start, index).toLongOrNull()
    }

    private fun hoppaOverWhitespace() {
        while (index < text.length && text[index].isWhitespace()) {
            index++
        }
    }

    private fun peek(): Char? = if (index < text.length) text[index] else null

    private fun match(ch: Char): Boolean {
        if (index < text.length && text[index] == ch) {
            index++
            return true
        }
        return false
    }
}
