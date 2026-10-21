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

class SyntetiskPlanTvaSidorTest {

    @Test
    fun planTvaSidorFortsattning() {
        val pdfBytes = skapaPdfMedTvaSidor()

        val planText = PdfLasare().lasSomText(pdfBytes).getOrThrow()

        val registreradeSidorAnrop = mutableListOf<List<Int>>()
        val tolkare = object : Tolkare {
            override fun foreslaKomponenter(
                underhallsplanUtdrag: String,
                sidor: List<Int>,
                filnamn: String,
            ): Result<String> {
                registreradeSidorAnrop.add(sidor)

                return if (sidor.size == 1) {
                    if (sidor[0] == 1) {
                        Result.success(
                            """
                            {"komponenter":[
                              {"namn":"Ventilation","grupp":"VENTILATION","anskaffningsvarde":800000,"anskaffningsar":2025,"nyttjandeperiodAr":15,"kalla":"plan.pdf, sida 1","sakerhet":"LAG"}
                            ],"anmarkningar":[]}
                            """.trimIndent(),
                        )
                    } else {
                        Result.success(
                            """
                            {"komponenter":[
                              {"namn":"Hiss","grupp":"HISS","anskaffningsvarde":200000,"anskaffningsar":2025,"nyttjandeperiodAr":20,"kalla":"plan.pdf, sida 2","sakerhet":"LAG"}
                            ],"anmarkningar":[]}
                            """.trimIndent(),
                        )
                    }
                } else {
                    Result.success(
                        """
                        {"komponenter":[
                          {"namn":"Ventilation","grupp":"VENTILATION","anskaffningsvarde":800000,"anskaffningsar":2025,"nyttjandeperiodAr":15,"kalla":"plan.pdf, sida 1","sakerhet":"LAG"},
                          {"namn":"Hiss","grupp":"HISS","anskaffningsvarde":200000,"anskaffningsar":2025,"nyttjandeperiodAr":20,"kalla":"plan.pdf, sida 2","sakerhet":"LAG"}
                        ],"anmarkningar":[]}
                        """.trimIndent(),
                    )
                }
            }
        }

        val arText = "Balansräkning\nByggnader och mark 1 000 000"

        val utkast = byggUtkast(planText, arText, Forening("Testföreningen BRF", 1975), tolkare, nuAr = 2025)

        Assert.assertTrue(registreradeSidorAnrop.flatten().contains(1))
        Assert.assertTrue(registreradeSidorAnrop.flatten().contains(2))

        Assert.assertEquals(2, utkast.komponenter.size)

        val ventilation = utkast.komponenter.find { it.namn == "Ventilation" }
        val hiss = utkast.komponenter.find { it.namn == "Hiss" }

        Assert.assertNotNull(ventilation)
        Assert.assertNotNull(hiss)
        Assert.assertEquals(800000, ventilation!!.anskaffningsvarde)
        Assert.assertEquals(200000, hiss!!.anskaffningsvarde)

        Assert.assertTrue(utkast.avstamning!!.stammer)

        Assert.assertEquals(Sakerhet.LAG, ventilation.sakerhet)
        Assert.assertEquals(Sakerhet.LAG, hiss.sakerhet)

        val anmarkningar = anmarkningarFor(utkast, listOf(), 2025)
        Assert.assertTrue(anmarkningar.any { it.contains("låg säkerhet") && it.contains("Ventilation") })
        Assert.assertTrue(anmarkningar.any { it.contains("låg säkerhet") && it.contains("Hiss") })
    }

    private fun skapaPdfMedTvaSidor(): ByteArray {
        PDDocument().use { doc ->
            val page1 = PDPage()
            doc.addPage(page1)
            PDPageContentStream(doc, page1).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText("Ventilation 800 000")
                cs.endText()
            }

            val page2 = PDPage()
            doc.addPage(page2)
            PDPageContentStream(doc, page2).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText("Hiss 200 000")
                cs.endText()
            }

            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
