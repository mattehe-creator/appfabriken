package se.tmconnect.rostfangst.wear

fun kodaFangst(text: String): ByteArray {
    val trimmed = text.trim()
    return trimmed.toByteArray(Charsets.UTF_8)
}

fun avkodaFangst(data: ByteArray): String? {
    val decoded = data.toString(Charsets.UTF_8).trim()
    if (decoded.isEmpty() || decoded.length > 2000 || decoded.matches(Regex("^\\s+$"))) {
        return null
    }
    return decoded
}
