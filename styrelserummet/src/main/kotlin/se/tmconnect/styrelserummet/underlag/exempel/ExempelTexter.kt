package se.tmconnect.styrelserummet.underlag.exempel

import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.underlag.gissaGrupp

fun exempelKomponenterFranPlan(): List<Komponent> {
    return EXEMPEL_ATGARDER
        .filter { it.kostnadKronor != null }
        .map { atgard ->
            val grupp = gissaGrupp(atgard.namn) ?: when (atgard.namn) {
                "Entréparti" -> Komponentgrupp.FONSTER_DORRAR
                "Brandlarm" -> Komponentgrupp.OVRIGT
                else -> throw IllegalArgumentException("Okänd åtgärd: ${atgard.namn}")
            }
            
            val nyttjandeperiodAr = when (grupp) {
                Komponentgrupp.STOMME -> 100
                Komponentgrupp.TAK -> 50
                Komponentgrupp.FASAD -> 40
                Komponentgrupp.FONSTER_DORRAR -> 40
                Komponentgrupp.STAMMAR -> 50
                Komponentgrupp.EL -> 50
                Komponentgrupp.VARME -> 80
                Komponentgrupp.VENTILATION -> 20
                Komponentgrupp.HISS -> 25
                Komponentgrupp.TVATTSTUGA -> 30
                Komponentgrupp.MARK_OCH_GARAGE -> 0
                Komponentgrupp.OVRIGT -> 30
            }
            
            Komponent(
                namn = atgard.namn,
                grupp = grupp,
                anskaffningsvarde = atgard.kostnadKronor!!,
                anskaffningsar = atgard.ar,
                nyttjandeperiodAr = nyttjandeperiodAr,
                sakerhet = Sakerhet.HOG,
                kalla = "exempel-underhallsplan"
            )
        }
}

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
            appendLine(
                "Verksamheten bedrivs i föreningens fastighet på Exempelvägen 1 i Teststad.",
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
