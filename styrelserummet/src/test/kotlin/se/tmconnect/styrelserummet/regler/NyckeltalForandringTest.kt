package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NyckeltalForandringTest {

    @Test
    fun exemplen() {
        assertEquals(104L, forandringTiondelsProcent(309, 280))
        assertEquals("+10,4 %", formateraForandring(104))

        assertEquals(-53L, forandringTiondelsProcent(90, 95))
        assertEquals("\u22125,3 %", formateraForandring(-53))

        assertEquals(0L, forandringTiondelsProcent(808, 808))
        assertEquals("0,0 %", formateraForandring(0))

        assertNull(forandringTiondelsProcent(100, 0))
        assertEquals("\u2013", formateraForandring(null))

        assertNull(forandringTiondelsProcent(null, 5))
    }
}
