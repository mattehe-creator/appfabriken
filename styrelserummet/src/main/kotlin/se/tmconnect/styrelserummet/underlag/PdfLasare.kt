package se.tmconnect.styrelserummet.underlag

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper

data class PdfSida(val nummer: Int, val text: String)

class PdfLasare {
    fun las(bytes: ByteArray): List<PdfSida> {
        Loader.loadPDF(bytes).use { doc ->
            val stripper = PDFTextStripper()
            return (1..doc.numberOfPages).map { nr ->
                stripper.startPage = nr
                stripper.endPage = nr
                val text = stripper.getText(doc).trim()
                PdfSida(nummer = nr, text = text)
            }
        }
    }

    fun harLasbarText(sidor: List<PdfSida>): Boolean =
        sidor.any { it.text.isNotBlank() }
}
