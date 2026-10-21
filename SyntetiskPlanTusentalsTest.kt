package se.tmconnect.styrelserummet.underlag

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.pdf.PdfLasare
import se.tmconnect.styrelserummet.tolking.Tolkare
import java.io.ByteArrayOutputStream

class SyntetiskPlanTusentalsTest {

    @Test
    fun planHardMellanslagOchPunkt() {
        val pdfBytes = skapaPdfMedText(
            "Fönsterbyte 2025 1.250.000 kr"
        )

        val lasare = PdfLasare()
        val planText = lasare.lasSomText(pdfBytes).getOrThrow()

        // PDFBox bevarar inte alltid hårda mellanslag, så vi ersätter manuellt
        // för att simulera att texten innehåller korrekt formatering
        val planTextMedHardMellanslag = planText.replace("1.250.000 kr", "1.250.000\u00A0kr")

        val tolkare = object : Tolkare {
            override fun foreslaKomponenter(
                underhallsplanUtdrag: String,
                sidor: List<Int>,
                filnamn: String,
            ) = Result.success(
                """
                {"komponenter":[
                  {"namn":"Fönster","grupp":"FONSTER_DORRAR","anskaffningsvarde":1250000,"anskaffningsar":2025,"nyttjandeperiodAr":40,"kalla":"plan.pdf, sida 1","sakerhet":"MEDEL"}
                ],"anmarkningar":[]}
                """.trimIndent(),
            )
        }

        val arText = "Byggnader och mark 1 250 000"

        val utkast = byggUtkast(planTextMedHardMellanslag, arText, Forening("Testföreningen", 1975), tolkare, nuAr = 2025)

        Assert.assertEquals(1, utkast.komponenter.size)
        Assert.assertTrue(utkast.avstamning!!.stammer)
        Assert.assertEquals(1_250_000L, utkast.avstamning!!.summa)

        val fönster = utkast.komponenter.single()
        Assert.assertEquals(1_250_000L, fönster.ananskaffningsvarde)
        Assert.assertEquals(Sakerhet.MEDEL, fönster.sakerhet)

        val anmarkningar = anmarkningarFor(utkast, listOf(), 2025)
        Assert.assertFalse(anmarkningar.any { it.contains("LAG") || it.contains("UPPSKATTNING") })
    }

    private fun skapaPdfMedText(text: String): ByteArray {
        PDDocument().use { doc ->
            val page = PDPage()
            doc.addPage(page)

            PDPageContentStream(doc, page).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText(text)
                cs.endText()
            }

            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
