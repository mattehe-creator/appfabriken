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
