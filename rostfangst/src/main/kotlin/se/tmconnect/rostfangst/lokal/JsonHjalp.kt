package se.tmconnect.rostfangst.lokal

import java.lang.StringBuilder
import java.util.regex.Pattern

fun jsonCitera(text: String): String {
    val result = StringBuilder()
    result.append("\"")
    
    for (i in text.indices) {
        when (val c = text[i]) {
            '"' -> result.append("\\\"")
            '\\' -> result.append("\\\\")
            '\n' -> result.append("\\n")
            '\r' -> result.append("\\r")
            '\t' -> result.append("\\t")
            else -> {
                if (c < 0x20) {
                    // För styrtecken under U+0020, använd \uXXXX-format
                    val hex = Integer.toHexString(c.toInt()).lowercase()
                    result.append("\\u")
                    for (j in 1..4 - hex.length) {
                        result.append("0")
                    }
                    result.append(hex)
                } else {
                    result.append(c)
                }
            }
        }
    }
    
    result.append("\"")
    return result.toString()
}

fun lasJsonStrang(json: String, nyckel: String): String? {
    // Sök efter nyckeln följt av : och ett strängvärde
    val pattern = Pattern.compile("\"$nyckel\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
    val matcher = pattern.matcher(json)
    
    if (!matcher.find()) {
        return null
    }
    
    val escapedValue = matcher.group(1) ?: return null
    
    // Avkoda escape-sekvenser
    val result = StringBuilder()
    var i = 0
    while (i < escapedValue.length) {
        val c = escapedValue[i]
        if (c == '\\' && i + 1 < escapedValue.length) {
            when (val next = escapedValue[i + 1]) {
                '"' -> result.append('"')
                '\\' -> result.append('\\')
                '/' -> result.append('/')
                'b' -> result.append('\b')
                'f' -> result.append('\u000C') // Form feed
                'n' -> result.append('\n')
                'r' -> result.append('\r')
                't' -> result.append('\t')
                'u' -> {
                    // Hantera \uXXXX format
                    if (i + 5 < escapedValue.length) {
                        try {
                            val hex = escapedValue.substring(i + 2, i + 6)
                            val codePoint = Integer.parseInt(hex, 16)
                            result.append(codePoint.toChar())
                            i += 4 // Hoppa över de fyra hex-siffrorna
                        } catch (e: NumberFormatException) {
                            return null
                        }
                    } else {
                        return null
                    }
                }
                else -> result.append(next)
            }
            i += 2 // Hoppa över backslash och nästa tecken
        } else {
            result.append(c)
            i++
        }
    }
    
    return result.toString()
}
