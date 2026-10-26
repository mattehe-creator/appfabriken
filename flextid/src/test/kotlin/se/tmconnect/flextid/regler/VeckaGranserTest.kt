package se.tmconnect.flextid.regler

import org.junit.Test
import java.time.LocalDate
import org.junit.Assert.assertEquals

class VeckaGranserTest {

    @Test
    fun testMandagISammaVecka() {
        // 2026-10-26 (måndag)
        assertEquals(LocalDate.of(2026, 10, 26), mandagISammaVecka(LocalDate.of(2026, 10, 26)))
        
        // 2026-10-28 (onsdag)
        assertEquals(LocalDate.of(2026, 10, 26), mandagISammaVecka(LocalDate.of(2026, 10, 28)))
        
        // 2026-10-25 (söndag)
        assertEquals(LocalDate.of(2026, 10, 19), mandagISammaVecka(LocalDate.of(2026, 10, 25)))
        
        // 2026-01-01 (torsdag)
        assertEquals(LocalDate.of(2025, 12, 29), mandagISammaVecka(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun testSondagISammaVecka() {
        // 2026-10-26 (måndag)
        assertEquals(LocalDate.of(2026, 11, 1), sondagISammaVecka(LocalDate.of(2026, 10, 26)))
        
        // 2026-10-28 (onsdag)
        assertEquals(LocalDate.of(2026, 11, 1), sondagISammaVecka(LocalDate.of(2026, 10, 28)))
        
        // 2026-10-25 (söndag)
        assertEquals(LocalDate.of(2026, 10, 25), sondagISammaVecka(LocalDate.of(2026, 10, 25)))
        
        // 2026-01-01 (torsdag)
        assertEquals(LocalDate.of(2026, 1, 4), sondagISammaVecka(LocalDate.of(2026, 1, 1)))
    }
}
