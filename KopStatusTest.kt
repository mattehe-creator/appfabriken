package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class KopStatusTest {
    
    @Test
    fun testKopStatusGarantiGaller() {
        assertEquals(KopStatus.GARANTI_GALLER, kopStatus(LocalDate.of(2024, 1, 10), 24, LocalDate.of(2025, 6, 1)))
    }
    
    @Test
    fun testKopStatusGarantiSnart() {
        assertEquals(KopStatus.GARANTI_SNART, kopStatus(LocalDate.of(2024, 1, 10), 24, LocalDate.of(2025, 12, 20)))
    }
    
    @Test
    fun testKopStatusReklamationGaller() {
        assertEquals(KopStatus.REKLAMATION_GALLER, kopStatus(LocalDate.of(2024, 1, 10), 24, LocalDate.of(2026, 2, 1)))
    }
    
    @Test
    fun testKopStatusUtgangen() {
        assertEquals(KopStatus.UTGANGEN, kopStatus(LocalDate.of(2024, 1, 10), 24, LocalDate.of(2027, 1, 11)))
    }
    
    @Test
    fun testKopStatusUtgangenForGarantiUtgangna() {
        assertEquals(KopStatus.UTGANGEN, kopStatus(LocalDate.of(2022, 4, 30), 12, LocalDate.of(2023, 5, 1)))
    }
}
