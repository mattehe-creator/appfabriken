package se.tmconnect.flextid.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class ManadsNormaltidTest {
    private val mask = ARBETSDAGAR_MANDAG_FREDAG

    @Test
    fun manader2026() {
        assertEquals(20, arbetsdagarIManad(2026, 4, mask, true, true))
        assertEquals(22, arbetsdagarIManad(2026, 4, mask, false, true))
        assertEquals(19, arbetsdagarIManad(2026, 5, mask, true, false))
        assertEquals(22, arbetsdagarIManad(2026, 6, mask, true, false))
        assertEquals(21, arbetsdagarIManad(2026, 6, mask, true, true))
        assertEquals(22, arbetsdagarIManad(2026, 12, mask, true, false))
        assertEquals(20, arbetsdagarIManad(2026, 12, mask, true, true))
        assertEquals(9600, normaltidForManadMin(2026, 4, mask, 480, true, true))
    }
}
