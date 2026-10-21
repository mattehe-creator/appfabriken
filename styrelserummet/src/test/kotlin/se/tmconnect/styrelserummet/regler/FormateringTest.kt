package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class FormateringTest {

    @Test
    fun kronor() {
        assertEquals("1\u00A0234\u00A0567 kr", kronor(1_234_567))
        assertEquals("0 kr", kronor(0))
        assertEquals("\u2212500 kr", kronor(-500))
    }

    @Test
    fun procentOchArsintervall() {
        assertEquals("12,5 %", procent(1, 8))
        assertEquals("\u2013", procent(0, 0))
        assertEquals("2010\u20132050", arsintervall(2010, 2050))
    }
}
