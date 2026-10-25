package se.tmconnect.rostfangst.wear

fun kodaFangst(text: String): ByteArray {
    val trimmad = text.trim()
    return trimmad.toByteArray(Charsets.UTF_8)
}

fun avkodaFangst(data: ByteArray): String? {
    val str = data.toString(Charsets.UTF_8).trim()
    if (str.isEmpty() || str.length > 2000 || str.isBlank()) {
        return null
    }
    return str
}
