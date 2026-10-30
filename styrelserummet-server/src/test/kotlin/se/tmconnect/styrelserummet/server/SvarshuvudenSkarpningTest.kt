package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SvarshuvudenSkarpningTest {
    @Test
    fun sakerhetshuvudenPaAllaSokvagar() {
        val h = svarshuvudenForSokvag("/")
        assertEquals("max-age=31536000", h["Strict-Transport-Security"])
        assertTrue(h["Content-Security-Policy"]!!.contains("base-uri 'none'"))
        assertTrue(h["Content-Security-Policy"]!!.contains("frame-ancestors 'none'"))
    }

    @Test
    fun sakerhetshuvudenPaApiOchJobb() {
        val api = svarshuvudenForSokvag("/api/jobb/abc")
        assertEquals("max-age=31536000", api["Strict-Transport-Security"])
        assertTrue(api["Content-Security-Policy"]!!.contains("base-uri 'none'"))
        assertTrue(api["Content-Security-Policy"]!!.contains("frame-ancestors 'none'"))
        
        val jobb = svarshuvudenForSokvag("/j/abc")
        assertEquals("max-age=31536000", jobb["Strict-Transport-Security"])
        assertTrue(jobb["Content-Security-Policy"]!!.contains("base-uri 'none'"))
        assertTrue(jobb["Content-Security-Policy"]!!.contains("frame-ancestors 'none'"))
    }

    @Test
    fun gemensammaHuvudenInnehallerRattAntal() {
        val h = gemensammaSvarshuvuden()
        assertEquals(4, h.size)
        assertTrue(h.containsKey("Referrer-Policy"))
        assertTrue(h.containsKey("X-Content-Type-Options"))
        assertTrue(h.containsKey("Content-Security-Policy"))
        assertTrue(h.containsKey("Strict-Transport-Security"))
    }

    @Test
    fun hstsVardetInnehallerInteSubdomains() {
        val h = gemensammaSvarshuvuden()
        assertEquals("max-age=31536000", h["Strict-Transport-Security"])
        val hsts = h["Strict-Transport-Security"]!!
        assertTrue(!hsts.contains("includeSubDomains"))
        assertTrue(!hsts.contains("preload"))
    }
}
