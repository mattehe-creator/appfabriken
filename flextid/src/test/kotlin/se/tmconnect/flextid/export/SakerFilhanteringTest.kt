package se.tmconnect.flextid.export

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SakerFilhanteringTest {
    @Test
    fun lyckadSkrivningGerSantOchStangerStrommen() = runTest {
        var stangd = false
        val ut =
            object : ByteArrayOutputStream() {
                override fun close() {
                    stangd = true
                    super.close()
                }
            }
        assertTrue(skrivSakert({ ut }) { it.write("abc".toByteArray()) })
        assertEquals("abc", ut.toString())
        assertTrue(stangd)
    }

    @Test
    fun filSomInteGarAttOppnaGerFalsktUtanKrasch() = runTest {
        assertFalse(skrivSakert({ throw FileNotFoundException("borta") }) { })
        assertFalse(skrivSakert({ throw SecurityException("nekad") }) { })
        assertFalse(skrivSakert({ null }) { })
    }

    @Test
    fun felUnderSkrivningGerFalskt() = runTest {
        assertFalse(skrivSakert({ ByteArrayOutputStream() }) { throw IOException("PDF") })
    }

    @Test
    fun lasningSomMisslyckasGerNull() = runTest {
        assertEquals("x", lasSakert({ ByteArrayInputStream("x".toByteArray()) }) { it.bufferedReader().readText() })
        assertNull(lasSakert({ throw FileNotFoundException("borta") }) { "" })
        assertNull(lasSakert({ null }) { "" })
    }
}
