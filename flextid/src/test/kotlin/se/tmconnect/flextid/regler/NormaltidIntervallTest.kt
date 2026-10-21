package se.tmconnect.flextid.regler

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class NormaltidIntervallTest {
    
    @Test
    fun testArbetsdagarIIntervall() {
        val mask = ARBETSDAGAR_MANDAG_FREDAG
        
        // 2026-10-12 – 2026-10-18 → 5
        assertEquals(5, arbetsdagarIIntervall(
            LocalDate.of(2026, 10, 12), 
            LocalDate.of(2026, 10, 18), 
            mask, 
            false
        ))
        
        // 2026-03-30 – 2026-04-05 (påskveckan) → 4 
        assertEquals(4, arbetsdagarIIntervall(
            LocalDate.of(2026, 3, 30), 
            LocalDate.of(2026, 4, 5), 
            mask, 
            false
        ))
        
        // 2026-12-21 – 2026-12-27, aftnar lediga → 3 
        assertEquals(3, arbetsdagarIIntervall(
            LocalDate.of(2026, 12, 21), 
            LocalDate.of(2026, 12, 27), 
            mask, 
            true
        ))
        
        // 2026-12-21 – 2026-12-27, aftnar inte lediga → 4
        assertEquals(4, arbetsdagarIIntervall(
            LocalDate.of(2026, 12, 21), 
            LocalDate.of(2026, 12, 27), 
            mask, 
            false
        ))
        
        // fran efter till → 0
        assertEquals(0, arbetsdagarIIntervall(
            LocalDate.of(2026, 10, 18), 
            LocalDate.of(2026, 10, 12), 
            mask, 
            false
        ))
    }
    
    @Test
    fun testNormaltidForIntervallMin() {
        val mask = ARBETSDAGAR_MANDAG_FREDAG
        val normaltidPerDag = 480
        
        // 2026-10-12 – 2026-10-18 → 5 arbetsdagar → 2400 minuter
        assertEquals(2400, normaltidForIntervallMin(
            LocalDate.of(2026, 10, 12), 
            LocalDate.of(2026, 10, 18), 
            mask, 
            normaltidPerDag, 
            false
        ))
        
        // 2026-12-21 – 2026-12-27, aftnar lediga → 3 arbetsdagar → 1440 minuter
        assertEquals(1440, normaltidForIntervallMin(
            LocalDate.of(2026, 12, 21), 
            LocalDate.of(2026, 12, 27), 
            mask, 
            normaltidPerDag, 
            true
        ))
    }
}
