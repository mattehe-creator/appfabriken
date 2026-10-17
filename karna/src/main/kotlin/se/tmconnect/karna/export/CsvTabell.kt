package se.tmconnect.karna.export

/**
 * CSV med semikolon (svenska Excel) och CRLF. Celler som börjar med `=`, `+`, `-` eller `@`
 * får inledande `'` så att de inte tolkas som formler.
 */
fun csvCell(text: String): String {
    val saker = if (text.isNotEmpty() && text[0] in "=+-@\t\r") "'$text" else text
    return if (saker.any { it == ';' || it == '"' || it == '\n' || it == '\r' }) {
        "\"" + saker.replace("\"", "\"\"") + "\""
    } else {
        saker
    }
}

fun csvRad(celler: List<String>): String = celler.joinToString(";") { csvCell(it) }

fun csvDokument(rader: List<String>): String = rader.joinToString("\r\n") + "\r\n"

/** Belopp som decimaltal med punkt (t.ex. `1299.50`). */
fun prisSomDecimal(prisOre: Long): String {
    val tecken = if (prisOre < 0) "-" else ""
    val absolut = kotlin.math.abs(prisOre)
    return "$tecken${absolut / 100}.${(absolut % 100).toString().padStart(2, '0')}"
}

fun skrivCsvMedBom(outputStream: java.io.OutputStream, csv: String) {
    outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
    outputStream.write(csv.toByteArray(Charsets.UTF_8))
}
