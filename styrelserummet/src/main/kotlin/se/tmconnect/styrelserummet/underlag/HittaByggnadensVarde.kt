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
                val tal = normaliserat.toBigDecimalOrNull() ?: return null
                tal.multiply(java.math.BigDecimal(1_000_000)).toLong()
            } else {
                parseBelopp(talText, iTusentals = true) ?: return null
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
        
        // Kontrollera om det är en rad med två kolumner (årtal)
        val lines = text.lines()
        val matchLineIndex = lines.indexOfFirst { it.contains(rubrik, ignoreCase = true) }
        if (matchLineIndex >= 0 && matchLineIndex + 1 < lines.size) {
            val nextLine = lines[matchLineIndex + 1]
            // Om nästa rad är en årtalsrad (endast siffror separerade med mellanrum)
            if (Regex("""^\s*\d{4}\s+\d{4}\s*$""").matches(nextLine)) {
                // Extrahera första talet från nästa rad
                val numbers = nextLine.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
                if (numbers.size >= 2) {
                    val firstNumber = numbers[0]
                    val kronor = parseBelopp(firstNumber, iTusentals = false) ?: return null
                    return Belopp(kronor, false)
                }
            }
        }
        
        val kontext = text.substring(
            maxOf(0, match.range.first),
            minOf(text.length, match.range.last + 60),
        )
        val iTusen = kontext.contains("tusen", ignoreCase = true) ||
            kontext.contains("tkr", ignoreCase = true)
        val kronor = parseBelopp(talText, iTusen) ?: return null
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
