package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertTrue
import org.junit.Test

class HelgdagarMidsommarTest {

    @Test
    fun midsommaraftonOchMidsommardagArHelgdagar() {
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 6, 19)))
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 6, 20)))
        assertTrue(arSvenskHelgdag(LocalDate.of(2025, 6, 20)))
        assertTrue(arSvenskHelgdag(LocalDate.of(2025, 6, 21)))
    }
}
