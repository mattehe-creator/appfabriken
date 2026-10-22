package se.tmconnect.flextid.export

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import se.tmconnect.flextid.regler.normaliseraStamplaInPaminnelseMinEfterMidnatt

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
        append("\"installning\":")
        append(skrivInstallningObjekt(innehall.installning))
        append(',')
        append("\"dagar\":[")
        innehall.dagar.forEachIndexed { index, dag ->
            if (index > 0) append(',')
            append(skrivDagObjekt(dag))
        }
        append("]}")
    }

fun lasSakerhetskopiaJson(json: String): SakerhetskopiaJsonLasResult {
    return try {
        val rot = JsonObjekt.las(json) ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        val version = rot.heltal("version") ?: 0
        if (version > FLEXTID_SAKERHETSKOPIA_VERSION) {
            return SakerhetskopiaJsonLasResult.Fel.OkandVersion
        }
        if (version < FLEXTID_SAKERHETSKOPIA_MIN_VERSION) {
            return SakerhetskopiaJsonLasResult.Fel.OkandVersion
        }
        val skapad = rot.strang("skapad") ?: ""
        val installningObj = rot.objekt("installning")
        val installning =
            if (installningObj != null) {
                lasInstallning(installningObj, version)
            } else {
                SakerhetskopiaInstallning(
                    normaltidMinPerDag = 480,
                    standardRastMin = 30,
                    ingangssaldoMin = 0,
                    arbetsdagarMask = 31,
                    stamplaInPaminnelseMinEfterMidnatt =
                        normaliseraStamplaInPaminnelseMinEfterMidnatt(null),
                    aftnarLediga = true,
                    semesterdagarPerAr = 25,
                    semesterarStartmanad = 4,
                )
            }
        val dagarLista = rot.lista("dagar")?.element ?: emptyList()
        val dagar =
            dagarLista.mapNotNull { element ->
                (element as? JsonObjekt)?.let { lasDag(it) }
            }
        if (dagar.size > FLEXTID_SAKERHETSKOPIA_MAX_DAGAR) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        SakerhetskopiaJsonLasResult.Ok(
            SakerhetskopiaInnehall(
                version = version.coerceAtLeast(FLEXTID_SAKERHETSKOPIA_MIN_VERSION),
                skapad = skapad,
                installning = installning,
                dagar = dagar,
            ),
        )
    } catch (_: Exception) {
        SakerhetskopiaJsonLasResult.Fel.TrasigJson
    }
}

private fun lasInstallning(obj: JsonObjekt, version: Int): SakerhetskopiaInstallning {
    val normaltid = obj.heltal("normaltidMinPerDag") ?: 480
    val rast = obj.heltal("standardRastMin") ?: 30
    val ingang = obj.heltal("ingangssaldoMin") ?: 0
    val mask = obj.heltal("arbetsdagarMask") ?: 31
    val stamplaInMin = obj.heltal("stamplaInPaminnelseMinEfterMidnatt")
    val aftnarLediga = obj.boolskt("aftnarLediga") ?: true
    val semesterdagarPerAr = (obj.heltal("semesterdagarPerAr") ?: 25).coerceIn(0, 60)
    val semesterarStartmanadRaw = obj.heltal("semesterarStartmanad") ?: 4
    val semesterarStartmanad = if (semesterarStartmanadRaw == 1) 1 else 4
    return SakerhetskopiaInstallning(
        normaltidMinPerDag = normaltid,
        standardRastMin = rast,
        ingangssaldoMin = ingang,
        arbetsdagarMask = mask,
        stamplaInPaminnelseMinEfterMidnatt = normaliseraStamplaInPaminnelseMinEfterMidnatt(stamplaInMin),
        aftnarLediga = aftnarLediga,
        semesterdagarPerAr = semesterdagarPerAr,
        semesterarStartmanad = semesterarStartmanad,
    )
}

private fun lasDag(obj: JsonObjekt): SakerhetskopiaDag? {
    val datumStr = obj.strang("datum") ?: return null
    val datum =
        try {
            LocalDate.parse(datumStr)
        } catch (_: DateTimeParseException) {
            return null
        }
    return SakerhetskopiaDag(
        datum = datum,
        start = obj.strang("start")?.let { parseTid(it) },
        slut = obj.strang("slut")?.let { parseTid(it) },
        rastMin = obj.heltal("rastMin") ?: 0,
        typ = obj.strang("typ") ?: se.tmconnect.flextid.regler.DagTyp.ARBETE.name,
        anteckning = obj.strang("anteckning"),
    )
}

private fun parseTid(text: String): LocalTime? =
    try {
        LocalTime.parse(text)
    } catch (_: DateTimeParseException) {
        null
    }

private fun skrivInstallningObjekt(installning: SakerhetskopiaInstallning): String =
    buildString {
        append('{')
        append("\"normaltidMinPerDag\":").append(installning.normaltidMinPerDag).append(',')
        append("\"standardRastMin\":").append(installning.standardRastMin).append(',')
        append("\"ingangssaldoMin\":").append(installning.ingangssaldoMin).append(',')
        append("\"arbetsdagarMask\":").append(installning.arbetsdagarMask).append(',')
        append("\"stamplaInPaminnelseMinEfterMidnatt\":")
            .append(installning.stamplaInPaminnelseMinEfterMidnatt)
            .append(',')
        append("\"aftnarLediga\":").append(installning.aftnarLediga).append(',')
        append("\"semesterdagarPerAr\":").append(installning.semesterdagarPerAr).append(',')
        append("\"semesterarStartmanad\":").append(installning.semesterarStartmanad)
        append('}')
    }

