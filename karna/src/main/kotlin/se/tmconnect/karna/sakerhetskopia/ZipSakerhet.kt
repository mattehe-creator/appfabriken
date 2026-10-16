package se.tmconnect.karna.sakerhetskopia

import java.io.ByteArrayOutputStream
import java.io.InputStream

fun arSakerZipPost(namn: String): Boolean {
    if (namn.contains("..")) {
        return false
    }
    if (namn.startsWith("/") || namn.startsWith("\\")) {
        return false
    }
    if (Regex("""^[A-Za-z]:[/\\]""").containsMatchIn(namn)) {
        return false
    }
    return true
}

fun lasBegransadeBytes(input: InputStream, maxBytes: Int): ByteArray {
    val buffer = ByteArrayOutputStream()
    val chunk = ByteArray(8192)
    var totalSize = 0
    while (true) {
        val las = input.read(chunk)
        if (las <= 0) {
            break
        }
        totalSize += las
        if (totalSize > maxBytes) {
            throw IllegalStateException("Filen är för stor")
        }
        buffer.write(chunk, 0, las)
    }
    return buffer.toByteArray()
}
