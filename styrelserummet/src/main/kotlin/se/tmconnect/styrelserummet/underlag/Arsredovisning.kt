package se.tmconnect.styrelserummet.underlag

data class Belopp(val kronor: Long, val kalla: String)

private val byggnadsRader = listOf(
    Regex("""Byggnader\s+och\s+mark""", RegexOption.IGNORE_CASE),
    Regex("""Byggnader\b""", RegexOption.IGNORE_CASE),
)

fun hittaByggnadensVarde(text: String): Belopp? {
    val rader = text.lines()
    for (i in rader.indices) {
        val rad = rader[i]
        if (byggnadsRader.none { it.containsMatchIn(rad) }) continue
        val tal = hittaTalPaRadEllerNasta(rader, i) ?: continue
        return tal
    }
    return null
}

private fun hittaTalPaRadEllerNasta(rader: List<String>, start: Int): Belopp? {
    for (j in start..(start + 2).coerceAtMost(rader.lastIndex)) {
        parseBelopp(rader[j])?.let { return it }
    }
    return null
}

private fun parseBelopp(rad: String): Belopp? {
    val tusen = Regex("""(\d[\d\s]{0,12})\s*(?:tkr|Tkr|TUSEN)?""").find(rad) ?: return null
    val raw = tusen.groupValues[1].replace(" ", "").replace("\u00a0", "")
    if (raw.isEmpty()) return null
    val heltal = raw.toLongOrNull() ?: return null
    val arTusen = rad.contains("tkr", true) || rad.contains("tusen", true) ||
        (rad.contains("000") && heltal < 1_000_000)
    val kronor = if (arTusen && heltal < 1_000_000) heltal * 1000 else heltal
    return Belopp(kronor, rad.trim())
}
