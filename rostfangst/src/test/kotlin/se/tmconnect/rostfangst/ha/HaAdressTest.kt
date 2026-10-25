package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HaAdressTest {
    @Test
    fun trimmar_och_tar_bort_avslutande_snedstreck() {
        assertEquals("http://192.168.1.10:8123", normaliseraHaAdress("  http://192.168.1.10:8123/ "))
        assertEquals("https://min.hemma.example", normaliseraHaAdress("https://min.hemma.example//"))
    }

    @Test
    fun prefixet_blir_gemener() {
        assertEquals("https://Hemma.example", normaliseraHaAdress("HTTPS://Hemma.example"))
    }

    @Test
    fun avvisar_ogiltiga() {
        assertNull(normaliseraHaAdress("192.168.1.10"))
        assertNull(normaliseraHaAdress("http://"))
        assertNull(normaliseraHaAdress(""))
        assertNull(normaliseraHaAdress("http://a b"))
    }
}
