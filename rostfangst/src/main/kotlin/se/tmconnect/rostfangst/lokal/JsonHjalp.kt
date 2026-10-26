package se.tmconnect.rostfangst.lokal

import java.util.regex.Pattern

fun jsonCitera(text: String): String {
    val ut = StringBuilder()
    ut.append('"')
    for (c in text) {
        when {
            c == '"' -> ut.append("\\\"")
            c == '\\' -> ut.append("\\\\")
            c == '\n' -> ut.append("\\n")
            c == '\r' -> ut.append("\\r")
            c == '\t' -> ut.append("\\t")
            c.code < 0x20 -> ut.append("\\u").append(c.code.toString(16).padStart(4, '0'))
            else -> ut.append(c)
        }
    }
    ut.append('"')
    return ut.toString()
}

fun lasJsonStrang(json: String, nyckel: String): String? {
    val monster = Pattern.compile("\"" + Pattern.quote(nyckel) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
    val traff = monster.matcher(json)
    if (!traff.find()) return null
    val rå = traff.group(1) ?: return null

    val ut = StringBuilder()
    var i = 0
    while (i < rå.length) {
        val c = rå[i]
        if (c != '\\') {
            ut.append(c)
            i++
            continue
        }
        if (i + 1 >= rå.length) return null
        when (rå[i + 1]) {
            '"' -> ut.append('"')
            '\\' -> ut.append('\\')
            '/' -> ut.append('/')
            'b' -> ut.append('\b')
            'f' -> ut.append('\u000C')
            'n' -> ut.append('\n')
            'r' -> ut.append('\r')
            't' -> ut.append('\t')
            'u' -> {
                if (i + 6 > rå.length) return null
                val kod = rå.substring(i + 2, i + 6).toIntOrNull(16) ?: return null
                ut.append(kod.toChar())
                i += 4
            }
            else -> return null
        }
        i += 2
    }
    return ut.toString()
}
