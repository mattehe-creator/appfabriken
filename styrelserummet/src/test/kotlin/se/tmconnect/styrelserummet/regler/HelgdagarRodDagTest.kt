package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HelgdagarRodDagTest {
    
    @Test
    fun testArSvenskHelgdag() {
        // Långfredag 2026-04-03
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 4, 3)))
        
        // Inte en helgdag 2026-04-07
        assertFalse(arSvenskHelgdag(LocalDate.of(2026, 4, 7)))
        
        // Nationaldag 2026-06-06
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 6, 6)))
        
        // Midsommarafton 2026-06-19
        assertTrue(arSvenskHelgdag(LocalDate.of(2026, 6, 19)))
    }
}
