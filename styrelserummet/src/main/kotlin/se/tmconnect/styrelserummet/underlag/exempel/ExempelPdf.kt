package se.tmconnect.styrelserummet.underlag.exempel

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.io.ByteArrayOutputStream

private const val MAX_RADLANGD = 90

fun skrivPdf(sidor: List<String>): ByteArray {
    PDDocument().use { doc ->
        sidor.forEach { sidtext ->
            val page = PDPage(PDRectangle.A4)
            doc.addPage(page)

            PDPageContentStream(doc, page).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.setLeading(14.5f)
                cs.newLineAtOffset(50f, 700f)

                sidtext.split('\n').forEach { logiskRad ->
                    delaLogiskRad(logiskRad).forEach { utskrivenRad ->
                        cs.showText(utskrivenRad)
                        cs.newLine()
                    }
                }

                cs.endText()
            }
        }

        return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
    }
}

private fun delaLogiskRad(rad: String): List<String> {
    if (rad.length <= MAX_RADLANGD) {
        return listOf(rad)
    }

    val rader = mutableListOf<String>()
    var rest = rad
    while (rest.length > MAX_RADLANGD) {
        val segment = rest.substring(0, MAX_RADLANGD)
        val sistaMellanslag = segment.lastIndexOf(' ')
        if (sistaMellanslag > 0) {
            rader.add(rest.substring(0, sistaMellanslag))
            rest = rest.substring(sistaMellanslag + 1)
        } else {
            rader.add(rest.substring(0, MAX_RADLANGD))
            rest = rest.substring(MAX_RADLANGD)
        }
    }
    if (rest.isNotEmpty()) {
        rader.add(rest)
    }
    return rader
}
