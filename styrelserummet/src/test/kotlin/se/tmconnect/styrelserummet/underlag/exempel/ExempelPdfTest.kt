package se.tmconnect.styrelserummet.underlag.exempel

import org.apache.pdfbox.Loader
import org.junit.Assert
import org.junit.Test
import se.tmconnect.styrelserummet.pdf.PdfLasare

class ExempelPdfTest {

    @Test
    fun skrivPdfKanLasasTillbaka() {
        val sidor = underhallsplanSidor()
        val bytes = skrivPdf(sidor)

        val text = PdfLasare().lasSomText(bytes).getOrThrow()
        Assert.assertTrue(text.contains("Brf Exempelgården"))
        Assert.assertTrue(text.contains("Yttertak"))
        Assert.assertTrue(text.contains("Balkonger"))

        Loader.loadPDF(bytes).use { doc ->
            Assert.assertEquals(sidor.size, doc.numberOfPages)
        }
    }

    @Test
    fun skrivPdfBryterLangRad() {
        val rad = "a".repeat(120)
        val bytes = skrivPdf(listOf(rad))

        val text = PdfLasare().lasSomText(bytes).getOrThrow()
        val forstaDel = rad.substring(0, 90)
        val andraDel = rad.substring(90)
        Assert.assertTrue(text.contains(forstaDel))
        Assert.assertTrue(text.contains(andraDel))
    }
}
