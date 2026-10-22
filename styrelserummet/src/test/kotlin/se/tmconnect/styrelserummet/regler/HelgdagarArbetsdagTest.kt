package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HelgdagarArbetsdagTest {
    
    @Test
    fun testNarmasteArbetsdagFram() {
        // d=2026-04-03 (långfredag), riktning 1 → 2026-04-07 (tisdag)
        assertEquals(
            LocalDate.of(2026, 4, 7),
            narmasteArbetsdag(LocalDate.of(2026, 4, 3), 1)
        )
        
        // d=2026-04-05 (påskdagen, söndag), riktning 1 → 2026-04-07
        assertEquals(
            LocalDate.of(2026, 4, 7),
            narmasteArbetsdag(LocalDate.of(2026, 4, 5), 1)
        )
        
        // d=2026-06-19 (midsommarafton fredag), riktning 1 → 2026-06-22 (måndag)
        assertEquals(
            LocalDate.of(2026, 6, 22),
            narmasteArbetsdag(LocalDate.of(2026, 6, 19), 1)
        )
    }
    
    @Test
    fun testNarmasteArbetsdagBak() {
        // d=2026-04-07, riktning -1 → 2026-04-07
        assertEquals(
            LocalDate.of(2026, 4, 7),
            narmasteArbetsdag(LocalDate.of(2026, 4, 7), -1)
        )
    }
    
    @Test
    fun testNarmasteArbetsdagIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> {
            narmasteArbetsdag(LocalDate.of(2026, 4, 7), 0)
        }
        
        assertFailsWith<IllegalArgumentException> {
            narmasteArbetsdag(LocalDate.of(2026, 4, 7), 2)
        }
    }
}
