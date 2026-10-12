package se.tmconnect.garantivalvet.ocr

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MlKitKvittoLasareInstrumentedTest {
    @Test
    fun lasTextFranPahittadeKvitton() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val lasare = MlKitKvittoLasare(context)
            val elgiganten = lasFranResurs(context, lasare, "ocr/elgiganten.png")
            val kjell = lasFranResurs(context, lasare, "ocr/kjell.png")
            assertNotNull("Elgiganten-kvitto", elgiganten)
            assertTrue(elgiganten!!.contains("ELGIGANTEN", ignoreCase = true))
            assertNotNull("Kjell-kvitto", kjell)
            assertTrue(kjell!!.contains("Kjell", ignoreCase = true))
            assertTrue(kjell.contains("349", ignoreCase = false))
        }

    private suspend fun lasFranResurs(
        context: android.content.Context,
        lasare: MlKitKvittoLasare,
        resursSokvag: String,
    ): String? {
        val stream =
            javaClass.classLoader?.getResourceAsStream(resursSokvag) ?: return null
        val kameraKatalog = File(context.cacheDir, "kamera").also { it.mkdirs() }
        val fil = File(kameraKatalog, resursSokvag.substringAfterLast('/'))
        fil.outputStream().use { out -> stream.copyTo(out) }
        val uri: Uri =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.kvittofil",
                fil,
            )
        return lasare.lasText(uri)
    }
}
