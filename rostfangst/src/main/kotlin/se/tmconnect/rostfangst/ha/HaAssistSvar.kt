package se.tmconnect.rostfangst.ha

/** Plockar response.speech.plain.speech ur Assist-svaret. Ren Kotlin: org.json finns inte i enhetstester. */
fun lasAssistSvar(json: String): String? {
    val rot = try {
        MiniJson(json.trim()).lasHela()
    } catch (_: IllegalArgumentException) {
        return null
    }
    var nu: Any? = rot
    for (nyckel in listOf("response", "speech", "plain", "speech")) {
        nu = (nu as? Map<*, *>)?.get(nyckel) ?: return null
    }
    val text = (nu as? String)?.trim() ?: return null
    return text.ifEmpty { null }
}

/** Minimal JSON-läsare: objekt, listor, text, tal, true/false/null. Kastar IllegalArgumentException vid fel. */
private class MiniJson(private val s: String) {
    private var i = 0

    fun lasHela(): Any? {
        val v = varde()
        blanksteg()
        require(i == s.length) { "extra text" }
        return v
    }

    private fun blanksteg() {
        while (i < s.length && s[i].isWhitespace()) i++
    }

    private fun varde(): Any? {
        blanksteg()
        require(i < s.length) { "slut" }
        return when (val c = s[i]) {
            '{' -> objekt()
            '[' -> lista()
            '"' -> text()
            else -> if (c == '-' || c.isDigit()) tal() else ord()
        }
    }

    private fun objekt(): Map<String, Any?> {
        val m = LinkedHashMap<String, Any?>()
        i++
        blanksteg()
        if (i < s.length && s[i] == '}') { i++; return m }
        while (true) {
            blanksteg()
            require(i < s.length && s[i] == '"') { "nyckel" }
            val k = text()
            blanksteg()
            require(i < s.length && s[i] == ':') { "kolon" }
            i++
            m[k] = varde()
            blanksteg()
            require(i < s.length) { "slut" }
            if (s[i] == ',') { i++; continue }
            require(s[i] == '}') { "objekt" }
            i++
            return m
        }
    }

    private fun lista(): List<Any?> {
        val l = ArrayList<Any?>()
        i++
        blanksteg()
        if (i < s.length && s[i] == ']') { i++; return l }
        while (true) {
            l.add(varde())
            blanksteg()
            require(i < s.length) { "slut" }
            if (s[i] == ',') { i++; continue }
            require(s[i] == ']') { "lista" }
            i++
            return l
        }
    }

    private fun text(): String {
        val sb = StringBuilder()
        i++
        while (true) {
            require(i < s.length) { "otermininerad text" }
            val c = s[i++]
            when (c) {
                '"' -> return sb.toString()
                '\\' -> {
                    require(i < s.length) { "escape" }
                    when (val e = s[i++]) {
                        'n' -> sb.append('\n')
                        't' -> sb.append('\t')
                        'r' -> sb.append('\r')
                        'b' -> sb.append('\b')
                        'f' -> sb.append('\u000C')
                        'u' -> {
                            require(i + 4 <= s.length) { "unicode" }
                            sb.append(s.substring(i, i + 4).toInt(16).toChar())
                            i += 4
                        }
                        else -> sb.append(e)
                    }
                }
                else -> sb.append(c)
            }
        }
    }

    private fun tal(): Any {
        val start = i
        while (i < s.length && (s[i].isDigit() || s[i] in "+-.eE")) i++
        return s.substring(start, i).toDoubleOrNull() ?: throw IllegalArgumentException("tal")
    }

    private fun ord(): Any? {
        for ((o, v) in listOf("true" to true, "false" to false, "null" to null)) {
            if (s.startsWith(o, i)) { i += o.length; return v }
        }
        throw IllegalArgumentException("okänt värde")
    }
}
