package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HelgdagarTabellTest {

    @Test
    fun midsommaraftonOchMidsommardag2025Till2030() {
        val tabell = listOf(
            Triple(2025, LocalDate.of(2025, 6, 20), LocalDate.of(2025, 6, 21)),
            Triple(2026, LocalDate.of(2026, 6, 19), LocalDate.of(2026, 6, 20)),
            Triple(2027, LocalDate.of(2027, 6, 25), LocalDate.of(2027, 6, 26)),
            Triple(2028, LocalDate.of(2028, 6, 23), LocalDate.of(2028, 6, 24)),
            Triple(2029, LocalDate.of(2029, 6, 22), LocalDate.of(2029, 6, 23)),
            Triple(2030, LocalDate.of(2030, 6, 21), LocalDate.of(2030, 6, 22)),
        )
        for ((_, afton, dag) in tabell) {
            assertTrue(arSvenskHelgdag(afton))
            assertTrue(arSvenskHelgdag(dag))
        }
    }

    @Test
    fun ovrigaHelgdagarOchEjHelgdag2026() {
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 4, 3)))
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 4, 5)))
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 4, 6)))
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 5, 14)))
        assertFalse(arSvenskHelgdag(LocalDate.of(2026, 4, 7)))
        assertFalse(arSvenskHelgdag(LocalDate.of(2026, 5, 24)))
    }
}
