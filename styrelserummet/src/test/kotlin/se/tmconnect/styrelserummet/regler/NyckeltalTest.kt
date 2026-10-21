package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class NyckeltalTest {

    @Test
    fun avrundaKvotPositivaExempel() {
        assertEquals(4, avrundaKvot(7, 2))
        assertEquals(2, avrundaKvot(5, 3))
        assertEquals(0, avrundaKvot(1, 3))
        assertEquals(808, avrundaKvot(4_200_000, 5_200))
        assertEquals(0, avrundaKvot(0, 5))
    }

    @Test
    fun avrundaKvotNegativTaljare() {
        assertEquals(-4, avrundaKvot(-7, 2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun avrundaKvotNollNamnare() {
        avrundaKvot(5, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun avrundaKvotNegativNamnare() {
        avrundaKvot(5, -2)
    }
}
