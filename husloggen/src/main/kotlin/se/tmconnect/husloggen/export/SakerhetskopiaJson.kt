package se.tmconnect.husloggen.export

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
        append("\"bostader\":[")
        innehall.bostader.forEachIndexed { index, bostad ->
            if (index > 0) append(',')
            append(skrivBostadObjekt(bostad))
        }
        append("],\"uppgifter\":[")
        innehall.uppgifter.forEachIndexed { index, uppgift ->
            if (index > 0) append(',')
            append(skrivUppgiftObjekt(uppgift))
        }
        append("],\"poster\":[")
        innehall.poster.forEachIndexed { index, post ->
            if (index > 0) append(',')
            append(skrivPostObjekt(post))
        }
        append("],\"matare\":[")
        innehall.matare.forEachIndexed { index, matare ->
            if (index > 0) append(',')
            append(skrivMatareObjekt(matare))
        }
        append("],\"avlasningar\":[")
        innehall.avlasningar.forEachIndexed { index, avlasning ->
            if (index > 0) append(',')
            append(skrivAvlasningObjekt(avlasning))
        }
        append("],\"dokument\":[")
        innehall.dokument.forEachIndexed { index, dokument ->
            if (index > 0) append(',')
            append(skrivDokumentObjekt(dokument))
        }
        append("],\"planeradeKostnader\":[")
        innehall.planeradeKostnader.forEachIndexed { index, plan ->
            if (index > 0) append(',')
            append(skrivPlaneradKostnadObjekt(plan))
        }
        append("]}")
    }

private fun skrivBostadObjekt(bostad: SakerhetskopiaBostad): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("namn", jsonStrang(bostad.namn))
        bostad.adress?.let { laggTillFalt("adress", jsonStrang(it)) }
        bostad.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        append('}')
    }

private fun skrivUppgiftObjekt(uppgift: SakerhetskopiaUppgift): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("namn", jsonStrang(uppgift.namn))
        laggTillFalt("intervallManader", uppgift.intervallManader.toString())
        laggTillFalt("startDatum", jsonStrang(uppgift.startDatum.toString()))
        uppgift.senastGjord?.let { laggTillFalt("senastGjord", jsonStrang(it.toString())) }
        uppgift.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        laggTillFalt("bostadIndex", uppgift.bostadIndex.toString())
        append('}')
    }

private fun skrivPostObjekt(post: SakerhetskopiaPost): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("datum", jsonStrang(post.datum.toString()))
        laggTillFalt("vad", jsonStrang(post.vad))
        laggTillFalt("typ", jsonStrang(post.typ))
        post.beloppOre?.let { laggTillFalt("beloppOre", it.toString()) }
        post.rotRutAvdragOre?.let { laggTillFalt("rotRutAvdragOre", it.toString()) }
        post.utforare?.let { laggTillFalt("utforare", jsonStrang(it)) }
        post.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        post.uppgiftIndex?.let { laggTillFalt("uppgiftIndex", it.toString()) }
        if (post.bilder.isNotEmpty()) {
            laggTillFalt("bilder", post.bilder.joinToString(",", "[", "]") { jsonStrang(it) })
        }
        laggTillFalt("bostadIndex", post.bostadIndex.toString())
        append('}')
    }

private fun skrivMatareObjekt(matare: SakerhetskopiaMatare): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("namn", jsonStrang(matare.namn))
        laggTillFalt("enhet", jsonStrang(matare.enhet))
        matare.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        matare.prisOrePerEnhet?.let { laggTillFalt("prisOrePerEnhet", it.toString()) }
        laggTillFalt("bostadIndex", matare.bostadIndex.toString())
        append('}')
    }

private fun skrivDokumentObjekt(dokument: SakerhetskopiaDokument): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("titel", jsonStrang(dokument.titel))
        dokument.kategori?.let { laggTillFalt("kategori", jsonStrang(it)) }
        dokument.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        laggTillFalt("filnamn", jsonStrang(dokument.filnamn))
        laggTillFalt("mimeTyp", jsonStrang(dokument.mimeTyp))
        laggTillFalt("tillagd", jsonStrang(dokument.tillagd.toString()))
        laggTillFalt("bostadIndex", dokument.bostadIndex.toString())
        append('}')
    }

private fun skrivPlaneradKostnadObjekt(plan: SakerhetskopiaPlaneradKostnad): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("titel", jsonStrang(plan.titel))
        laggTillFalt("beloppOre", plan.beloppOre.toString())
        laggTillFalt("planeratAr", plan.planeratAr.toString())
        plan.anteckning?.let { laggTillFalt("anteckning", jsonStrang(it)) }
        laggTillFalt("bostadIndex", plan.bostadIndex.toString())
        append('}')
    }

