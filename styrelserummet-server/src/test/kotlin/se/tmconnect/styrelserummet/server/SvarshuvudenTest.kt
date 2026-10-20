package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SvarshuvudenTest {
    @Test
    fun gemensammaPaAllaSokvagar() {
        val h = svarshuvudenForSokvag("/")
        assertEquals("no-referrer", h["Referrer-Policy"])
        assertEquals("nosniff", h["X-Content-Type-Options"])
        assertTrue(h["Content-Security-Policy"]!!.contains("default-src 'self'"))
    }

    @Test
    fun apiOchJobbHarNoStoreOchNoindex() {
        val api = svarshuvudenForSokvag("/api/jobb/abc")
        assertEquals("no-store", api["Cache-Control"])
        assertEquals("noindex", api["X-Robots-Tag"])

        val jobb = svarshuvudenForSokvag("/j/abc")
        assertEquals("no-store", jobb["Cache-Control"])
        assertEquals("noindex", jobb["X-Robots-Tag"])
    }

    @Test
    fun startsidaUtanNoStore() {
        val h = svarshuvudenForSokvag("/index.html")
        assertFalse(h.containsKey("Cache-Control"))
        assertFalse(h.containsKey("X-Robots-Tag"))
    }
}
