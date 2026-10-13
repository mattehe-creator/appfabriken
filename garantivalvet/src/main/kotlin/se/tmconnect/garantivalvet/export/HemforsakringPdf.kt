package se.tmconnect.garantivalvet.export

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import se.tmconnect.garantivalvet.data.KvittoLager
import se.tmconnect.garantivalvet.regler.HemforsakringSammanstallning
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import java.io.OutputStream
import java.util.Locale
import kotlin.math.min

data class HemforsakringTexter(
    val rubrik: String,
    val ingress: String,
    val vara: String,
    val butik: String,
    val datum: String,
    val pris: String,
    val summa: String,
    val saknarPris: String,
    val kvittonRubrik: String,
)

private const val SIDA_B = 595
private const val SIDA_H = 842
private const val MARG = 48f
private const val RAD_H = 18f

/**
 * Skriver en PDF för hemförsäkringen: en tabell över alla köp med värde och summa, därefter ett kvitto per sida.
 * Kvittona läses ett i taget och skalas ned, så minnet räcker även för många köp.
 */
fun skrivHemforsakringTillPdf(
    sammanstallning: HemforsakringSammanstallning,
    texter: HemforsakringTexter,
    locale: Locale,
    kvittoLager: KvittoLager,
    outputStream: OutputStream,
    inkluderaKvitton: Boolean = true,
): PdfExportResult {
    val dokument = PdfDocument()
    return try {
        var sidnr = 1
        var sida = dokument.startPage(PdfDocument.PageInfo.Builder(SIDA_B, SIDA_H, sidnr).create())
        var y = MARG
        val rubrik = Paint().apply {
            textSize = 20f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); isAntiAlias = true
        }
        val fet = Paint().apply {
            textSize = 11f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); isAntiAlias = true
        }
        val text = Paint().apply { textSize = 11f; isAntiAlias = true }
        val hoger = Paint(text).apply { textAlign = Paint.Align.RIGHT }
        val hogerFet = Paint(fet).apply { textAlign = Paint.Align.RIGHT }
        val kolDatum = 330f
        val kolButik = 200f
        val kolPrisHoger = SIDA_B - MARG

        fun nySida() {
            dokument.finishPage(sida)
            sidnr++
            sida = dokument.startPage(PdfDocument.PageInfo.Builder(SIDA_B, SIDA_H, sidnr).create())
            y = MARG
        }

        fun kapa(s: String, paint: Paint, bredd: Float): String {
            val n = paint.breakText(s, true, bredd, null)
            return if (n >= s.length) s else s.substring(0, n).trimEnd() + "…"
        }

        sida.canvas.drawText(texter.rubrik, MARG, y + 20f, rubrik)
        y += 36f
        var rest = texter.ingress
        while (rest.isNotEmpty()) {
            val n = text.breakText(rest, true, SIDA_B - 2 * MARG, null)
            sida.canvas.drawText(rest.substring(0, n).trimEnd(), MARG, y + 11f, text)
            y += RAD_H
            rest = rest.substring(n).trimStart()
        }
        y += 10f

        fun tabellRubrik() {
            sida.canvas.drawText(texter.vara, MARG, y + 11f, fet)
            sida.canvas.drawText(texter.butik, kolButik, y + 11f, fet)
            sida.canvas.drawText(texter.datum, kolDatum, y + 11f, fet)
            sida.canvas.drawText(texter.pris, kolPrisHoger, y + 11f, hogerFet)
            y += RAD_H + 4f
        }
        tabellRubrik()
        for (r in sammanstallning.rader) {
            if (y + RAD_H > SIDA_H - MARG) {
                nySida()
                tabellRubrik()
            }
            sida.canvas.drawText(kapa(r.vad, text, kolButik - MARG - 8f), MARG, y + 11f, text)
            sida.canvas.drawText(kapa(r.butik.orEmpty(), text, kolDatum - kolButik - 8f), kolButik, y + 11f, text)
            sida.canvas.drawText(formateraDatum(r.datum, locale), kolDatum, y + 11f, text)
            sida.canvas.drawText(r.prisOre?.let { formateraPrisKr(it, locale) } ?: "–", kolPrisHoger, y + 11f, hoger)
            y += RAD_H
        }
        if (y + 2 * RAD_H > SIDA_H - MARG) nySida()
        y += 8f
        sida.canvas.drawText(texter.summa, MARG, y + 11f, fet)
        sida.canvas.drawText(formateraPrisKr(sammanstallning.summaOre, locale), kolPrisHoger, y + 11f, hogerFet)
        if (sammanstallning.antalUtanPris > 0) {
            y += RAD_H
            sida.canvas.drawText(texter.saknarPris, MARG, y + 11f, text)
        }

        if (inkluderaKvitton) {
            for (r in sammanstallning.rader) {
                val filnamn = r.kvittoFil ?: continue
                val bitmap = laddaKvitto(kvittoLager, filnamn) ?: continue
                try {
                    nySida()
                    sida.canvas.drawText(texter.kvittonRubrik, MARG, y + 11f, fet)
                    y += RAD_H
                    sida.canvas.drawText(
                        kapa("${r.vad} · ${formateraDatum(r.datum, locale)}", text, SIDA_B - 2 * MARG),
                        MARG, y + 11f, text,
                    )
                    y += RAD_H + 6f
                    val maxB = SIDA_B - 2 * MARG
                    val maxH = SIDA_H - MARG - y
                    val skala = min(maxB / bitmap.width, maxH / bitmap.height)
                    val skalad = Bitmap.createScaledBitmap(
                        bitmap, (bitmap.width * skala).toInt().coerceAtLeast(1), (bitmap.height * skala).toInt().coerceAtLeast(1), true,
                    )
                    sida.canvas.drawBitmap(skalad, MARG, y, null)
                    if (skalad !== bitmap) skalad.recycle()
                } finally {
                    bitmap.recycle()
                }
            }
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

private fun laddaKvitto(kvittoLager: KvittoLager, filnamn: String): Bitmap? {
    val bytes = kvittoLager.lasRåBytes(filnamn) ?: return null
    return try {
        val grans = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, grans)
        val sample = beraknaInSampleSize(grans.outWidth, grans.outHeight, maxSida = 1600)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    } catch (_: Exception) {
        null
    }
}
