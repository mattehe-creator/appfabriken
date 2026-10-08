package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GarantiTest {
    
    @Test
    fun testGarantiSlut() {
        assertEquals(LocalDate.of(2026, 2, 28), garantiSlut(LocalDate.of(2026, 1, 31), 1))
        assertEquals(LocalDate.of(2028, 3, 15), garantiSlut(LocalDate.of(2026, 3, 15), 24))
        assertEquals(LocalDate.of(2026, 3, 15), garantiSlut(LocalDate.of(2026, 3, 15), 0))
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testGarantiSlutMedNegativtAntalManader() {
        garantiSlut(LocalDate.of(2026, 3, 15), -1)
    }
    
    @Test
    fun testGarantiStatusGaller() {
        assertEquals(GarantiStatus.GALLER, garantiStatus(LocalDate.of(2026, 1, 10), 12, LocalDate.of(2026, 6, 1)))
    }
    
    @Test
    fun testGarantiStatusGarUtSnart() {
        assertEquals(GarantiStatus.GAR_UT_SNART, garantiStatus(LocalDate.of(2026, 1, 10), 12, LocalDate.of(2026, 12, 11)))
    }
    
    @Test
    fun testGarantiStatusGallerMed31DagarKvar() {
        assertEquals(GarantiStatus.GALLER, garantiStatus(LocalDate.of(2026, 1, 10), 12, LocalDate.of(2026, 12, 10)))
    }
    
    @Test
    fun testGarantiStatusGarUtSnartSistaDagen() {
        assertEquals(GarantiStatus.GAR_UT_SNART, garantiStatus(LocalDate.of(2026, 1, 10), 12, LocalDate.of(2027, 1, 10)))
    }
    
    @Test
    fun testGarantiStatusUtgangen() {
        assertEquals(GarantiStatus.UTGANGEN, garantiStatus(LocalDate.of(2026, 1, 10), 12, LocalDate.of(2027, 1, 11)))
    }
}
