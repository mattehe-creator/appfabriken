package se.tmconnect.flextid.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LedighetsutrymmeTest {
    @Test
    fun exempel() {
        assertEquals(2, ledighetsutrymme(1200, 0, 480))
        assertEquals(0, ledighetsutrymme(479, 0, 480))
        assertEquals(1, ledighetsutrymme(300, -480, 480))
        assertNull(ledighetsutrymme(500, null, 480))
        assertNull(ledighetsutrymme(1200, 0, 0))
    }

    @Test
    fun saldoExaktPaGolvet() {
        assertEquals(0, ledighetsutrymme(480, 480, 480))
        assertEquals(0, ledighetsutrymme(-480, -480, 480))
    }
}
