package se.tmconnect.styrelserummet.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream

class PdfLasareTest {
    @Test
    fun lasPerSidaReturnerarTextPerSida() {
        val bytes = pdfMedText(listOf("Sida ett", "Sida två"))
        val lasare = PdfLasare()
        val sidor = lasare.lasPerSida(bytes).getOrThrow()
        assertEquals(2, sidor.size)
        assertTrue(sidor[0].contains("Sida ett"))
        assertTrue(sidor[1].contains("Sida två"))
    }

    @Test
    fun skannadPdfUtanTextGerIngenLasbarText() {
        val bytes = pdfMedText(listOf("", ""))
        val lasare = PdfLasare()
        assertFalse(lasare.harLasbarText(bytes))
        val result = lasare.lasSomText(bytes)
        assertTrue(result.isFailure)
        assertEquals(INGEN_LASBAR_TEXT, (result.exceptionOrNull() as IngenLasbarTextException).meddelande)
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
