package se.tmconnect.styrelserummet.underlag

data class ArsredovisningUtdrag(
    val byggnadensVarde: Long?,
    val ackumuleradeAvskrivningar: Long?,
    val byggnadsar: Int?,
    val avskrivningsprincip: String?,
)

private val ackRegex = Regex(
    """Ackumulerade avskrivningar[^\d]*([\d\s]+(?:,\d+)?)""",
    RegexOption.IGNORE_CASE,
)
private val byggnadsarRegex = Regex("""Byggnadsår[^\d]{0,20}(\d{4})""", RegexOption.IGNORE_CASE)
private val principRegex = Regex(
    """Avskrivningsprincip[^\n]*\n([^\n]+)""",
    RegexOption.IGNORE_CASE,
)

fun lasArsredovisning(text: String): ArsredovisningUtdrag {
    val byggnad = hittaByggnadensVarde(text)?.kronor
    val ack = ackRegex.find(text)?.groupValues?.get(1)?.let { parseBelopp(it, textHarTusen(text)) }
    val byggnadsar = byggnadsarRegex.find(text)?.groupValues?.get(1)?.toIntOrNull()
    val princip = principRegex.find(text)?.groupValues?.get(1)?.trim()
    return ArsredovisningUtdrag(byggnad, ack, byggnadsar, princip)
}

private fun textHarTusen(text: String): Boolean =
    text.contains("tusen kr", ignoreCase = true) || text.contains("Tkr", ignoreCase = true)
