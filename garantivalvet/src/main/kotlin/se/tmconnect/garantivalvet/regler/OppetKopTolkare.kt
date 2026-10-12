package se.tmconnect.garantivalvet.regler

private val OPPET_KOP_FORE = Regex("""[öo]ppet\s+k[öo]p[^0-9\n]{0,15}?(\d{1,4})\s*(?:dagar|dgr|dag)""")
private val OPPET_KOP_EFTER = Regex("""(\d{1,4})\s*-?\s*dagars?\s+[öo]ppet\s+k[öo]p""")

/**
 * Antal dagar öppet köp som kvittot anger, eller `null`: "Öppet köp i 30 dagar", "60 dagars öppet köp",
 * "Öppet köp: 14 dgr". Utan antal, eller över ett år, ges inget.
 */
fun tolkaOppetKop(text: String): Int? {
    val lag = text.lowercase()
    val traffar = (OPPET_KOP_FORE.findAll(lag) + OPPET_KOP_EFTER.findAll(lag))
        .sortedBy { it.range.first }
    return traffar.firstNotNullOfOrNull { giltigaOppetKopDagar(it.groupValues[1].toIntOrNull()) }
}
