package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Test

class KlientAdressTest {
    @Test
    fun proxyPaAnvanderSistaForwardedFor() {
        assertEquals(
            "10.0.0.1",
            klientAdress("203.0.113.5, 10.0.0.1", "127.0.0.1", proxyPaa = true),
        )
    }

    @Test
    fun proxyAvIgnorerarForwardedFor() {
        assertEquals(
            "127.0.0.1",
            klientAdress("203.0.113.5, 10.0.0.1", "127.0.0.1", proxyPaa = false),
        )
    }

    @Test
    fun proxyPaUtanHeaderAnvanderAnslutning() {
        assertEquals("10.1.2.3", klientAdress(null, "10.1.2.3", proxyPaa = true))
    }

    @Test
    fun proxyPaSistaAdressNarForsoktForslan() {
        assertEquals(
            "203.0.113.5",
            klientAdress("9.9.9.9, 203.0.113.5", "127.0.0.1", proxyPaa = true),
        )
    }

    @Test
    fun proxyPaEnkelAdress() {
        assertEquals(
            "203.0.113.5",
            klientAdress("203.0.113.5", "127.0.0.1", proxyPaa = true),
        )
    }

    @Test
    fun proxyPaTomPostSist() {
        assertEquals(
            "203.0.113.5",
            klientAdress("203.0.113.5, ", "127.0.0.1", proxyPaa = true),
        )
    }

    @Test
    fun proxyPaAllaTomma() {
        assertEquals(
            "10.1.2.3",
            klientAdress(" , ", "10.1.2.3", proxyPaa = true),
        )
    }
}
