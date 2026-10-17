package se.tmconnect.husloggen.export

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.karna.export.PdfExportResult
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusHistorikPdfTest {
    private fun testData() =
        byggHusHistorik(
            poster = listOf(
                Post(
                    datum = LocalDate.of(2025, 1, 1),
                    vad = "Tak",
                    typ = PostTyp.OMBYGGNAD,
                    beloppOre = 1_000_000L,
                ),
            ),
            forsaljningsar = 2026,
            localeLand = "SE",
            typEtikett = { "Ombyggnad" },
            datumEtikett = { it.datum.toString() },
            beloppEtikett = { "10 000 kr" },
            forbattringEtikett = { "Ja" },
            gransJa = "Minst 5000",
            gransNej = "Under 5000",
        )

    private fun testTexter() = HusHistorikTexter(
        rubrik = "Husets historik",
        kolumnDatum = "Datum",
        kolumnBeskrivning = "Beskrivning",
        kolumnTyp = "Typ",
        kolumnBelopp = "Belopp",
        kolumnForbattring = "Förbättring",
        arSummaRubrik = { "År $it" },
        typSummaRad = { t, b -> "$t: $b" },
        totalt = "Totalt",
        gransJa = "Ja",
        gransNej = "Nej",
        skattRubrik = "Information",
        skattText = "Det här är information, inte skatteråd.",
        kallorRubrik = "Källor",
        kallorText = "Skatteverket",
    )

    @Test
    fun skrivningGerResultatUtanKrasch() {
        val ut = ByteArrayOutputStream()
        val result = skrivHusHistorikTillPdf(testData(), testTexter(), Locale.forLanguageTag("sv-SE"), ut)
        assertTrue(result is PdfExportResult.Ok || result is PdfExportResult.Fel)
        if (result is PdfExportResult.Ok) {
            assertTrue(String(ut.toByteArray(), 0, 4) == "%PDF")
        }
    }

    @Test
    fun returnerarFelVidSkrivfel() {
        val stream = object : OutputStream() {
            override fun write(b: Int) {
                throw IOException("disk full")
            }
        }
        val result = skrivHusHistorikTillPdf(testData(), testTexter(), Locale.forLanguageTag("sv-SE"), stream)
        assertTrue(result is PdfExportResult.Fel)
    }
}
