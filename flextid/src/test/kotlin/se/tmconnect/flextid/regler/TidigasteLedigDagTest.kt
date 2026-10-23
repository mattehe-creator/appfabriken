package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TidigasteLedigDagTest {
    private val mask = ARBETSDAGAR_MANDAG_FREDAG

    @Test
    fun exempelEnDag() {
        assertEquals(
            LocalDate.of(2026, 10, 23),
            tidigasteLedigDag(LocalDate.of(2026, 10, 22), 1, mask, helgdagar = true),
        )
        assertEquals(
            LocalDate.of(2026, 10, 26),
            tidigasteLedigDag(LocalDate.of(2026, 10, 23), 1, mask, helgdagar = true),
        )
        assertNull(tidigasteLedigDag(LocalDate.of(2026, 10, 22), 0, mask, helgdagar = true))
    }

    @Test
    fun ledighetOverHelg() {
        assertEquals(
            LocalDate.of(2026, 10, 23),
            tidigasteLedigDag(LocalDate.of(2026, 10, 22), 2, mask, helgdagar = true),
        )
    }
}
