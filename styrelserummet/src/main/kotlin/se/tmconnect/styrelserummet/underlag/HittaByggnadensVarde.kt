package se.tmconnect.styrelserummet.underlag

data class Belopp(
    val kronor: Long,
    val iTusentals: Boolean,
)

private val rubriker = listOf("Byggnader och mark", "Byggnader")

fun hittaByggnadensVarde(text: String): Belopp? {
    for (rubrik in rubriker) {
        // Först sök efter tkr eller mnkr
        val regexTkrMnkr = Regex(
            """$rubrik[^\d\n]*([\d][\d\s]*(?:,\d+)?)[\s]*(tkr|mnkr)""",
            RegexOption.IGNORE_CASE,
        )
        val matchTkrMnkr = regexTkrMnkr.find(text)
        
        if (matchTkrMnkr != null) {
            val talText = matchTkrMnkr.groupValues[1].trim()
            val enhet = matchTkrMnkr.groupValues[2].lowercase()

            val iTusen = enhet == "tkr"
            val finalKronor = if (enhet == "mnkr") {
                val normaliserat = talText.replace(" ", "").replace(",", ".")
                val tal = normaliserat.toBigDecimalOrNull() ?: continue
                tal.multiply(java.math.BigDecimal(1_000_000)).toLong()
            } else {
                parseBelopp(talText, iTusentals = true) ?: continue
            }

            return Belopp(finalKronor, iTusen)
        }
        
        // Fallback till befintlig logik för "tusen kronor"
        val regex = Regex(
            """$rubrik[^\d\n]*([\d][\d\s]*(?:,\d+)?)""",
            RegexOption.IGNORE_CASE,
        )
        val match = regex.find(text) ?: continue
        val talText = match.groupValues[1].trim()
        val kontext = text.substring(
            maxOf(0, match.range.first),
            minOf(text.length, match.range.last + 60),
        )
        val iTusen = kontext.contains("tusen", ignoreCase = true) ||
            kontext.contains("tkr", ignoreCase = true)
        val kronor = parseBelopp(talText, iTusen) ?: continue
        return Belopp(kronor, iTusen)
    }
    return null
}

internal fun parseBelopp(text: String, iTusentals: Boolean): Long? {
    val normalizedText = text
        .replace("\u00A0", " ")
        .replace("\u2009", " ")
        .replace(".", " ")
    val cleanText = normalizedText.replace(" ", "")
    val hasComma = text.contains(',')
    val tal = if (hasComma) {
        cleanText.replace(",", ".").toBigDecimalOrNull()
    } else {
        cleanText.toBigDecimalOrNull()
    } ?: return null
    return if (iTusentals) {
        tal.multiply(java.math.BigDecimal(1000)).toLong()
    } else {
        tal.toLong()
    }
}
