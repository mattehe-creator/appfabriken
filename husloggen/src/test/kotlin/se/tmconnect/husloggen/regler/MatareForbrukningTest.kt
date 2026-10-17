package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.husloggen.data.Avlasning

class MatareForbrukningTest {
    @Test
    fun periodforbrukningMellanTvaAvlasningar() {
        val avlasningar =
            listOf(
                Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 1), varde = 1_000_000),
                Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 2, 1), varde = 1_250_500),
            )
        val med = beraknaAvlasningarMedForbrukning(avlasningar)
        assertNull(med[0].periodForbrukning)
        assertEquals(250_500L, med[1].periodForbrukning)
    }

    @Test
    fun lagreAvlasningGerIngenNegativForbrukning() {
        val avlasningar =
            listOf(
                Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 1), varde = 1_000_000),
                Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 2, 1), varde = 1_250_500),
                Avlasning(id = 3, matareId = 1, datum = LocalDate.of(2026, 3, 1), varde = 900_000),
            )
        val med = beraknaAvlasningarMedForbrukning(avlasningar)
        assertNull(med.last().periodForbrukning)
    }

    @Test
    fun arsforbrukningSummerarPerioder() {
        val avlasningar =
            listOf(
                Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 1), varde = 1_000_000),
                Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 6, 1), varde = 1_100_000),
                Avlasning(id = 3, matareId = 1, datum = LocalDate.of(2027, 1, 1), varde = 1_200_000),
            )
        val med = beraknaAvlasningarMedForbrukning(avlasningar)
        val ar = beraknaArsforbrukning(med)
        assertEquals(100_000L, ar[2026])
        assertEquals(100_000L, ar[2027])
    }
}
