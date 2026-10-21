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

class SyntetiskPlanSaknadKostnadTest {

    @Test
    fun planSaknadKostnadGerUppskattning() {
        val pdfBytes = skapaPdfMedAtgarder(
            listOf(
                listOf("Stomme", "1975", "6 300 000"),
                listOf("Fasad", "2035", "–"),
            ),
        )
        val planText = PdfLasare().lasSomText(pdfBytes).getOrThrow()

        val json = """
            {"komponenter":[
              {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":6300000,"anskaffningsar":1975,"nyttjandeperiodAr":100,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"HOG"},
              {"namn":"Fasad","grupp":"FASAD","anskaffningsvarde":null,"anskaffningsar":null,"nyttjandeperiodAr":40,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"LAG"}
            ],"anmarkningar":["Kostnad saknas i planen."]}
        """.trimIndent()

        val tolkare = object : Tolkare {
            override fun foreslaKomponenter(
                underhallsplanUtdrag: String,
                sidor: List<Int>,
                filnamn: String,
            ) = Result.success(json)
        }

        val ar = "Byggnader 10 000 000"
        val utkast = byggUtkast(planText, ar, Forening("Testföreningen", 1975), tolkare, nuAr = 2025)

        assertEquals(2, utkast.komponenter.size)

        val fasad = utkast.komponenter.single { it.grupp == Komponentgrupp.FASAD }
        assertEquals(Sakerhet.UPPSKATTNING, fasad.sakerhet)
        assertEquals(3_700_000L, fasad.anskaffningsvarde)

        assertNotNull(utkast.avstamning)
        assertTrue(utkast.avstamning!!.stammer)
        assertEquals(10_000_000L, utkast.avstamning!!.summa)

        assertTrue(
            utkast.anmarkningar.any {
                it.contains("uppskattat anskaffningsvärde", ignoreCase = true) && it.contains("Fasad")
            },
        )
    }

    private fun skapaPdfMedAtgarder(rader: List<List<String>>): ByteArray {
        PDDocument().use { doc ->
            val page = PDPage()
            doc.addPage(page)

            PDPageContentStream(doc, page).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)

                cs.showText("Åtgärd   År   Kostnad")
                cs.newLine()

                rader.forEach { rad ->
                    cs.showText(rad.joinToString("   "))
                    cs.newLine()
                }

                cs.endText()
            }

            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
