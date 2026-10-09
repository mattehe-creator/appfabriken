package se.tmconnect.garantivalvet.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
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
