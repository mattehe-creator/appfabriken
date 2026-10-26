package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class InspelningslangdTest {

    @Test
    fun testInspelningenSlut() {
        assertFalse(inspelningenSlut(59_999, 60_000))
        assertTrue(inspelningenSlut(60_000, 60_000))
        assertTrue(inspelningenSlut(61_000, 60_000))
    }

    @Test
    fun testFormateraLangd() {
        assertEquals("0:00", formateraLangd(0))
        assertEquals("0:07", formateraLangd(7_400))
        assertEquals("0:59", formateraLangd(59_999))
        assertEquals("1:00", formateraLangd(60_000))
        assertEquals("2:05", formateraLangd(125_000))
        assertEquals("0:00", formateraLangd(-5))
    }
}
