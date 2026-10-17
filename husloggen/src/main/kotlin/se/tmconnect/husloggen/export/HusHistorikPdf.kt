package se.tmconnect.husloggen.export

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import se.tmconnect.karna.export.PDF_MARGIN
import se.tmconnect.karna.export.PDF_PAGE_HEIGHT
import se.tmconnect.karna.export.PDF_PAGE_WIDTH
import se.tmconnect.karna.export.PDF_RAD_AVSTAND
import se.tmconnect.karna.export.PdfExportResult
import se.tmconnect.karna.export.ritaRadText
import se.tmconnect.karna.format.formateraPrisKr
import java.io.OutputStream
import java.util.Locale

fun skrivHusHistorikTillPdf(
    data: HusHistorikData,
    texter: HusHistorikTexter,
    locale: Locale,
    outputStream: OutputStream,
): PdfExportResult {
    val dokument = PdfDocument()
    return try {
        var sidNr = 1
        var sida = dokument.startPage(PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, sidNr).create())
        var canvas = sida.canvas
        var y = PDF_MARGIN
        val contentWidth = PDF_PAGE_WIDTH - 2 * PDF_MARGIN
        val rubrikPaint = Paint().apply {
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val underrubrikPaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val fet = Paint().apply {
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val text = Paint().apply {
            textSize = 11f
            isAntiAlias = true
        }

        fun nySidaOmBehovs(hojd: Float) {
            if (y + hojd > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                dokument.finishPage(sida)
                sidNr++
                sida = dokument.startPage(PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, sidNr).create())
                canvas = sida.canvas
                y = PDF_MARGIN
            }
        }

        canvas.drawText(texter.rubrik, PDF_MARGIN, y + 16f, rubrikPaint)
        y += 32f

        for (block in data.arBlock) {
            nySidaOmBehovs(80f)
            canvas.drawText(
                texter.arSummaRubrik(block.ar) + " — " + formateraPrisKr(block.totalOre.takeIf { it > 0 }, locale),
                PDF_MARGIN,
                y + 12f,
                underrubrikPaint,
            )
            y += 24f
            block.gransEtikett?.let { grans ->
                nySidaOmBehovs(PDF_RAD_AVSTAND)
                canvas.drawText(grans, PDF_MARGIN, y, text)
                y += PDF_RAD_AVSTAND
            }
            for ((typ, summa) in block.typSummor) {
                nySidaOmBehovs(PDF_RAD_AVSTAND)
                canvas.drawText(
                    texter.typSummaRad(typ, formateraPrisKr(summa, locale)),
                    PDF_MARGIN,
                    y,
                    text,
                )
                y += PDF_RAD_AVSTAND
            }
            for (rad in block.poster) {
                nySidaOmBehovs(PDF_RAD_AVSTAND * 3)
                val radText = buildString {
                    append(rad.datumEtikett)
                    append("  ")
                    append(rad.post.vad)
                    append(" (")
                    append(rad.typEtikett)
                    append(")")
                    if (rad.beloppEtikett.isNotEmpty()) {
                        append(" — ")
                        append(rad.beloppEtikett)
                    }
                    rad.forbattringEtikett?.let {
                        append(" [")
                        append(it)
                        append(']')
                    }
                }
                y = ritaRadText(canvas, radText, PDF_MARGIN, y, contentWidth, text)
                y += 4f
            }
            y += 8f
        }

        if (data.inkluderaSkatteAvsnitt) {
            nySidaOmBehovs(120f)
            y += 12f
            canvas.drawText(texter.skattRubrik, PDF_MARGIN, y, underrubrikPaint)
            y += 20f
            y = ritaRadText(canvas, texter.skattText, PDF_MARGIN, y, contentWidth, text)
            y += PDF_RAD_AVSTAND
            canvas.drawText(texter.kallorRubrik, PDF_MARGIN, y, fet)
            y += PDF_RAD_AVSTAND
            y = ritaRadText(canvas, texter.kallorText, PDF_MARGIN, y, contentWidth, text)
        }

        dokument.finishPage(sida)
        dokument.writeTo(outputStream)
        PdfExportResult.Ok
    } catch (e: Exception) {
        PdfExportResult.Fel(e.message ?: e.javaClass.simpleName)
    } finally {
        dokument.close()
    }
}
