package se.tmconnect.karna.sakerhetskopia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class ZipSakerhetTest {
    @Test
    fun zipSlipAvvisas() {
        assertFalse(arSakerZipPost("../evil.txt"))
        assertFalse(arSakerZipPost("/etc/passwd"))
        assertFalse(arSakerZipPost("""C:\Windows\system.ini"""))
        assertTrue(arSakerZipPost("bilder/foto.jpg"))
        assertTrue(arSakerZipPost("logg.json"))
    }

    @Test
    fun begransadLasningStopparStoraFiler() {
        val data = ByteArray(100) { 1 }
        val bytes = lasBegransadeBytes(ByteArrayInputStream(data), maxBytes = 200)
        assertEquals(100, bytes.size)
        try {
            lasBegransadeBytes(ByteArrayInputStream(data), maxBytes = 50)
            error("Ska kasta")
        } catch (_: IllegalStateException) {
        }
    }
}
