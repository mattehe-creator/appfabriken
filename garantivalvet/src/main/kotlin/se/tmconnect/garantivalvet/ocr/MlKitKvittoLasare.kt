package se.tmconnect.garantivalvet.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import se.tmconnect.garantivalvet.regler.exifOrienteringTillGrader
import se.tmconnect.garantivalvet.regler.valjBastaLasning

/** ML Kit Text Recognition med den medföljande latinska modellen: ingen nedladdning, inget nätverk. */
class MlKitKvittoLasare(context: Context) : KvittoLasare {
    private val appContext = context.applicationContext

    override suspend fun las(bild: Uri): LasResultat =
        try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            try {
                val bitmap = avkoda(bild)
                if (bitmap == null) {
                    lasEn(recognizer, InputImage.fromFilePath(appContext, bild)).let { text ->
                        text?.let { LasResultat.Text(it) } ?: LasResultat.IngenText
                    }
                } else {
                    // Kvitton ligger ofta på sidan eller upp och ned: läs i alla fyra rotationer och ta den bästa.
                    val lasningar = listOf(0, 90, 180, 270).map { grader ->
                        lasEn(recognizer, InputImage.fromBitmap(bitmap, grader)).orEmpty()
                    }
                    bitmap.recycle()
                    valjBastaLasning(lasningar, LocalDate.now())?.let { LasResultat.Text(it) } ?: LasResultat.IngenText
                }
            } finally {
                recognizer.close()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Avläsningen kastade", e)
            LasResultat.Fel(orsak(e))
        } catch (e: LinkageError) {
            // Till exempel NoClassDefFoundError när kodförminskningen tagit bort en klass ML Kit behöver.
            Log.w(TAG, "Avläsningen kastade", e)
            LasResultat.Fel(orsak(e))
        }

    private suspend fun lasEn(recognizer: TextRecognizer, image: InputImage): String? =
        suspendCancellableCoroutine { fortsatt ->
            recognizer.process(image)
                .addOnSuccessListener { resultat -> fortsatt.resume(resultat.text.takeIf { it.isNotBlank() }) }
                .addOnFailureListener { fel -> fortsatt.resumeWithException(fel) }
        }

    /** Bilden med EXIF-orienteringen tillämpad och högst cirka 2800 px på långsidan, eller `null` om den inte går att avkoda. */
    private suspend fun avkoda(bild: Uri): Bitmap? = withContext(Dispatchers.IO) {
        val resolver = appContext.contentResolver
        val storlek = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(bild)?.use { BitmapFactory.decodeStream(it, null, storlek) }
        if (storlek.outWidth <= 0 || storlek.outHeight <= 0) return@withContext null
        var sample = 1
        while (maxOf(storlek.outWidth, storlek.outHeight) / sample > MAX_SIDA) sample *= 2
        val bitmap = resolver.openInputStream(bild)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: return@withContext null
        val grader = try {
            resolver.openInputStream(bild)?.use {
                exifOrienteringTillGrader(
                    ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL),
                )
            } ?: 0
        } catch (_: Exception) {
            0
        }
        if (grader == 0) {
            bitmap
        } else {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(grader.toFloat()) }, true)
                .also { if (it !== bitmap) bitmap.recycle() }
        }
    }

    private fun orsak(fel: Throwable): String = "${fel.javaClass.simpleName}: ${fel.message.orEmpty()}".take(160)

    private companion object {
        const val TAG = "KvittoLasare"
        const val MAX_SIDA = 2800
    }
}
