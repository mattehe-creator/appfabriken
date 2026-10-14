package se.tmconnect.garantivalvet.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/** ML Kit Text Recognition med den medföljande latinska modellen: ingen nedladdning, inget nätverk. */
class MlKitKvittoLasare(context: Context) : KvittoLasare {
    private val appContext = context.applicationContext

    override suspend fun las(bild: Uri): LasResultat =
        try {
            val image = InputImage.fromFilePath(appContext, bild)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            try {
                suspendCancellableCoroutine { fortsatt ->
                    recognizer.process(image)
                        .addOnSuccessListener { resultat ->
                            fortsatt.resume(
                                resultat.text.takeIf { it.isNotBlank() }?.let { LasResultat.Text(it) }
                                    ?: LasResultat.IngenText,
                            )
                        }
                        .addOnFailureListener { fel ->
                            Log.w(TAG, "Avläsningen misslyckades", fel)
                            fortsatt.resume(LasResultat.Fel(orsak(fel)))
                        }
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

    private fun orsak(fel: Throwable): String = "${fel.javaClass.simpleName}: ${fel.message.orEmpty()}".take(160)

    private companion object {
        const val TAG = "KvittoLasare"
    }
}
