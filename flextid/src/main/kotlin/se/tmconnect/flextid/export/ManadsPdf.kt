package se.tmconnect.flextid.export

import android.graphics.Paint
import android.graphics.Typeface
import java.io.OutputStream
import java.time.YearMonth
import se.tmconnect.flextid.regler.ManadsRapportData
import se.tmconnect.flextid.regler.ManadsRapportTexter
import se.tmconnect.karna.export.PDF_MARGIN
import se.tmconnect.karna.export.PDF_PAGE_WIDTH
import se.tmconnect.karna.export.PDF_RAD_AVSTAND
import se.tmconnect.karna.export.PdfExportResult
import se.tmconnect.karna.export.PdfSidSkrivare
import se.tmconnect.karna.export.skrivPdfDokument

fun manadsPdfFilnamn(arManad: YearMonth): String =
    "flextid-${arManad.year}-${arManad.monthValue.toString().padStart(2, '0')}.pdf"

fun skrivManadsPdf(
    rapport: ManadsRapportData,
    texter: ManadsRapportTexter,
    outputStream: OutputStream,
): PdfExportResult =
    skrivPdfDokument({ _, skrivare ->
        var y = skrivare.y
        val rubrikPaint =
            Paint().apply {
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
        val underrubrikPaint =
            Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
        val tabellRubrikPaint =
            Paint().apply {
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
        val tabellTextPaint =
            Paint().apply {
                textSize = 8f
                isAntiAlias = true
            }
        val radHojd = 14f

        skrivare.canvas.drawText(rapport.rubrik, PDF_MARGIN, y + 14f, rubrikPaint)
        y += 28f

        if (rapport.ingaDagar) {
            skrivare.y = y
            skrivare.behovNySida(PDF_RAD_AVSTAND)
            skrivare.canvas.drawText(texter.ingaRegistreradeDagar, PDF_MARGIN, skrivare.y, tabellTextPaint)
            y = skrivare.y + PDF_RAD_AVSTAND
        } else {
            val kolumner = tabellKolumner()
            skrivare.y = y
            skrivare.behovNySida(radHojd * 2)
            ritaTabellRad(skrivare.canvas, texter.kolumnRubriker, kolumner, skrivare.y, tabellRubrikPaint)
            y = skrivare.y + radHojd
            for (rad in rapport.rader) {
                skrivare.y = y
                skrivare.behovNySida(radHojd)
                val celler =
                    listOf(
                        rad.datum,
                        rad.veckodag,
                        rad.start,
                        rad.slut,
                        rad.rast,
                        rad.arbetad,
                        rad.utfall,
                        rad.typ,
                    )
                ritaTabellRad(skrivare.canvas, celler, kolumner, skrivare.y, tabellTextPaint)
                y = skrivare.y + radHojd
            }
            y += 8f
            rapport.summaArbetad?.let { summa ->
                skrivare.y = y
                skrivare.behovNySida(PDF_RAD_AVSTAND * 2)
                skrivare.canvas.drawText(
                    "${texter.summaArbetadEtikett}: $summa",
                    PDF_MARGIN,
                    skrivare.y,
                    underrubrikPaint,
                )
                y = skrivare.y + PDF_RAD_AVSTAND
            }
            rapport.summaUtfall?.let { summa ->
                skrivare.y = y
                skrivare.behovNySida(PDF_RAD_AVSTAND)
                skrivare.canvas.drawText(
                    "${texter.summaUtfallEtikett}: $summa",
                    PDF_MARGIN,
                    skrivare.y,
                    underrubrikPaint,
                )
                y = skrivare.y + PDF_RAD_AVSTAND
            }
        }

        if (texter.normaltidManadEtikett.isNotBlank()) {
            skrivare.y = y + 4f
            skrivare.behovNySida(PDF_RAD_AVSTAND)
            skrivare.canvas.drawText(
                "${texter.normaltidManadEtikett}: ${rapport.normaltidManad}",
                PDF_MARGIN,
                skrivare.y,
                underrubrikPaint,
            )
            y = skrivare.y + PDF_RAD_AVSTAND - 4f
        }
        skrivare.y = y + 4f
        skrivare.behovNySida(PDF_RAD_AVSTAND)
        skrivare.canvas.drawText(
            "${texter.saldoManadsslutEtikett}: ${rapport.saldoManadsslut}",
            PDF_MARGIN,
            skrivare.y,
            underrubrikPaint,
        )
        skrivare.y = skrivare.y + PDF_RAD_AVSTAND
    }, outputStream)

private data class TabellKolumn(val x: Float, val bredd: Float)

private fun tabellKolumner(): List<TabellKolumn> {
    var x = PDF_MARGIN
    val bredder = listOf(62f, 58f, 36f, 36f, 30f, 44f, 44f, 52f)
    return bredder.map { bredd ->
        val kolumn = TabellKolumn(x, bredd)
        x += bredd
        kolumn
    }
}

private fun ritaTabellRad(
    canvas: android.graphics.Canvas,
    celler: List<String>,
    kolumner: List<TabellKolumn>,
    baselineY: Float,
    paint: Paint,
) {
    celler.forEachIndexed { index, text ->
        if (index >= kolumner.size) return@forEachIndexed
        val kolumn = kolumner[index]
        val visning =
            if (text.length <= 14) {
                text
            } else {
                text.take(13) + "…"
            }
        canvas.drawText(visning, kolumn.x, baselineY, paint)
    }
}
