package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NormaliseringTest {
    @Test
    fun normaliseraVanligStartMin() {
        assertEquals(480, normaliseraVanligStartMin(null))
        assertEquals(0, normaliseraVanligStartMin(-5))
        assertEquals(1439, normaliseraVanligStartMin(2000))
        assertEquals(450, normaliseraVanligStartMin(450))
    }

    @Test
    fun normaliseraVanligSlutMin() {
        assertEquals(990, normaliseraVanligSlutMin(null))
    }

    @Test
    fun normaliseraVanligRastMin() {
        assertEquals(30, normaliseraVanligRastMin(null))
        assertEquals(0, normaliseraVanligRastMin(-10))
        assertEquals(45, normaliseraVanligRastMin(45))
    }

    @Test
    fun arGiltigtStamplaInPaminnelseMinEfterMidnatt() {
        assertTrue(arGiltigtStamplaInPaminnelseMinEfterMidnatt(300))
        assertTrue(arGiltigtStamplaInPaminnelseMinEfterMidnatt(720))
        assertFalse(arGiltigtStamplaInPaminnelseMinEfterMidnatt(750))
        assertFalse(arGiltigtStamplaInPaminnelseMinEfterMidnatt(299))
        assertFalse(arGiltigtStamplaInPaminnelseMinEfterMidnatt(315))
    }

    @Test
    fun veckodagBit() {
        assertEquals(1, veckodagBit(DayOfWeek.MONDAY))
        assertEquals(64, veckodagBit(DayOfWeek.SUNDAY))
    }
}
