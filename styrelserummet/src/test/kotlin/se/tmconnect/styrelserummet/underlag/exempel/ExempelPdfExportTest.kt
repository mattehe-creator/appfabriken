package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test
import se.tmconnect.styrelserummet.pdf.PdfLasare

class ExempelPdfExportTest {

    @Test
    fun exportPdfInnehallerKarninnehall() {
        val planBytes = exempelUnderhallsplanPdf()
        Assert.assertTrue(planBytes.isNotEmpty())

        val planText = PdfLasare().lasSomText(planBytes).getOrThrow()
        Assert.assertTrue(planText.contains("Kostnad"))
        Assert.assertTrue(
            planText.contains("27 000 000") || planText.contains("5 000 000"),
        )

        val arText = PdfLasare().lasSomText(exempelArsredovisningPdf()).getOrThrow()
        Assert.assertTrue(arText.contains("Byggnader och mark"))
    }
}
