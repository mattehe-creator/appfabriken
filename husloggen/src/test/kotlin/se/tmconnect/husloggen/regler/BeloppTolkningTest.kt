package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BeloppTolkningTest {
    @Test
    fun tolkaBeloppExempel() {
        assertEquals(12_500L, tolkaBeloppTillOre("125"))
        assertEquals(12_550L, tolkaBeloppTillOre("125,50"))
        assertEquals(12_550L, tolkaBeloppTillOre("125.5"))
        assertNull(tolkaBeloppTillOre(""))
        assertNull(tolkaBeloppTillOre("12,345"))
        assertNull(tolkaBeloppTillOre("-5"))
    }

    @Test
    fun beloppOreTillFaltText() {
        assertEquals("125", beloppOreTillFaltText(12_500L))
        assertEquals("125,5", beloppOreTillFaltText(12_550L))
    }
}
