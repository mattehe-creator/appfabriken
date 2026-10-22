package se.tmconnect.flextid.regler

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class ArsOversiktCsvTest {
    private val locale = Locale.forLanguageTag("sv-SE")
    private val rubriker = listOf("Månad", "Arbetat", "Utfall", "Saldo")
    private val manadsnamn =
        listOf(
            "januari",
            "februari",
            "mars",
            "april",
            "maj",
            "juni",
            "juli",
            "augusti",
            "september",
            "oktober",
            "november",
            "december",
        )

    @Test
    fun exempelFranUppgiften() {
        val rader =
            listOf(
                ManadsRad(1, 510, 30, 150),
                ManadsRad(3, 420, -60, 90),
            )
        val forvantat =
            "Månad;Arbetat;Utfall;Saldo\n" +
                "januari;8:30;+0:30;+2:30\n" +
                "mars;7:00;-1:00;+1:30\n"
        assertEquals(forvantat, arsOversiktCsv(rader, rubriker, manadsnamn, locale))
    }

    @Test(expected = IllegalArgumentException::class)
    fun felAntalRubriker() {
        arsOversiktCsv(emptyList(), listOf("a", "b", "c"), manadsnamn, locale)
    }

    @Test(expected = IllegalArgumentException::class)
    fun felAntalManadsnamn() {
        arsOversiktCsv(emptyList(), rubriker, manadsnamn.take(11), locale)
    }
}
