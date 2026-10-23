package se.tmconnect.styrelserummet.underlag.exempel

fun underhallsplanSidor(): List<String> {
    val sida1 =
        buildString {
            appendLine(FORENING_NAMN)
            appendLine(ORGANISATIONSNUMMER)
            appendLine(ADRESS)
            appendLine("Byggnadsår: $BYGGNADSAR")
            appendLine()
            append(
                "Underhållsplanen är påhittad och avsedd endast som demoexempel i appen.",
            )
        }

    val tabellRader =
        buildString {
            appendLine("Åtgärd   År   Kostnad")
            EXEMPEL_ATGARDER.forEach { atgard ->
                val kostnadText =
                    atgard.kostnadKronor?.let { formatKronor(it) } ?: "–"
                appendLine("${atgard.namn}   ${atgard.ar}   $kostnadText")
            }
        }

    val summaKronor = summaAtgarderMedKostnad()
    val summaTkr = summaKronor / 1_000
    val sida3 =
        buildString {
            appendLine("Sammanfattning")
            appendLine()
            append(
                "Total planerad investering för åtgärder med angiven kostnad: " +
                    "${formatKronor(summaTkr)} tkr.",
            )
        }

    return listOf(sida1, tabellRader, sida3)
}

fun arsredovisningSidor(): List<String> {
    val byggnaderInnevarande = formatKronor(BYGGNADENS_VARDE)
    val sida =
        buildString {
            appendLine("Förvaltningsberättelse")
            appendLine()
            appendLine(
                "Styrelsen i $FORENING_NAMN avlägger härmed årsredovisning " +
                    "för räkenskapsåret $RAKENSKAPSAR.",
            )
            appendLine()
            appendLine("Balansräkning")
            appendLine("Byggnader och mark")
            appendLine("$RAKENSKAPSAR ${RAKENSKAPSAR - 1}")
            appendLine("$byggnaderInnevarande 26 500 000")
        }
    return listOf(sida)
}

private fun formatKronor(kronor: Long): String {
    val text = kronor.toString()
    return text
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
}
