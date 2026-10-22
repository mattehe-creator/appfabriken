package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MatareKostnadTest {

    @Test
    fun `1000 kWh till 150 ore per enhet ger 150000 ore`() {
        assertEquals(150_000L, forbrukningKostnadOre(1_000_000L, 150L))
    }

    @Test
    fun `2,5 kWh till 133 ore avrundas halva uppat till 333 ore`() {
        assertEquals(333L, forbrukningKostnadOre(2_500L, 133L))
    }

    @Test
    fun `minimal forbrukning ger 0 ore`() {
        assertEquals(0L, forbrukningKostnadOre(1L, 100L))
    }

    @Test
    fun `noll forbrukning ger 0 ore`() {
        assertEquals(0L, forbrukningKostnadOre(0L, 200L))
    }

    @Test
    fun `negativ forbrukning ger null`() {
        assertNull(forbrukningKostnadOre(-1_000L, 100L))
    }

    @Test
    fun `negativt pris ger null`() {
        assertNull(forbrukningKostnadOre(1_000L, -1L))
    }
}
