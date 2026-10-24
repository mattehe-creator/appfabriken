package se.tmconnect.styrelserummet.server

private const val MAX_FILNAMN_LANGD = 200
private const val MAX_FORENINGSNAMN_LANGD = 120
private val PDF_PREFIX = "%PDF-".encodeToByteArray()

fun sakertFilnamn(namn: String): String {
    var s = namn.replace('\\', '/').substringAfterLast('/')
    while (s.contains("..")) {
        s = s.replace("..", "")
    }
    s = s.filter { it.category != CharCategory.CONTROL }
    if (s.length > MAX_FILNAMN_LANGD) {
        s = s.take(MAX_FILNAMN_LANGD)
    }
    return s.ifEmpty { "fil.pdf" }
}

fun arPdf(forstaByte: ByteArray): Boolean {
    if (forstaByte.size < PDF_PREFIX.size) return false
    for (i in PDF_PREFIX.indices) {
        if (forstaByte[i] != PDF_PREFIX[i]) return false
    }
    return true
}

fun valideraForeningsnamn(namn: String): String? {
    if (namn.isEmpty()) return "Ange föreningens namn."
    if (namn.length > MAX_FORENINGSNAMN_LANGD) {
        return "Föreningens namn får vara högst $MAX_FORENINGSNAMN_LANGD tecken."
    }
    if (namn.any { it.category == CharCategory.CONTROL }) {
        return "Föreningens namn innehåller ogiltiga tecken."
    }
    return null
}

fun valideraArsslut(text: String, nuAr: Int): Int? {
    val ar = text.trim().toIntOrNull() ?: return null
    if (ar !in 2000..nuAr + 1) return null
    return ar
}

/**
 * Är filen årsredovisningen (och inte underhållsplanen)? Namnet avgör om det säger det
 * ("årsredovisning", "arsredovisning", "redovisning"); annars innehållet: en årsredovisning har
 * både resultaträkning och balansräkning. Tidigare räckte "ars" i namnet, vilket slog till på
 * till exempel "Brf Marsvinet underhållsplan".
 */
fun arArsredovisning(filnamn: String, text: String): Boolean {
    val namn = filnamn.lowercase()
    if (namn.contains("underh")) return false
    if (namn.contains("redov") || namn.contains("rsred") || namn.contains("bokslut")) return true
    val innehall = text.lowercase()
    return innehall.contains("resultaträkning") && innehall.contains("balansräkning")
}

/** Lagringsnamn med löpnummer först, så att två filer med samma namn inte skriver över varandra. */
fun lagringsnamn(lopnummer: Int, filnamn: String): String =
    "${lopnummer.toString().padStart(2, '0')}-${sakertFilnamn(filnamn)}"
