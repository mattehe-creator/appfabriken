package se.tmconnect.garantivalvet.ocr

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class MlKitKvittoLasare(
    private val context: Context,
) : KvittoLasare {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun lasText(bild: Uri): String? {
        return try {
            val image = InputImage.fromFilePath(context, bild)
            val result = recognizer.process(image).await() ?: return null
            val text = result.text.trim()
            text.ifEmpty { null }
        } catch (_: Exception) {
            null
        }
    }
}

private suspend fun <T> Task<T>.await(): T? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resume(null) }
        addOnCanceledListener { cont.resume(null) }
    }
