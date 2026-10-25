package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ForstaLordagenProcentTest {

    @Test
    fun forstaLordagen() {
        assertEquals(LocalDate.of(2026, 10, 24), forstaLordagenFran(LocalDate.of(2026, 10, 24)))
        assertEquals(LocalDate.of(2026, 10, 31), forstaLordagenFran(LocalDate.of(2026, 10, 25)))
        assertEquals(LocalDate.of(2026, 10, 24), forstaLordagenFran(LocalDate.of(2026, 10, 19)))
        assertEquals(LocalDate.of(2026, 6, 20), forstaLordagenFran(LocalDate.of(2026, 6, 20)))
        assertEquals(LocalDate.of(2027, 1, 2), forstaLordagenFran(LocalDate.of(2026, 12, 31)))
        assertEquals(LocalDate.of(2027, 6, 26), forstaLordagenFran(LocalDate.of(2027, 6, 20)))
    }

    @Test
    fun procentvarden() {
        assertEquals("33,3 %", procent(1, 3))
        assertEquals("66,7 %", procent(2, 3))
        assertEquals("150,0 %", procent(3, 2))
        assertEquals("0,0 %", procent(0, 7))
        assertEquals("0,1 %", procent(1, 1000))
        assertEquals("\u2013", procent(5, 0))
    }
}
