package se.tmconnect.garantivalvet.regler

private val RAD_MED_PRIS = Regex(
    """^(.*?\p{L}.*?)\s+(?:\d{1,3}(?:[ .]\d{3})*[.,]\d{2})\s*(?:kr|sek|:-)?\s*$""",
    RegexOption.IGNORE_CASE,
)
private val ANTAL_FORE = Regex("""^\d+\s*[x*×]\s*""", RegexOption.IGNORE_CASE)
private val ANTAL_FORE_ORD = Regex("""^\d+\s+(?=\p{L})""")
private val ANTAL_EFTER = Regex("""\s+\d+\s*(?:st|x|pcs)\.?$""", RegexOption.IGNORE_CASE)
private val VAROR_UTESLUT = Regex(
    """^(summa|total|totalt|att betala|moms|mva|vat|rabatt|kontant|kort|visa|mastercard|debit|credit|växel|tillbaka|netto|brutto|subtotal|delsumma|belopp|betalat|avrundning|swish|öresavrundning)\b""",
    RegexOption.IGNORE_CASE,
)

private const val MAX_VARUNAMN_TECKEN = 40
private const val MAX_VAROR = 6

/**
 * Förslag på vad som köpts: kvittorader som slutar på ett belopp med ören ("3 Banh Mi Nem Nuong 99,00").
 * Antal och belopp tas bort och versaler görs om till vanlig skrivning. Rader om summa, moms, kortbetalning och
 * liknande hoppas över. Det är bara förslag; användaren väljer själv.
 */
fun hittaVarorader(text: String): List<String> =
    text.lines()
        .mapNotNull { rad -> RAD_MED_PRIS.find(rad.trim())?.groupValues?.get(1) }
        .map { rensaVarunamn(it) }
        .filter { namn ->
            namn.count { it.isLetter() } >= 3 &&
                namn.length <= MAX_VARUNAMN_TECKEN &&
                !VAROR_UTESLUT.containsMatchIn(namn)
        }
        .distinct()
        .take(MAX_VAROR)

private fun rensaVarunamn(rad: String): String {
    var namn = rad.trim()
    namn = ANTAL_FORE.replace(namn, "")
    namn = ANTAL_FORE_ORD.replace(namn, "")
    namn = ANTAL_EFTER.replace(namn, "")
    namn = namn.trim(' ', '.', ',', ':', '-', '*')
    if (namn.any { it.isLetter() } && namn.none { it.isLowerCase() }) {
        namn = namn.lowercase().replaceFirstChar { it.uppercase() }
    }
    return namn
}
