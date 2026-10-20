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
            val kontext = text.substring(
                maxOf(0, matchTkrMnkr.range.first),
                minOf(text.length, matchTkrMnkr.range.last + 60),
            )
            
            val iTusen = enhet == "tkr"
            val kronor = parseBelopp(talText, iTusen) ?: continue
            
            // Om enheten är mnkr, multiplicera med 1 000 000
            val finalKronor = if (enhet == "mnkr") {
                kronor * 1_000_000
            } else {
                kronor
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
    val normaliserat = text.replace(" ", "").replace(",", ".")
    val tal = normaliserat.toBigDecimalOrNull() ?: return null
    val kronor = if (iTusentals) {
        tal.multiply(java.math.BigDecimal(1000)).toLong()
    } else {
        tal.toLong()
    }
    return kronor
}