private fun skrivDagObjekt(dag: SakerhetskopiaDag): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("datum", jsonStrang(dag.datum.toString()))
        dag.start?.let { laggTillFalt("start", jsonStrang(it.toString())) }
        dag.slut?.let { laggTillFalt("slut", jsonStrang(it.toString())) }
        laggTillFalt("rastMin", dag.rastMin.toString())
        laggTillFalt("typ", jsonStrang(dag.typ))
        dag.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        append('}')
    }

private fun jsonStrang(text: String): String =
    buildString {
        append('"')
        text.forEach { c ->
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(c)
            }
        }
        append('"')
    }

private sealed interface JsonVarde

private data class JsonStrang(val varde: String) : JsonVarde

private data class JsonNummer(val varde: Long) : JsonVarde

private data class JsonBool(val varde: Boolean) : JsonVarde

private class JsonObjekt(val falt: Map<String, JsonVarde>) : JsonVarde {
    fun strang(namn: String): String? = (falt[namn] as? JsonStrang)?.varde

    fun heltal(namn: String): Int? = (falt[namn] as? JsonNummer)?.varde?.toInt()

    fun boolskt(namn: String): Boolean? =
        when (val varde = falt[namn]) {
            is JsonBool -> varde.varde
            is JsonStrang -> varde.varde == "true"
            else -> null
        }

    fun objekt(namn: String): JsonObjekt? = falt[namn] as? JsonObjekt

    fun lista(namn: String): JsonLista? = falt[namn] as? JsonLista

    companion object {
        fun las(json: String): JsonObjekt? = JsonLasare(json).lasRot()
    }
}

private class JsonLista(val element: List<JsonVarde>) : JsonVarde

private class JsonLasare(private val text: String) {
    private var i = 0

    fun lasRot(): JsonObjekt? {
        skipWhitespace()
        return lasObjekt()
    }

    private fun lasObjekt(): JsonObjekt? {
        if (!consume('{')) return null
        val falt = mutableMapOf<String, JsonVarde>()
        skipWhitespace()
        if (peek() == '}') {
            i++
            return JsonObjekt(falt)
        }
        while (true) {
            skipWhitespace()
            val nyckel = lasStrang() ?: return null
            skipWhitespace()
            if (!consume(':')) return null
            skipWhitespace()
            val varde = lasVarde() ?: return null
            falt[nyckel] = varde
            skipWhitespace()
            when {
                peek() == ',' -> {
                    i++
                }
                peek() == '}' -> {
                    i++
                    break
                }
                else -> return null
            }
        }
        return JsonObjekt(falt)
    }

    private fun lasLista(): JsonLista? {
        if (!consume('[')) return null
        val element = mutableListOf<JsonVarde>()
        skipWhitespace()
        if (peek() == ']') {
            i++
            return JsonLista(element)
        }
        while (true) {
            skipWhitespace()
            val varde = lasVarde() ?: return null
            element.add(varde)
            skipWhitespace()
            when {
                peek() == ',' -> i++
                peek() == ']' -> {
                    i++
                    break
                }
                else -> return null
            }
        }
        return JsonLista(element)
    }

    private fun lasVarde(): JsonVarde? {
        skipWhitespace()
        return when (peek()) {
            '"' -> lasStrang()?.let { JsonStrang(it) }
            '{' -> lasObjekt()
            '[' -> lasLista()
            '-', in '0'..'9' -> lasNummer()
            't' -> lasNyckelord("true")?.let { JsonBool(true) }
            'f' -> lasNyckelord("false")?.let { JsonBool(false) }
            else -> null
        }
    }

    private fun lasNyckelord(nyckelord: String): Unit? {
        if (!text.regionMatches(i, nyckelord, 0, nyckelord.length)) return null
        i += nyckelord.length
        return Unit
    }

    private fun lasNummer(): JsonNummer? {
        val start = i
        if (peek() == '-') i++
        while (peek().isDigit()) i++
        return text.substring(start, i).toLongOrNull()?.let { JsonNummer(it) }
    }

    private fun lasStrang(): String? {
        if (!consume('"')) return null
        val sb = StringBuilder()
        while (i < text.length) {
            when (val c = text[i++]) {
                '"' -> return sb.toString()
                '\\' -> {
                    if (i >= text.length) return null
                    when (text[i++]) {
                        '"' -> sb.append('"')
                        '\\' -> sb.append('\\')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        't' -> sb.append('\t')
                        else -> return null
                    }
                }
                else -> sb.append(c)
            }
        }
        return null
    }

    private fun skipWhitespace() {
        while (i < text.length && text[i].isWhitespace()) i++
    }

    private fun peek(): Char = if (i < text.length) text[i] else '\u0000'

    private fun consume(c: Char): Boolean {
        skipWhitespace()
        if (i < text.length && text[i] == c) {
            i++
            return true
        }
        return false
    }
}
