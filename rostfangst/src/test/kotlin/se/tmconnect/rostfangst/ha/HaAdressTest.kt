package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HaAdressTest {
    
    @Test
    fun testNormaliseraHaAdress() {
        assertEquals("http://192.168.1.10:8123", normaliseraHaAdress("  http://192.168.1.10:8123/ "))
        assertEquals("https://min.hemma.example", normaliseraHaAdress("https://min.hemma.example//"))
        assertEquals("https://Hemma.example", normaliseraHaAdress("HTTPS://Hemma.example"))
        assertEquals(null, normaliseraHaAdress("192.168.1.10"))
        assertEquals(null, normaliseraHaAdress("http://"))
        assertEquals(null, normaliseraHaAdress(""))
        assertEquals(null, normaliseraHaAdress("http://a b"))
    }
}
