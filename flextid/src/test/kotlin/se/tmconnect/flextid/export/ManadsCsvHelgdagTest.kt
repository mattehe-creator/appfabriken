package se.tmconnect.flextid.export

import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.DagTyp

class ManadsCsvHelgdagTest {
    private val rubriker =
        listOf("datum", "start", "slut", "rast", "arbetad", "utfall", "typ", "helgdag")

    private fun dataradFor(csv: String, datum: String): String =
        csv.lineSequence()
            .first { it.startsWith(datum) }

    @Test
    fun langfredagenVisasSomHelgdagEtikettOchNamn() {
        val dagar =
            listOf(
                Dag(datum = LocalDate.of(2026, 4, 3), typ = DagTyp.HELGDAG),
            )
        val texter =
            ManadsCsvTexter(
                rubriker = rubriker,
                typEtiketter = mapOf(DagTyp.HELGDAG to "Helgdag"),
            )
        val csv =
            byggManadsCsv(
                dagar = dagar,
                installning = Installning(),
                arManad = YearMonth.of(2026, 4),
                texter = texter,
                locale = Locale.forLanguageTag("sv-SE"),
            )
        val rad = dataradFor(csv, "2026-04-03")
        assertTrue(rad.endsWith(";Helgdag;Långfredagen"))
    }

    @Test
    fun arbetstagUtanEtikettOchUtanHelgdag() {
        val dagar =
            listOf(
                Dag(datum = LocalDate.of(2026, 4, 7), typ = DagTyp.ARBETE),
            )
        val csv =
            byggManadsCsv(
                dagar = dagar,
                installning = Installning(),
                arManad = YearMonth.of(2026, 4),
                texter = ManadsCsvTexter(rubriker = rubriker),
                locale = Locale.forLanguageTag("sv-SE"),
            )
        val rad = dataradFor(csv, "2026-04-07")
        assertTrue(rad.endsWith(";ARBETE;"))
    }

    @Test
    fun helgdagNamnKanOverstyras() {
        val dagar =
            listOf(
                Dag(datum = LocalDate.of(2026, 12, 24)),
            )
        val texter =
            ManadsCsvTexter(
                rubriker = rubriker,
                helgdagNamn = { "X" },
            )
        val csv =
            byggManadsCsv(
                dagar = dagar,
                installning = Installning(),
                arManad = YearMonth.of(2026, 12),
                texter = texter,
                locale = Locale.forLanguageTag("sv-SE"),
            )
        val rad = dataradFor(csv, "2026-12-24")
        assertTrue(rad.endsWith(";ARBETE;X"))
    }
}
