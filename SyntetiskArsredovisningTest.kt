package se.tmconnect.styrelserummet.underlag

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.tolking.Tolkare
import java.io.ByteArrayOutputStream

class SyntetiskArsredovisningTest {

    @Test
    fun arsredovisningBalansTvaKolumner() {
        // Skapa PDF med balansräkning i två kolumner
        val arPdfBytes = skapaArPdfMedBalans()
        
        val arText = PdfLasare().lasSomText(arPdfBytes).getOrThrow()
        val planText = "Tak och stomme enligt underhållsplan."
        
        val tolkare = object : Tolkare {
            override fun foreslaKomponenter(
                underhallsplanUtdrag: String,
                sidor: List<Int>,
                filnamn: String,
            ) = Result.success(
                """
                {"komponenter":[
                  {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":7500000,"anskaffningsar":1975,"nyttjandeperiodAr":100,"kalla":"plan.pdf, sida 1","sakerhet":"HOG"},
                  {"namn":"Tak","grupp":"TAK","anskaffningsvarde":5000000,"anskaffningsar":1975,"nyttjandeperiodAr":50,"kalla":"plan.pdf, sida 1","sakerhet":"MEDEL"}
                ],"anmarkningar":[]}
                """.trimIndent(),
            )
        }

        val utkast = byggUtkast(planText, arText, Forening("Testföreningen BRF", 1975), tolkare, nuAr = 2025)

        Assert.assertEquals(12_500_000L, utkast.byggnadensVarde)
        Assert.assertTrue(utkast.avstamning!!.stammer)
        Assert.assertEquals(12_500_000L, utkast.avstamning!!.summa)
        Assert.assertEquals(2, utkast.komponenter.size)
        Assert.assertEquals(1975, utkast.arsredovisning.byggnadsar)
    }

    private fun skapaArPdfMedBalans(): ByteArray {
        PDDocument().use { doc ->
            val page = PDPage()
            doc.addPage(page)

            PDPageContentStream(doc, page).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)

                cs.showText("Balansräkning")
                cs.newLine()
                cs.showText("Byggnader och mark")
                cs.newLine()
                cs.showText("2025 2024")
                cs.newLine()
                cs.showText("12 500 000 11 800 000")
                cs.newLine()
                cs.showText("Byggnadsår 1975")
                cs.newLine()

                cs.endText()
            }

            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
