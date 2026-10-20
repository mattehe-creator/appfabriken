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
    // Ersätt olika typer av mellanslag med vanliga mellanslag
    val normalizedText = text.replace("\u00A0", " ")  // hårt mellanslag
        .replace("\u2009", " ")  // smalt mellanslag
        .replace(".", " ")       // punkt till mellanslag
    
    // Hantera decimaltecken (komma) och ta bort eventuella mellanslag före komman
    val decimalText = normalizedText.replace(" ", "").replace(",", ".")
    
    val tal = decimalText.toBigDecimalOrNull() ?: return null
    
    val kronor = if (iTusentals) {
        tal.multiply(java.math.BigDecimal(1000)).toLong()
    } else {
        tal.toLong()
    }
    return kronor
}
