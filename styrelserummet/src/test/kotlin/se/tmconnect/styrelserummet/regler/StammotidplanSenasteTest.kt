package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StammotidplanSenasteTest {
    
    @Test
    fun testSenasteStammodag() {
        // arsslut 2025-12-31 → 2026-06-30
        assertEquals(LocalDate.of(2026, 6, 30), senasteStammodag(LocalDate.of(2025, 12, 31)))
        
        // arsslut 2024-08-31 → 2025-02-28
        assertEquals(LocalDate.of(2025, 2, 28), senasteStammodag(LocalDate.of(2024, 8, 31)))
        
        // arsslut 2023-08-31 → 2024-02-29 (skottår)
        assertEquals(LocalDate.of(2024, 2, 29), senasteStammodag(LocalDate.of(2023, 8, 31)))
        
        // arsslut 2025-06-30 → 2025-12-30
        assertEquals(LocalDate.of(2025, 12, 30), senasteStammodag(LocalDate.of(2025, 6, 30)))
    }
}
