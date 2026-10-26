package se.tmconnect.rostfangst.lokal

fun jsonCitera(text: String): String {
    val sb = StringBuilder()
    sb.append('"')
    for (c in text) {
        when (c) {
            '"' -> sb.append("\\\"")
            '\\' -> sb.append("\\\\")
            '\n' -> sb.append("\\n")
            '\r' -> sb.append("\\r")
            '\t' -> sb.append("\\t")
            else -> {
                if (c < 0x20) {
                    sb.append(String.format("\\u%04x", c.toInt()))
                } else {
                    sb.append(c)
                }
            }
        }
    }
    sb.append('"')
    return sb.toString()
}

fun lasJsonStrang(json: String, nyckel: String): String? {
    val nyckelStr = "\"$nyckel\""
    val index = json.indexOf(nyckelStr)
    if (index == -1) return null

    var i = index + nyckelStr.length
    // Hoppa över blanksteg efter nyckeln
    while (i < json.length && json[i].isWhitespace()) {
        i++
    }
    // Kontrollera att det finns ett kolon
    if (i >= json.length || json[i] != ':') return null
    i++

    // Hoppa över blanksteg efter kolonet
    while (i < json.length && json[i].isWhitespace()) {
        i++
    }

    // Kontrollera att det är en sträng
    if (i >= json.length || json[i] != '"') return null

    i++ // Hoppa över första citationstecknet
    val sb = StringBuilder()
    while (i < json.length) {
        val c = json[i]
        when {
            c == '\\' -> {
                i++
                if (i >= json.length) return null
                when (json[i]) {
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '/' -> sb.append('/')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    'b' -> sb.append('\b')
                    'f' -> sb.append('\u000c') // \f
                    'u' -> {
                        if (i + 4 >= json.length) return null
                        try {
                            val hex = json.substring(i + 1, i + 5)
                            val codePoint = hex.toInt(16)
                            sb.append(codePoint.toChar())
                            i += 4
                        } catch (e: NumberFormatException) {
                            return null
                        }
                    }
                    else -> sb.append('\\').append(json[i])
                }
            }
            c == '"' -> {
                return sb.toString()
            }
            else -> {
                sb.append(c)
            }
        }
        i++
    }

    // Strängen är oavslutad
    return null
}
