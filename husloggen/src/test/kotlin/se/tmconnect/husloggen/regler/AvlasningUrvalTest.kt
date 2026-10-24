package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.husloggen.data.Avlasning
import java.time.LocalDate

class AvlasningUrvalTest {
    @Test
    fun testSorteraAvlasningar() {
        val a = Avlasning(id = 3, matareId = 1, datum = LocalDate.of(2026, 3, 1), varde = 300)
        val b = Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 1), varde = 100)
        val c = Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 3, 1), varde = 290)

        assertEquals(listOf(1L, 2L, 3L), sorteraAvlasningar(listOf(a, b, c)).map { it.id })
        assertEquals(emptyList<Avlasning>(), sorteraAvlasningar(emptyList()))
    }

    @Test
    fun testSenasteAvlasningForeDatum() {
        val a = Avlasning(id = 3, matareId = 1, datum = LocalDate.of(2026, 3, 1), varde = 300)
        val b = Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 1), varde = 100)
        val c = Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 3, 1), varde = 290)

        assertEquals(1L, senasteAvlasningForeDatum(LocalDate.of(2026, 2, 15), listOf(a, b, c))?.id)
        assertEquals(3L, senasteAvlasningForeDatum(LocalDate.of(2026, 3, 1), listOf(a, b, c))?.id)
        assertNull(senasteAvlasningForeDatum(LocalDate.of(2025, 12, 31), listOf(a, b, c)))
    }
}
