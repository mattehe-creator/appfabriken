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

class SyntetiskPlanTabellTest {

    @Test
    fun planTabellLayoutGerAvstamdUtkast() {
        val pdfBytes = skapaPdfMedTabell(
            listOf(
                listOf("Tak", "2030", "2 000 000"),
                listOf("Fasad", "2035", "1 500 000"),
            ),
        )

        val lasare = PdfLasare()
        val planText = lasare.lasSomText(pdfBytes).getOrThrow()

        val tolkare = object : Tolkare {
            override fun foreslaKomponenter(
                underhallsplanUtdrag: String,
                sidor: List<Int>,
                filnamn: String,
            ) = Result.success(
                """
                {"komponenter":[
                  {"namn":"Tak","grupp":"TAK","anskaffningsvarde":2000000,"anskaffningsar":2030,"nyttjandeperiodAr":50,"kalla":"plan.pdf, sida 1","sakerhet":"HOG"},
                  {"namn":"Fasad","grupp":"FASAD","anskaffningsvarde":1500000,"anskaffningsar":2035,"nyttjandeperiodAr":40,"kalla":"plan.pdf, sida 1","sakerhet":"MEDEL"}
                ],"anmarkningar":[]}
                """.trimIndent(),
            )
        }

        val arText = "Balansräkning\nByggnader och mark 3 500 000"

        val utkast = byggUtkast(planText, arText, Forening("Testföreningen BRF", 1975), tolkare, nuAr = 2025)

        Assert.assertEquals(2, utkast.komponenter.size)
        Assert.assertTrue(utkast.avstamning!!.stammer)

        val tak = utkast.komponenter.find { it.namn == "Tak" }
        val fasad = utkast.komponenter.find { it.namn == "Fasad" }

        Assert.assertNotNull(tak)
        Assert.assertNotNull(fasad)
        Assert.assertEquals(Sakerhet.HOG, tak!!.sakerhet)
        Assert.assertEquals(Sakerhet.MEDEL, fasad!!.sakerhet)

        val anmarkningar = anmarkningarFor(utkast, listOf(), 2025)
        Assert.assertFalse(anmarkningar.any { it.contains("LAG") || it.contains("UPPSKATTNING") })
    }

    private fun skapaPdfMedTabell(rader: List<List<String>>): ByteArray {
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
                    val text = rad.joinToString("   ")
                    cs.showText(text)
                    cs.newLine()
                }

                cs.endText()
            }

            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
