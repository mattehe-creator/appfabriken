package se.tmconnect.flextid.regler

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GaHemTidTest {
    
    @Test
    fun `test gaHemTid normal cases`() {
        assertEquals(LocalTime.of(16, 30), gaHemTid(LocalTime.of(8, 0), 30, 480))
        assertEquals(LocalTime.of(16, 0), gaHemTid(LocalTime.of(7, 15), 45, 480))
        assertEquals(LocalTime.of(9, 0), gaHemTid(LocalTime.of(9, 0), 0, 0))
        assertEquals(LocalTime.of(16, 0), gaHemTid(LocalTime.of(8, 0), -10, 480))
    }
    
    @Test
    fun `test gaHemTid midnight crossing`() {
        assertNull(gaHemTid(LocalTime.of(20, 0), 30, 240))
        assertNull(gaHemTid(LocalTime.of(15, 30), 30, 480))
    }
    
    @Test
    fun `test gaHemTid with seconds`() {
        assertEquals(LocalTime.of(16, 30), gaHemTid(LocalTime.of(8, 0, 45), 30, 480))
    }
    
    @Test
    fun `test gaHemTidForNollsaldo normal cases`() {
        assertEquals(LocalTime.of(16, 30), gaHemTidForNollsaldo(LocalTime.of(8, 0), 30, 480, 0))
        assertEquals(LocalTime.of(15, 30), gaHemTidForNollsaldo(LocalTime.of(8, 0), 30, 480, 60))
        assertEquals(LocalTime.of(18, 0), gaHemTidForNollsaldo(LocalTime.of(8, 0), 30, 480, -90))
        assertEquals(LocalTime.of(8, 0), gaHemTidForNollsaldo(LocalTime.of(8, 0), 30, 480, 600))
        assertEquals(LocalTime.of(8, 0), gaHemTidForNollsaldo(LocalTime.of(8, 0), 30, 480, 480))
        assertNull(gaHemTidForNollsaldo(LocalTime.of(14, 0), 30, 480, -120))
    }
}
