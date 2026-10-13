package se.tmconnect.garantivalvet.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import se.tmconnect.garantivalvet.regler.arPdf
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.beraknaPdfStorlek
import se.tmconnect.garantivalvet.regler.exifOrienteringTillGrader
import se.tmconnect.garantivalvet.regler.summeraRotation
import se.tmconnect.garantivalvet.regler.beraknaNedskaladStorlek

/**
 * Lagrar kvittobilder i appens interna lagring ([Context.filesDir]/kvitton/).
 */
class KvittoLager(
    private val context: Context,
) {
    private val kvittonKatalog: File
        get() = File(context.filesDir, KVITTON_MAPP).also { it.mkdirs() }

    /**
     * Kopierar bilden från [kallaUri], skalar ned till högst 2000 px på längsta sidan (JPEG 85)
     * och returnerar filnamnet (utan sökväg) som ska sparas i [Kop.kvittoFil].
     */
    fun kopieraFranUri(kallaUri: Uri): String? {
        // Först läs storleken utan att ladda hela bilden
        val (bredd, hojd) = lasStorlek(kallaUri) ?: return null

        // Beräkna inSampleSize baserat på storlek
        val sampleSize = beraknaInSampleSize(bredd, hojd)

        // Läs bilden med rätt inSampleSize
        val bitmap = context.contentResolver.openInputStream(kallaUri)?.use { input ->
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            BitmapFactory.decodeStream(input, null, decodeOptions)
        } ?: return null

        val (malBredd, malHojd) = beraknaNedskaladStorlek(bitmap.width, bitmap.height)
        val skalad = if (malBredd != bitmap.width || malHojd != bitmap.height) {
            Bitmap.createScaledBitmap(bitmap, malBredd, malHojd, true).also {
                if (it !== bitmap) {
                    bitmap.recycle()
                }
            }
        } else {
            bitmap
        }

        val filnamn = "${UUID.randomUUID()}.jpg"
        return try {
            FileOutputStream(File(kvittonKatalog, filnamn)).use { ut ->
                skalad.compress(Bitmap.CompressFormat.JPEG, JPEG_KVALITET, ut)
            }
            filnamn
        } finally {
            skalad.recycle()
        }
    }

    /**
     * Sparar en bild på felet (galleri eller kamera): görs upprätt, skalas ned och läggs bland de sparade filerna.
     * Returnerar filnamnet, eller `null` om bilden inte gick att läsa.
     */
    fun sparaFelBild(kalla: Uri): String? {
        // Skrivs direkt till kvittomappen (en kodning, ingen mellanfil i cache).
        val filnamn = "${UUID.randomUUID()}.jpg"
        val mal = File(kvittonKatalog, filnamn)
        val klar = try {
            normaliseraTillFil(kalla, 0, mal)
        } catch (_: Exception) {
            false
        } catch (_: OutOfMemoryError) {
            false
        }
        if (!klar) {
            mal.delete()
            return null
        }
        raderaEgenCacheFil(kalla)
        return filnamn
    }

    /** Tar bort en kamerafil ur appens egen cache när den är sparad. Främmande URI:er (galleriet) rörs aldrig. */
    private fun raderaEgenCacheFil(uri: Uri) {
        if (uri.authority != fileProviderAuthority(context) || uri.pathSegments.firstOrNull() != "kamera") {
            return
        }
        try {
            context.contentResolver.delete(uri, null, null)
        } catch (_: Exception) {
        }
    }

    /** Rensar gamla filer i cache (kamerabilder och exporterade bilagor) som inte behövs längre. */
    fun rensaCache(aldreAnMillis: Long = 24L * 60 * 60 * 1000) {
        val gransen = System.currentTimeMillis() - aldreAnMillis
        listOf(KAMERA_CACHE_MAPP, EXPORT_CACHE_MAPP).forEach { namn ->
            File(context.cacheDir, namn).listFiles()?.forEach { fil ->
                if (fil.isFile && fil.lastModified() < gransen) fil.delete()
            }
        }
    }

    /**
     * Tar emot ett kvitto som delats till appen (bild eller PDF) och lägger det som JPEG i cache,
     * så att det inte beror på avsändarens tillfälliga läsrättighet. PDF:ens första sida renderas till en bild.
     * Returnerar FileProvider-URI, eller `null` om filen inte gick att läsa.
     */
    fun tagEmotDelatKvitto(kalla: Uri, mime: String?): Uri? {
        val katalog = File(context.cacheDir, KAMERA_CACHE_MAPP).also { it.mkdirs() }
        val fil = File(katalog, "${UUID.randomUUID()}.jpg")
        val klar = try {
            if (arPdf(mime)) renderaPdfForstaSidan(kalla, fil) else normaliseraTillFil(kalla, 0, fil)
        } catch (_: Exception) {
            false
        }
        if (!klar) {
            fil.delete()
            return null
        }
        return FileProvider.getUriForFile(context, fileProviderAuthority(context), fil)
    }

    /**
     * Gör en kopia i cache där bilden står rätt: EXIF-orienteringen läggs in i pixlarna (BitmapFactory ignorerar den)
     * och bilden roteras [extraGrader] medurs. Skalas ned till högst 2000 px. Returnerar FileProvider-URI, eller
     * `null` om bilden inte gick att läsa.
     */
    fun normalisera(kalla: Uri, extraGrader: Int = 0): Uri? {
        val katalog = File(context.cacheDir, KAMERA_CACHE_MAPP).also { it.mkdirs() }
        val fil = File(katalog, "${UUID.randomUUID()}.jpg")
        val klar = try {
            normaliseraTillFil(kalla, extraGrader, fil)
        } catch (_: Exception) {
            false
        } catch (_: OutOfMemoryError) {
            false
        }
        if (!klar) {
            fil.delete()
            return null
        }
        return FileProvider.getUriForFile(context, fileProviderAuthority(context), fil)
    }

    /**
     * Bildens mått utan att ladda den. `decodeStream` med `inJustDecodeBounds` returnerar alltid `null`, så
     * måtten läses ur [BitmapFactory.Options] och aldrig ur returvärdet.
     */
    private fun lasStorlek(kalla: Uri): Pair<Int, Int>? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val strom = context.contentResolver.openInputStream(kalla) ?: return null
        strom.use { BitmapFactory.decodeStream(it, null, options) }
        return if (options.outWidth > 0 && options.outHeight > 0) options.outWidth to options.outHeight else null
    }

    private fun normaliseraTillFil(kalla: Uri, extraGrader: Int, mal: File): Boolean {
        val (bredd, hojd) = lasStorlek(kalla) ?: return false
        val bitmap = context.contentResolver.openInputStream(kalla)?.use { input ->
            BitmapFactory.decodeStream(
                input,
                null,
                BitmapFactory.Options().apply { inSampleSize = beraknaInSampleSize(bredd, hojd) },
            )
        } ?: return false
        val grader = summeraRotation(exifGrader(kalla), extraGrader)
        val rakt = if (grader == 0) {
            bitmap
        } else {
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                Matrix().apply { postRotate(grader.toFloat()) },
                true,
            ).also { if (it !== bitmap) bitmap.recycle() }
        }
        return try {
            FileOutputStream(mal).use { ut -> rakt.compress(Bitmap.CompressFormat.JPEG, 90, ut) }
        } finally {
            rakt.recycle()
        }
    }

    private fun exifGrader(kalla: Uri): Int =
        try {
            context.contentResolver.openInputStream(kalla)?.use { input ->
                exifOrienteringTillGrader(
                    ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL),
                )
            } ?: 0
        } catch (_: Exception) {
            0
        }

    private fun renderaPdfForstaSidan(kalla: Uri, mal: File): Boolean {
        val pfd = context.contentResolver.openFileDescriptor(kalla, "r") ?: return false
        pfd.use {
            PdfRenderer(it).use { renderare ->
                if (renderare.pageCount == 0) return false
                renderare.openPage(0).use { sida ->
                    val (bredd, hojd) = beraknaPdfStorlek(sida.width, sida.height)
                    val bitmap = Bitmap.createBitmap(bredd, hojd, Bitmap.Config.ARGB_8888)
                    try {
                        bitmap.eraseColor(Color.WHITE)
                        sida.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        FileOutputStream(mal).use { ut -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ut) }
                    } finally {
                        bitmap.recycle()
                    }
                }
            }
        }
        return true
    }

    /**
     * FileProvider-URI för att visa eller dela ett sparat kvitto.
     */
    fun innehallUriForFilnamn(filnamn: String): Uri {
        val fil = File(kvittonKatalog, filnamn)
        return FileProvider.getUriForFile(context, fileProviderAuthority(context), fil)
    }

    /**
     * Skapar en temporär fil i cache för kamera-intent ([TakePicture](android.provider.MediaStore.ACTION_IMAGE_CAPTURE)).
     * Anropa [kopieraFranUri] med returnerad URI när fotot är taget.
     */
    fun skapaKameraFotoUri(): Uri {
        val katalog = File(context.cacheDir, KAMERA_CACHE_MAPP).also { it.mkdirs() }
        val fil = File(katalog, "${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(context, fileProviderAuthority(context), fil)
    }

    /**
     * Kopierar ett sparat kvitto till exportmappen under ett läsbart namn (till exempel för mejlbilagan) och
     * returnerar FileProvider-URI, eller `null` om kvittot saknas.
     */
    fun kopieraTillExport(filnamn: String, malNamn: String): Uri? {
        val kalla = File(kvittonKatalog, filnamn)
        if (!kalla.isFile) {
            return null
        }
        val katalog = File(context.cacheDir, EXPORT_CACHE_MAPP).also { it.mkdirs() }
        val mal = File(katalog, malNamn)
        kalla.copyTo(mal, overwrite = true)
        return FileProvider.getUriForFile(context, fileProviderAuthority(context), mal)
    }

    /** Rå bytes för export till säkerhetskopia. */
    fun lasRåBytes(filnamn: String): ByteArray? {
        val fil = File(kvittonKatalog, filnamn)
        if (!fil.isFile) {
            return null
        }
        return fil.readBytes()
    }

    /** Sparar kvittodata vid import; returnerar nytt filnamn i [kvitton/]. */
    fun sparaFranBytes(innehall: ByteArray, ursprungligtFilnamn: String): String {
        val delar = ursprungligtFilnamn.substringAfterLast('.', missingDelimiterValue = "")
        val andelse = delar.ifEmpty { "jpg" }.take(10)
        val filnamn = "${UUID.randomUUID()}.$andelse"
        File(kvittonKatalog, filnamn).writeBytes(innehall)
        return filnamn
    }

    /** Tar bort kvittofilen om [filnamn] inte är null. */
    fun taBort(filnamn: String?) {
        if (filnamn.isNullOrBlank()) {
            return
        }
        File(kvittonKatalog, filnamn).delete()
    }

    companion object {
        private const val KVITTON_MAPP = "kvitton"
        private const val KAMERA_CACHE_MAPP = "kamera"
        private const val EXPORT_CACHE_MAPP = "export"
        private const val JPEG_KVALITET = 85

        /** Authority för FileProvider i manifestet. */
        fun fileProviderAuthority(context: Context): String =
            "${context.packageName}.kvittofil"
    }
}
