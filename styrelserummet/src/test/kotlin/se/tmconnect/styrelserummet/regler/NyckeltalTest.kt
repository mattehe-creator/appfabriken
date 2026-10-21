package se.tmconnect.styrelserummet.regler

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NyckeltalTest {
    
    fun `test avrundaKvot med positiva tal`() {
        assertEquals(4, avrundaKvot(7, 2))
        assertEquals(2, avrundaKvot(5, 3))
        assertEquals(0, avrundaKvot(1, 3))
        assertEquals(808, avrundaKvot(4_200_000, 5_200))
        assertEquals(0, avrundaKvot(0, 5))
    }
    
    fun `test avrundaKvot med negativa tal`() {
        assertEquals(-4, avrundaKvot(-7, 2))
    }
    
    fun `test avrundaKvot med noll i täljare`() {
        assertEquals(0, avrundaKvot(0, 5))
    }
    
    fun `test avrundaKvot med noll i nämnare`() {
        assertFailsWith<IllegalArgumentException> {
            avrundaKvot(5, 0)
        }
    }
    
    fun `test avrundaKvot med negativ nämnare`() {
        assertFailsWith<IllegalArgumentException> {
            avrundaKvot(5, -2)
        }
    }
}
