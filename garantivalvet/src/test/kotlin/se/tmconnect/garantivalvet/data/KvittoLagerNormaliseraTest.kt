package se.tmconnect.garantivalvet.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Kvittobilden görs upprätt och kan roteras; mått byter plats vid 90 och 270 grader. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class KvittoLagerNormaliseraTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val lager = KvittoLager(context)

    /** FileProvider cachar sökvägarna per authority i en static; varje Robolectric-test får en ny datamapp. */
    @Before
    fun nollstallFileProviderCache() {
        val falt = androidx.core.content.FileProvider::class.java.getDeclaredField("sCache")
        falt.isAccessible = true
        (falt.get(null) as MutableMap<*, *>).clear()
    }

    private fun skapaBild(bredd: Int, hojd: Int): Uri {
        val bitmap = Bitmap.createBitmap(bredd, hojd, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.LTGRAY)
        val fil = File(context.cacheDir, "test-$bredd-$hojd.jpg")
        FileOutputStream(fil).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return Uri.fromFile(fil)
    }

    private fun mattPa(uri: Uri): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)!!.use { BitmapFactory.decodeStream(it, null, options) }
        return options.outWidth to options.outHeight
    }

    @Test
    fun utanRotationBehallsMatten() {
        val ny = lager.normalisera(skapaBild(200, 100))
        assertNotNull(ny)
        assertEquals(200 to 100, mattPa(ny!!))
    }

    @Test
    fun nittiGraderByterPlatsPaBreddOchHojd() {
        val ny = lager.normalisera(skapaBild(200, 100), 90)
        assertNotNull(ny)
        assertEquals(100 to 200, mattPa(ny!!))
    }

    @Test
    fun rotationKanGoraTvaGangerOchBakat() {
        val forsta = lager.normalisera(skapaBild(200, 100), 90)!!
        val andra = lager.normalisera(forsta, 270)
        assertNotNull(andra)
        assertEquals(200 to 100, mattPa(andra!!))
    }

    @Test
    fun enFilSomInteArEnBildGerNull() {
        val fil = File(context.cacheDir, "inte-en-bild.jpg").apply { writeText("det här är text") }
        assertNull(lager.normalisera(Uri.fromFile(fil)))
    }

    @Test
    fun ettKvittoSomSparasFinnsKvarOchKanLasas() {
        val filnamn = lager.kopieraFranUri(skapaBild(200, 100))
        assertNotNull("kopieraFranUri returnerade null: kvittot sparades inte", filnamn)
        val fil = File(context.filesDir, "kvitton/$filnamn")
        org.junit.Assert.assertTrue(fil.isFile)
        assertEquals(200 to 100, mattPa(Uri.fromFile(fil)))
        org.junit.Assert.assertNotNull(lager.lasRåBytes(filnamn!!))
    }

    @Test
    fun enStorBildSkalasNedVidSparandet() {
        val filnamn = lager.kopieraFranUri(skapaBild(4000, 2000))!!
        val (b, h) = mattPa(Uri.fromFile(File(context.filesDir, "kvitton/$filnamn")))
        assertEquals(2000, b)
        assertEquals(1000, h)
    }

    @Test
    fun enFilSomInteArEnBildSparasInte() {
        val fil = File(context.cacheDir, "inte-en-bild2.jpg").apply { writeText("text") }
        assertNull(lager.kopieraFranUri(Uri.fromFile(fil)))
    }
}
