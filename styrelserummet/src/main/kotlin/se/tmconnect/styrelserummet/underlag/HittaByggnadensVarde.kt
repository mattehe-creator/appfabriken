package se.tmconnect.styrelserummet.underlag

data class Belopp(
    val kronor: Long,
    val iTusentals: Boolean,
)

private val rubriker = listOf("Byggnader och mark", "Byggnader")

fun hittaByggnadensVarde(text: String): Belopp? {
    for (rubrik in rubriker) {
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
    // Hantera tusentalsavgränsare
    var normalizedText = text
    
    // Ersätt olika typer av mellanslag med vanliga mellanslag
    normalizedText = normalizedText.replace("\u00A0", " ")  // hårt mellanslag
    normalizedText = normalizedText.replace("\u2009", " ")  // smalt mellanslag
    
    // Ersätt punkter med mellanslag (men vi måste vara försiktiga)
    // För att hantera "1.234,5" korrekt, ersätter vi punkter med mellanslag först
    // men vi vill behålla komman som decimaltecken
    val hasComma = normalizedText.contains(",")
    
    if (hasComma) {
        // Om det finns en komma, så är detta ett decimaltal
        // Vi ersätter punkter med mellanslag för att få rätt struktur
        // men vi behåller komman som decimaltecken
        normalizedText = normalizedText.replace(".", " ")
    } else {
        // Om det inte finns komma, ersätt punkter med mellanslag
        normalizedText = normalizedText.replace(".", " ")
    }
    
    // Ta bort alla mellanslag för att få ett rent tal
    val cleanText = normalizedText.replace(" ", "")
    
    // Om vi har en komma i originaltexten, så är det ett decimaltal
    val tal = if (hasComma) {
        // Ersätt komman med punkt för att få rätt decimalformat
        val decimalText = cleanText.replace(",", ".")
        decimalText.toBigDecimalOrNull()
    } else {
        cleanText.toBigDecimalOrNull()
    }
    
    if (tal == null) return null
    
    val kronor = if (iTusentals) {
        tal.multiply(java.math.BigDecimal(1000)).toLong()
    } else {
        tal.toLong()
    }
    return kronor
}
