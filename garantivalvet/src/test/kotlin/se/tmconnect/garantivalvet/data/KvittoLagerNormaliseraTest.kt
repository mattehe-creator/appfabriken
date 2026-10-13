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
}
