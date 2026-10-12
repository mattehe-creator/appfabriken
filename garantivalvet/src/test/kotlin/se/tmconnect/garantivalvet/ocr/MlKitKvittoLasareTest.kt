package se.tmconnect.garantivalvet.ocr

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MlKitKvittoLasareTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun lasTextFranPahittadeKvitton() =
        runBlocking {
            val lasare =
                try {
                    MlKitKvittoLasare(context)
                } catch (_: Exception) {
                    null
                }
            assumeTrue("ML Kit initieras inte under Robolectric", lasare != null)
            val elgiganten = lasSakert(lasare!!, "ocr/elgiganten.png")
            val kjell = lasSakert(lasare, "ocr/kjell.png")
            assumeTrue(
                "ML Kit OCR kördes inte här (Robolectric saknar native init); prova på enhet/emulator",
                elgiganten != null || kjell != null,
            )
            assertNotNull(elgiganten)
            assertTrue(elgiganten!!.contains("ELGIGANTEN", ignoreCase = true))
            assertNotNull(kjell)
            assertTrue(kjell!!.contains("Kjell", ignoreCase = true))
            assertTrue(kjell.contains("349", ignoreCase = false))
        }

    private suspend fun lasSakert(lasare: MlKitKvittoLasare, resursSokvag: String): String? =
        try {
            lasFranResurs(lasare, resursSokvag)
        } catch (_: Exception) {
            null
        }

    private suspend fun lasFranResurs(lasare: MlKitKvittoLasare, resursSokvag: String): String? {
        val stream =
            javaClass.classLoader?.getResourceAsStream(resursSokvag) ?: return null
        val kameraKatalog = File(context.cacheDir, "kamera").also { it.mkdirs() }
        val fil = File(kameraKatalog, resursSokvag.substringAfterLast('/'))
        fil.outputStream().use { out -> stream.copyTo(out) }
        val uri =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.kvittofil",
                fil,
            )
        context.grantUriPermission(
            context.packageName,
            uri,
            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
        return lasare.lasText(uri)
    }
}
