package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class IntervallEtikettTest {
    @Test
    fun intervallEtikett() {
        assertEquals(IntervallEnhet.MANAD to 1, intervallEtikett(1))
        assertEquals(IntervallEnhet.MANAD to 6, intervallEtikett(6))
        assertEquals(IntervallEnhet.AR to 1, intervallEtikett(12))
        assertEquals(IntervallEnhet.MANAD to 18, intervallEtikett(18))
        assertEquals(IntervallEnhet.AR to 2, intervallEtikett(24))
    }
}
