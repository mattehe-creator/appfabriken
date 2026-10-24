package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.husloggen.data.Avlasning

class ForegaendeAvlasningTest {
    private val a1 =
        Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 10), varde = 1_000_000)
    private val a2 =
        Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 2, 10), varde = 1_200_000)
    private val a3 =
        Avlasning(id = 3, matareId = 1, datum = LocalDate.of(2026, 3, 10), varde = 1_500_000)

    @Test
    fun foregaendeMellanTvaAvlasningar() {
        assertEquals(a2, foregaendeAvlasning(LocalDate.of(2026, 2, 15), listOf(a1, a2, a3)))
    }

    @Test
    fun sammaDagRaknasMed() {
        assertEquals(a2, foregaendeAvlasning(LocalDate.of(2026, 2, 10), listOf(a1, a2, a3)))
    }

    @Test
    fun allaSenareGerNull() {
        assertNull(foregaendeAvlasning(LocalDate.of(2026, 1, 9), listOf(a1, a2, a3)))
    }

    @Test
    fun senasteForeSlutetAvAret() {
        assertEquals(a3, foregaendeAvlasning(LocalDate.of(2026, 12, 31), listOf(a1, a2, a3)))
    }

    @Test
    fun redigerarIdExkluderarAvlasning() {
        assertEquals(
            a2,
            foregaendeAvlasning(LocalDate.of(2026, 3, 10), listOf(a1, a2, a3), redigerarId = 3),
        )
    }

    @Test
    fun redigerarIdGerTidigareAvlasning() {
        assertEquals(
            a1,
            foregaendeAvlasning(LocalDate.of(2026, 2, 10), listOf(a1, a2, a3), redigerarId = 2),
        )
    }

    @Test
    fun listordningSpelarIngenRoll() {
        assertEquals(a2, foregaendeAvlasning(LocalDate.of(2026, 2, 20), listOf(a3, a1, a2)))
    }

    @Test
    fun tomListaGerNull() {
        assertNull(foregaendeAvlasning(LocalDate.of(2026, 2, 20), emptyList()))
    }
}
