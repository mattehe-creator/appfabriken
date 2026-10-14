package se.tmconnect.garantivalvet.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class KvittoLagerFelBildTest {
    private lateinit var context: Context
    private lateinit var lager: KvittoLager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        lager = KvittoLager(context)
        val falt = androidx.core.content.FileProvider::class.java.getDeclaredField("sCache")
        falt.isAccessible = true
        (falt.get(null) as MutableMap<*, *>).clear()
    }

    private fun skapaBildUri(namn: String): Uri {
        val bitmap = Bitmap.createBitmap(80, 60, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.GREEN)
        val fil = File(context.cacheDir, namn)
        FileOutputStream(fil).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return Uri.fromFile(fil)
    }

    @Test
    fun sparaFelBildFranGalleriRorInteKallfilen() {
        val kalla = skapaBildUri("galleri-kalla.jpg")
        val filnamn = lager.sparaFelBild(kalla)
        assertNotNull(filnamn)
        assertTrue(File(context.filesDir, "kvitton/$filnamn").isFile)
        assertTrue(File(context.cacheDir, "galleri-kalla.jpg").isFile)
    }

    @Test
    fun sparaFelBildFranKameraTarBortCachefilen() {
        val kameraUri = lager.skapaKameraFotoUri()
        val foreAntal = File(context.cacheDir, "kamera").listFiles()?.count { it.isFile } ?: 0
        val bitmap = Bitmap.createBitmap(80, 60, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.CYAN)
        context.contentResolver.openOutputStream(kameraUri)!!.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        val filnamn = lager.sparaFelBild(kameraUri)
        assertNotNull(filnamn)
        val efterAntal = File(context.cacheDir, "kamera").listFiles()?.count { it.isFile } ?: 0
        assertEquals(foreAntal, efterAntal)
    }

    @Test
    fun trasigBildGerNullOchIngenFilKvar() {
        val trasig = File(context.cacheDir, "trasig-fel.jpg").apply { writeText("inte bild") }
        val fore = File(context.filesDir, "kvitton").list()?.size ?: 0
        assertNull(lager.sparaFelBild(Uri.fromFile(trasig)))
        val efter = File(context.filesDir, "kvitton").list()?.size ?: 0
        assertEquals(fore, efter)
    }

    @Test
    fun rensaCacheTarBaraBortGamlaFiler() {
        val kameraKatalog = File(context.cacheDir, "kamera").also { it.mkdirs() }
        val exportKatalog = File(context.cacheDir, "export").also { it.mkdirs() }
        val gammalKamera = File(kameraKatalog, "gammal.jpg").apply {
            writeText("x")
            setLastModified(System.currentTimeMillis() - 48L * 60 * 60 * 1000)
        }
        val nyKamera = File(kameraKatalog, "ny.jpg").apply { writeText("y") }
        val gammalExport = File(exportKatalog, "gammal.pdf").apply {
            writeText("z")
            setLastModified(System.currentTimeMillis() - 48L * 60 * 60 * 1000)
        }
        val nyExport = File(exportKatalog, "ny.pdf").apply { writeText("w") }

        lager.rensaCache(24L * 60 * 60 * 1000)

        assertFalse(gammalKamera.isFile)
        assertTrue(nyKamera.isFile)
        assertFalse(gammalExport.isFile)
        assertTrue(nyExport.isFile)
    }

    @Test
    fun taBortRaderarSparadFil() {
        val filnamn = lager.sparaFranBytes(byteArrayOf(1, 2, 3), "test.jpg")
        val fil = File(context.filesDir, "kvitton/$filnamn")
        assertTrue(fil.isFile)
        lager.taBort(filnamn)
        assertFalse(fil.isFile)
    }

}