private fun skrivAvlasningObjekt(avlasning: SakerhetskopiaAvlasning): String =
    buildString {
        append('{')
        var forsta = true
        fun laggTillFalt(namn: String, varde: String) {
            if (!forsta) append(',')
            forsta = false
            append('"').append(namn).append("\":").append(varde)
        }
        laggTillFalt("matareIndex", avlasning.matareIndex.toString())
        laggTillFalt("datum", jsonStrang(avlasning.datum.toString()))
        laggTillFalt("vardeTusendel", avlasning.vardeTusendel.toString())
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
        if (version.toInt() > HUSLOGGEN_SAKERHETSKOPIA_VERSION) {
            return SakerhetskopiaJsonLasResult.Fel.OkandVersion
        }
        if (version.toInt() < HUSLOGGEN_SAKERHETSKOPIA_MIN_VERSION) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val skapad = rot["skapad"] as? String ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        val rawBostader = rot["bostader"] as? List<*> ?: emptyList<Any>()
        if (rawBostader.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_BOSTADER) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val bostader =
            rawBostader.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasBostadObjekt(falt as Map<String, Any?>)
            }
        if (bostader.size != rawBostader.size && rawBostader.isNotEmpty()) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val rawUppgifter = rot["uppgifter"] as? List<*> ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        val rawPoster = rot["poster"] as? List<*> ?: return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        if (rawUppgifter.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_UPPGIFTER ||
            rawPoster.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_POSTER
        ) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val uppgifter =
            rawUppgifter.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasUppgiftObjekt(falt as Map<String, Any?>)
            }
        val poster =
            rawPoster.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasPostObjekt(falt as Map<String, Any?>)
            }
        if (uppgifter.size != rawUppgifter.size || poster.size != rawPoster.size) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val rawMatare = rot["matare"] as? List<*> ?: emptyList<Any>()
        val rawAvlasningar = rot["avlasningar"] as? List<*> ?: emptyList<Any>()
        if (rawMatare.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_MATARE ||
            rawAvlasningar.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_AVLASNINGAR
        ) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val matare =
            rawMatare.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasMatareObjekt(falt as Map<String, Any?>)
            }
        val avlasningar =
            rawAvlasningar.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasAvlasningObjekt(falt as Map<String, Any?>)
            }
        if (matare.size != rawMatare.size || avlasningar.size != rawAvlasningar.size) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val rawDokument = rot["dokument"] as? List<*> ?: emptyList<Any>()
        if (rawDokument.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_DOKUMENT) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val dokument =
            rawDokument.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasDokumentObjekt(falt as Map<String, Any?>)
            }
        if (dokument.size != rawDokument.size) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val rawPlanerade = rot["planeradeKostnader"] as? List<*> ?: emptyList<Any>()
        if (rawPlanerade.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_PLANERADE_KOSTNADER) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        val planeradeKostnader =
            rawPlanerade.mapNotNull { element ->
                val falt = element as? Map<*, *> ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                lasPlaneradKostnadObjekt(falt as Map<String, Any?>)
            }
        if (planeradeKostnader.size != rawPlanerade.size) {
            return SakerhetskopiaJsonLasResult.Fel.TrasigJson
        }
        SakerhetskopiaJsonLasResult.Ok(
            SakerhetskopiaInnehall(
                version = version.toInt(),
                skapad = skapad,
                bostader = bostader,
                uppgifter = uppgifter,
                poster = poster,
                matare = matare,
                avlasningar = avlasningar,
                dokument = dokument,
                planeradeKostnader = planeradeKostnader,
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

private fun lasBostadIndex(falt: Map<String, Any?>): Int =
    when (val v = falt["bostadIndex"]) {
        null -> 0
        is Number -> v.toInt()
        else -> 0
    }

private fun lasBostadObjekt(falt: Map<String, Any?>): SakerhetskopiaBostad? {
    val namn = falt["namn"] as? String ?: return null
    return SakerhetskopiaBostad(
        namn = namn,
        adress = falt["adress"] as? String,
        anteckning = falt["anteckning"] as? String,
    )
}

private fun lasUppgiftObjekt(falt: Map<String, Any?>): SakerhetskopiaUppgift? {
    val namn = falt["namn"] as? String ?: return null
    val intervall = falt["intervallManader"] as? Number ?: return null
    val start = lasDatum(falt["startDatum"]) ?: return null
    return SakerhetskopiaUppgift(
        namn = namn,
        intervallManader = intervall.toInt(),
        startDatum = start,
        senastGjord = lasDatum(falt["senastGjord"]),
        anteckning = falt["anteckning"] as? String,
        bostadIndex = lasBostadIndex(falt),
    )
}

private fun lasMatareObjekt(falt: Map<String, Any?>): SakerhetskopiaMatare? {
    val namn = falt["namn"] as? String ?: return null
    val enhet = falt["enhet"] as? String ?: return null
    val prisOrePerEnhet = (falt["prisOrePerEnhet"] as? Number)?.toLong()
    return SakerhetskopiaMatare(
        namn = namn,
        enhet = enhet,
        anteckning = falt["anteckning"] as? String,
        prisOrePerEnhet = prisOrePerEnhet,
        bostadIndex = lasBostadIndex(falt),
    )
}

private fun lasDokumentObjekt(falt: Map<String, Any?>): SakerhetskopiaDokument? {
    val titel = falt["titel"] as? String ?: return null
    val filnamn = falt["filnamn"] as? String ?: return null
    val mimeTyp = falt["mimeTyp"] as? String ?: return null
    val tillagd = lasDatum(falt["tillagd"]) ?: return null
    return SakerhetskopiaDokument(
        titel = titel,
        kategori = falt["kategori"] as? String,
        anteckning = falt["anteckning"] as? String,
        filnamn = filnamn,
        mimeTyp = mimeTyp,
        tillagd = tillagd,
        bostadIndex = lasBostadIndex(falt),
    )
}

private fun lasPlaneradKostnadObjekt(falt: Map<String, Any?>): SakerhetskopiaPlaneradKostnad? {
    val titel = falt["titel"] as? String ?: return null
    val belopp =
        when (val b = falt["beloppOre"]) {
            is Number -> b.toLong()
            else -> return null
        }
    val planeratAr =
        when (val a = falt["planeratAr"]) {
            is Number -> a.toInt()
            else -> return null
        }
    return SakerhetskopiaPlaneradKostnad(
        titel = titel,
        beloppOre = belopp,
        planeratAr = planeratAr,
        anteckning = falt["anteckning"] as? String,
        bostadIndex = lasBostadIndex(falt),
    )
}

private fun lasAvlasningObjekt(falt: Map<String, Any?>): SakerhetskopiaAvlasning? {
    val matareIndex = falt["matareIndex"] as? Number ?: return null
    val datum = lasDatum(falt["datum"]) ?: return null
    val varde =
        when (val v = falt["vardeTusendel"]) {
            is Number -> v.toLong()
            else -> return null
        }
    return SakerhetskopiaAvlasning(
        matareIndex = matareIndex.toInt(),
        datum = datum,
        vardeTusendel = varde,
    )
}

private fun lasPostObjekt(falt: Map<String, Any?>): SakerhetskopiaPost? {
    val vad = falt["vad"] as? String ?: return null
    val datum = lasDatum(falt["datum"]) ?: return null
    val typ = falt["typ"] as? String ?: return null
    val belopp =
        when (val p = falt["beloppOre"]) {
            null -> null
            is Number -> p.toLong()
            else -> return null
        }
    val rotRutAvdrag =
        when (val p = falt["rotRutAvdragOre"]) {
            null -> null
            is Number -> p.toLong()
            else -> return null
        }
    val uppgiftIndex =
        when (val u = falt["uppgiftIndex"]) {
            null -> null
            is Number -> u.toInt()
            else -> return null
        }
    val bilder = (falt["bilder"] as? List<*>)?.filterIsInstance<String>().orEmpty()
    return SakerhetskopiaPost(
        datum = datum,
        vad = vad,
        typ = typ,
        beloppOre = belopp,
        rotRutAvdragOre = rotRutAvdrag,
        utforare = falt["utforare"] as? String,
        anteckning = falt["anteckning"] as? String,
        uppgiftIndex = uppgiftIndex,
        bilder = bilder,
        bostadIndex = lasBostadIndex(falt),
    )
}

private class JsonParser(private val text: String) {
    private var index = 0

    fun lasObjekt(): Map<String, Any?>? {
        hoppaOverWhitespace()
        if (!match('{')) return null
        hoppaOverWhitespace()
        if (match('}')) return emptyMap()
        val result = linkedMapOf<String, Any?>()
        while (true) {
            hoppaOverWhitespace()
            val nyckel = lasStrang() ?: return null
            hoppaOverWhitespace()
            if (!match(':')) return null
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
        if (!match('[')) return null
        hoppaOverWhitespace()
        if (match(']')) return emptyList()
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
        if (!match('"')) return null
        val builder = StringBuilder()
        while (index < text.length) {
            when (val c = text[index++]) {
                '"' -> return builder.toString()
                '\\' -> {
                    if (index >= text.length) return null
                    when (val esc = text[index++]) {
                        '"', '\\', '/' -> builder.append(esc)
                        'b' -> builder.append('\b')
                        'f' -> builder.append('\u000C')
                        'n' -> builder.append('\n')
                        'r' -> builder.append('\r')
                        't' -> builder.append('\t')
                        'u' -> {
                            if (index + 4 > text.length) return null
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
        if (peek() == '-') index++
        while (index < text.length && text[index].isDigit()) index++
        if (index == start || (index == start + 1 && text[start] == '-')) return null
        return text.substring(start, index).toLongOrNull()
    }

    private fun hoppaOverWhitespace() {
        while (index < text.length && text[index].isWhitespace()) index++
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
