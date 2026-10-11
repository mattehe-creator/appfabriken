package se.tmconnect.garantivalvet.export

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import se.tmconnect.garantivalvet.regler.Underlag
import java.io.File
import java.io.OutputStream
import kotlin.math.min

sealed class PdfExportResult {
    data object Ok : PdfExportResult()

    data class Fel(val meddelande: String) : PdfExportResult()
}

private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 48f
private const val RAD_AVSTAND = 20f
private const val RUBRIK_STORLEK = 18f
private const val ETIKETT_STORLEK = 11f
private const val TEXT_STORLEK = 12f

fun skrivUnderlagTillPdf(
    underlag: Underlag,
    outputStream: OutputStream,
    kvittoBitmap: Bitmap? = null,
    kvittoFil: File? = null,
): PdfExportResult {
    val bitmap = kvittoBitmap ?: kvittoFil?.let { fil ->
        if (!fil.isFile) {
            null
        } else {
            try {
                BitmapFactory.decodeFile(fil.absolutePath)
            } catch (_: Exception) {
                null
            }
        }
    }

    val document = PdfDocument()
    return try {
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var y = MARGIN

        val rubrikPaint = Paint().apply {
            textSize = RUBRIK_STORLEK
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val etikettPaint = Paint().apply {
            textSize = ETIKETT_STORLEK
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val textPaint = Paint().apply {
            textSize = TEXT_STORLEK
            isAntiAlias = true
        }

        fun nySidaOmBehovs(hojd: Float) {
            if (y + hojd > PAGE_HEIGHT - MARGIN) {
                document.finishPage(page)
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pages.size + 1).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = MARGIN
            }
        }

        canvas.drawText(underlag.dokumentRubrik, MARGIN, y, rubrikPaint)
        y += RUBRIK_STORLEK + RAD_AVSTAND

        val contentWidth = PAGE_WIDTH - 2 * MARGIN
        for (rad in underlag.rader) {
            nySidaOmBehovs(RAD_AVSTAND * 3)
            canvas.drawText(rad.etikett, MARGIN, y, etikettPaint)
            y += RAD_AVSTAND
            y = ritaRadText(canvas, rad.varde, MARGIN, y, contentWidth, textPaint)
            y += RAD_AVSTAND
        }

        bitmap?.let { bmp ->
            val maxBredd = contentWidth
            val skala = min(1f, maxBredd / bmp.width.toFloat())
            val malBredd = bmp.width * skala
            val malHojd = bmp.height * skala
            nySidaOmBehovs(malHojd + RAD_AVSTAND)
            val skalad = if (skala < 1f) {
                Bitmap.createScaledBitmap(
                    bmp,
                    malBredd.toInt(),
                    malHojd.toInt(),
                    true,
                )
            } else {
                bmp
            }
            canvas.drawBitmap(skalad, MARGIN, y, null)
            if (skalad !== bmp) {
                skalad.recycle()
            }
        }

        document.finishPage(page)
        document.writeTo(outputStream)
        PdfExportResult.Ok
    } catch (e: Exception) {
        PdfExportResult.Fel(e.message ?: e.javaClass.simpleName)
    } finally {
        document.close()
    }
}

private fun ritaRadText(
    canvas: Canvas,
    text: String,
    x: Float,
    startY: Float,
    maxWidth: Float,
    paint: Paint,
): Float {
    if (text.isEmpty()) {
        return startY
    }
    var y = startY
    var rest = text
    while (rest.isNotEmpty()) {
        val antal = paint.breakText(rest, true, maxWidth, null)
        val rad = if (antal >= rest.length) rest else rest.substring(0, antal)
        canvas.drawText(rad.trimEnd(), x, y, paint)
        y += RAD_AVSTAND
        rest = if (antal >= rest.length) "" else rest.substring(antal).trimStart()
    }
    return y
}
