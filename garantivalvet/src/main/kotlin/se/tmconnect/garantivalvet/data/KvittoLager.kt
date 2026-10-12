package se.tmconnect.garantivalvet.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import se.tmconnect.garantivalvet.regler.DELAT_KVITTO_MAX_BYTES
import se.tmconnect.garantivalvet.regler.arPdf
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.beraknaPdfStorlek
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
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        context.contentResolver.openInputStream(kallaUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        } ?: return null
        
        val bredd = options.outWidth
        val hojd = options.outHeight
        
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
     * Tar emot ett kvitto som delats till appen (bild eller PDF) och lägger det som JPEG i cache,
     * så att det inte beror på avsändarens tillfälliga läsrättighet. PDF:ens första sida renderas till en bild.
     * Returnerar FileProvider-URI, eller `null` om filen inte gick att läsa.
     */
    fun tagEmotDelatKvitto(kalla: Uri, mime: String?): Uri? {
        val katalog = File(context.cacheDir, KAMERA_CACHE_MAPP).also { it.mkdirs() }
        val fil = File(katalog, "${UUID.randomUUID()}.jpg")
        val klar = try {
            if (arPdf(mime)) renderaPdfForstaSidan(kalla, fil) else kopieraBild(kalla, fil)
        } catch (_: Exception) {
            false
        }
        if (!klar) {
            fil.delete()
            return null
        }
        return FileProvider.getUriForFile(context, fileProviderAuthority(context), fil)
    }

    private fun kopieraBild(kalla: Uri, mal: File): Boolean {
        val ok = context.contentResolver.openInputStream(kalla)?.use { input ->
            FileOutputStream(mal).use { ut ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var totalt = 0L
                while (true) {
                    val last = input.read(buffer)
                    if (last < 0) break
                    totalt += last
                    if (totalt > DELAT_KVITTO_MAX_BYTES) return false
                    ut.write(buffer, 0, last)
                }
                true
            }
        } ?: return false
        if (!ok) return false
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(mal.absolutePath, options)
        return options.outWidth > 0 && options.outHeight > 0
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
        private const val JPEG_KVALITET = 85

        /** Authority för FileProvider i manifestet. */
        fun fileProviderAuthority(context: Context): String =
            "${context.packageName}.kvittofil"
    }
}
