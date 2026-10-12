package se.tmconnect.garantivalvet.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/** ML Kit Text Recognition med den medföljande latinska modellen: ingen nedladdning, inget nätverk. */
class MlKitKvittoLasare(context: Context) : KvittoLasare {
    private val appContext = context.applicationContext

    override suspend fun lasText(bild: Uri): String? =
        try {
            val image = InputImage.fromFilePath(appContext, bild)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            try {
                suspendCancellableCoroutine { fortsatt ->
                    recognizer.process(image)
                        .addOnSuccessListener { fortsatt.resume(it.text.takeIf { text -> text.isNotBlank() }) }
                        .addOnFailureListener { fortsatt.resume(null) }
                }
            } finally {
                recognizer.close()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
}
