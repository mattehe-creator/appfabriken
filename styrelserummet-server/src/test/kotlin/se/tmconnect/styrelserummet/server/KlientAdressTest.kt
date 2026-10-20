package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Test

class KlientAdressTest {
    @Test
    fun proxyPaAnvanderForstaForwardedFor() {
        assertEquals(
            "203.0.113.5",
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
}
