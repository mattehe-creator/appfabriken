package se.tmconnect.styrelserummet.underlag

data class Belopp(
    val kronor: Long,
    val iTusentals: Boolean,
)

private val rubriker = listOf("Byggnader och mark", "Byggnader")

private val arRadRegex = Regex("""^\s*\d{4}\s+\d{4}\s*$""")

private val forstaBeloppPaRadRegex = Regex(
    """(\(\s*[\d][\d\s]*(?:,\d+)?\s*\)|-\s*(?:\d{1,3}(?: \d{3})+|[\d][\d\s]*(?:,\d+)?)|\d{1,3}(?: \d{3})+(?:,\d+)?)""",
)

private val enbartTvaArtalRegex = Regex("""^\s*\d{4}\s+\d{4}\s*$""")

fun hittaByggnadensVarde(text: String): Belopp? {
    for (rubrik in rubriker) {
        // Först sök efter tkr eller mnkr
        val regexTkrMnkr = Regex(
            """$rubrik[^\d\n(-]*(\(\s*[\d][\d\s]*(?:,\d+)?\s*\)|-\s*[\d][\d\s]*(?:,\d+)?|[\d][\d\s]*(?:,\d+)?)[\s]*(tkr|mnkr)""",
            RegexOption.IGNORE_CASE,
        )
        val matchTkrMnkr = regexTkrMnkr.find(text)
        
        if (matchTkrMnkr != null) {
            val talText = matchTkrMnkr.groupValues[1].trim()
            val enhet = matchTkrMnkr.groupValues[2].lowercase()

            val iTusen = enhet == "tkr"
            val finalKronor = if (enhet == "mnkr") {
                val (talUtanTecken, negativt) = avmarkeraNegativt(talText)
                val normaliserat = talUtanTecken.replace(" ", "").replace(",", ".")
                val tal = normaliserat.toBigDecimalOrNull() ?: continue
                var kronor = tal.multiply(java.math.BigDecimal(1_000_000)).toLong()
                if (negativt) kronor = -kronor
                kronor
            } else {
                parseBelopp(talText, iTusentals = true) ?: continue
            }

            return Belopp(finalKronor, iTusen)
        }

        val lines = text.lines()
        val rubrikLineIndex = lines.indexOfFirst { line ->
            Regex("(?i)${Regex.escape(rubrik)}").containsMatchIn(line)
        }
        if (rubrikLineIndex >= 0) {
            val rubrikLine = lines[rubrikLineIndex]
            val efterRubrik = rubrikLine.replace(
                Regex("(?i)^.*?${Regex.escape(rubrik)}\\s*"),
                "",
            ).trim()
            if (efterRubrik.isNotEmpty() && enbartTvaArtalRegex.matches(efterRubrik)) {
                return null
            }
            if (rubrikLineIndex + 1 < lines.size && arRadRegex.matches(lines[rubrikLineIndex + 1])) {
                if (rubrikLineIndex + 2 < lines.size) {
                    val beloppsRad = lines[rubrikLineIndex + 2]
                    val talMatch = forstaBeloppPaRadRegex.find(beloppsRad) ?: continue
                    val talText = talMatch.groupValues[1].trim()
                    val iTusen = beloppsRad.contains("tusen", ignoreCase = true) ||
                        beloppsRad.contains("tkr", ignoreCase = true)
                    val kronor = parseBelopp(talText, iTusen) ?: continue
                    return Belopp(kronor, iTusen)
                }
                continue
            }
        }

        // Fallback till befintlig logik för "tusen kronor"
        val regex = Regex(
            """$rubrik[^\d\n(-]*(\(\s*[\d][\d\s]*(?:,\d+)?\s*\)|-\s*[\d][\d\s]*(?:,\d+)?|[\d][\d\s]*(?:,\d+)?)""",
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

private fun avmarkeraNegativt(text: String): Pair<String, Boolean> {
    var raw = text.trim()
    var negativt = false
    if (raw.startsWith("(") && raw.endsWith(")")) {
        negativt = true
        raw = raw.substring(1, raw.length - 1).trim()
    } else if (raw.startsWith("-")) {
        negativt = true
        raw = raw.substring(1).trim()
    }
    return raw to negativt
}

internal fun parseBelopp(text: String, iTusentals: Boolean): Long? {
    val (talText, negativt) = avmarkeraNegativt(text)
    val normalizedText = talText
        .replace("\u00A0", " ")
        .replace("\u2009", " ")
        .replace(".", " ")
    val cleanText = normalizedText.replace(" ", "")
    val hasComma = talText.contains(',')
    val tal = if (hasComma) {
        cleanText.replace(",", ".").toBigDecimalOrNull()
    } else {
        cleanText.toBigDecimalOrNull()
    } ?: return null
    val kronor = if (iTusentals) {
        tal.multiply(java.math.BigDecimal(1000)).toLong()
    } else {
        tal.toLong()
    }
    return if (negativt) -kronor else kronor
}
