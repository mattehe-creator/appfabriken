package se.tmconnect.rostfangst.wear

fun kodaFangst(text: String): ByteArray = text.trim().toByteArray(Charsets.UTF_8)

fun avkodaFangst(data: ByteArray): String? {
    val text = data.toString(Charsets.UTF_8).trim()
    if (text.isEmpty() || text.length > 2000) return null
    return text
}
