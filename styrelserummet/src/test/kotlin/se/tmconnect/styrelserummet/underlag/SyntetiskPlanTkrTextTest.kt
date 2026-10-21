package se.tmconnect.styrelserummet.underlag

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.pdf.PdfLasare
import se.tmconnect.styrelserummet.tolking.Tolkare
import java.io.ByteArrayOutputStream

class SyntetiskPlanTkrTextTest {
    private fun fakeTolkare(json: String) = object : Tolkare {
        override fun foreslaKomponenter(
            underhallsplanUtdrag: String,
            sidor: List<Int>,
            filnamn: String,
        ) = Result.success(json)
    }

    @Test
    fun planLopandeTextTkrGerUtkast() {
        val pdfText = "Takbyte planeras 2030, uppskattad kostnad 500 tkr. Stomrenovering 2040, 1 200 tkr."
        val pdfBytes = pdfMedText(listOf(pdfText))
        val lasare = PdfLasare()
        val extractedText = lasare.lasSomText(pdfBytes).getOrThrow()
        assertTrue(extractedText.contains("500 tkr"))
        assertTrue(extractedText.contains("1 200 tkr"))

        val json = """
            {"komponenter":[
              {"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2030,"nyttjandeperiodAr":50,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"UPPSKATTNING"},
              {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":1200000,"anskaffningsar":2040,"nyttjandeperiodAr":100,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"UPPSKATTNING"}
            ],"anmarkningar":[]}
        """.trimIndent()
        val tolkare = fakeTolkare(json)
        val ar = "Byggnader och mark 1 700 tkr"

        val utkast = byggUtkast(
            extractedText,
            ar,
            Forening("Testföreningen", 1975),
            tolkare,
            nuAr = 2025,
        )

        assertEquals(2, utkast.komponenter.size)
        assertNotNull(utkast.avstamning)
        assertTrue(utkast.avstamning!!.stammer)
        assertEquals(1_700_000L, utkast.avstamning!!.summa)

        val tak = utkast.komponenter.single { it.grupp == Komponentgrupp.TAK }
        assertEquals(Sakerhet.UPPSKATTNING, tak.sakerhet)

        val stomme = utkast.komponenter.single { it.grupp == Komponentgrupp.STOMME }
        assertEquals(Sakerhet.UPPSKATTNING, stomme.sakerhet)

        assertTrue(utkast.anmarkningar.none { it.contains("differens") })
    }

    private fun pdfMedText(sidor: List<String>): ByteArray {
        PDDocument().use { doc ->
            sidor.forEach { text ->
                val page = PDPage()
                doc.addPage(page)
                if (text.isNotBlank()) {
                    PDPageContentStream(doc, page).use { cs ->
                        cs.beginText()
                        cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                        cs.newLineAtOffset(50f, 700f)
                        cs.showText(text)
                        cs.endText()
                    }
                }
            }
            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
