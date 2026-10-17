package se.tmconnect.karna.export

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.OutputStream

const val PDF_PAGE_WIDTH = 595
const val PDF_PAGE_HEIGHT = 842
const val PDF_MARGIN = 48f
const val PDF_RAD_AVSTAND = 18f

class PdfSidSkrivare(
    private val dokument: PdfDocument,
) {
    private var sidNr = 0
    lateinit var sida: PdfDocument.Page
        private set
    var canvas: Canvas = Canvas()
        private set
    var y: Float = PDF_MARGIN

    fun startaForstaSida() {
        sidNr = 1
        sida = dokument.startPage(PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, sidNr).create())
        canvas = sida.canvas
        y = PDF_MARGIN
    }

    fun behovNySida(hojd: Float) {
        if (y + hojd > PDF_PAGE_HEIGHT - PDF_MARGIN) {
            dokument.finishPage(sida)
            sidNr++
            sida = dokument.startPage(PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, sidNr).create())
            canvas = sida.canvas
            y = PDF_MARGIN
        }
    }

    fun avslutaSida() {
        dokument.finishPage(sida)
    }
}

fun ritaRadText(
    canvas: Canvas,
    text: String,
    x: Float,
    startY: Float,
    maxWidth: Float,
    paint: Paint,
    radAvstand: Float = PDF_RAD_AVSTAND,
): Float {
    if (text.isEmpty()) {
        return startY
    }
    var yPos = startY
    var rest = text
    while (rest.isNotEmpty()) {
        val antal = paint.breakText(rest, true, maxWidth, null)
        val rad = if (antal >= rest.length) rest else rest.substring(0, antal)
        canvas.drawText(rad.trimEnd(), x, yPos, paint)
        yPos += radAvstand
        rest = if (antal >= rest.length) "" else rest.substring(antal).trimStart()
    }
    return yPos
}

fun skrivPdfDokument(block: (PdfDocument, PdfSidSkrivare) -> Unit, outputStream: OutputStream): PdfExportResult {
    val dokument = PdfDocument()
    return try {
        val skrivare = PdfSidSkrivare(dokument)
        skrivare.startaForstaSida()
        block(dokument, skrivare)
        skrivare.avslutaSida()
        dokument.writeTo(outputStream)
        PdfExportResult.Ok
    } catch (e: Exception) {
        PdfExportResult.Fel(e.message ?: e.javaClass.simpleName)
    } finally {
        dokument.close()
    }
}
