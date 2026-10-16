package se.tmconnect.karna.bild

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Lagrar bilder i appens interna lagring ([Context.filesDir]/[katalogNamn]/).
 */
class BildLager(
    private val context: Context,
    private val katalogNamn: String,
    private val fileProviderAuthority: String,
) {
    private val bildKatalog: File
        get() = File(context.filesDir, katalogNamn).also { it.mkdirs() }

    /**
     * Kopierar bilden från [kallaUri], skalar ned till högst 2000 px på längsta sidan (JPEG 85)
     * och returnerar filnamnet (utan sökväg).
     */
    fun kopieraFranUri(kallaUri: Uri): String? {
        val (bredd, hojd) = lasStorlek(kallaUri) ?: return null
        val sampleSize = beraknaInSampleSize(bredd, hojd)
        val bitmap = context.contentResolver.openInputStream(kallaUri)?.use { input ->
            BitmapFactory.decodeStream(
                input,
                null,
                BitmapFactory.Options().apply { inSampleSize = sampleSize },
            )
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
            FileOutputStream(File(bildKatalog, filnamn)).use { ut ->
                skalad.compress(Bitmap.CompressFormat.JPEG, JPEG_KVALITET, ut)
            }
            raderaEgenCacheFil(kallaUri)
            filnamn
        } finally {
            skalad.recycle()
        }
    }

    fun innehallUriForFilnamn(filnamn: String): Uri {
        val fil = File(bildKatalog, filnamn)
        return FileProvider.getUriForFile(context, fileProviderAuthority, fil)
    }

    /** Temporär fil i cache för [TakePicture](android.provider.MediaStore.ACTION_IMAGE_CAPTURE). */
    fun skapaKameraFotoUri(): Uri {
        val katalog = File(context.cacheDir, KAMERA_CACHE_MAPP).also { it.mkdirs() }
        val fil = File(katalog, "${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(context, fileProviderAuthority, fil)
    }

    fun taBort(filnamn: String?) {
        if (filnamn.isNullOrBlank()) {
            return
        }
        File(bildKatalog, filnamn).delete()
    }

    fun filFinns(filnamn: String): Boolean = File(bildKatalog, filnamn).isFile

    private fun lasStorlek(kalla: Uri): Pair<Int, Int>? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val strom = context.contentResolver.openInputStream(kalla) ?: return null
        strom.use { BitmapFactory.decodeStream(it, null, options) }
        return if (options.outWidth > 0 && options.outHeight > 0) {
            options.outWidth to options.outHeight
        } else {
            null
        }
    }

    private fun raderaEgenCacheFil(uri: Uri) {
        if (uri.authority != fileProviderAuthority || uri.pathSegments.firstOrNull() != "kamera") {
            return
        }
        try {
            context.contentResolver.delete(uri, null, null)
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val KAMERA_CACHE_MAPP = "kamera"
        private const val JPEG_KVALITET = 85
    }
}
