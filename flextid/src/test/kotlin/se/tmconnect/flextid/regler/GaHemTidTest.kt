package se.tmconnect.flextid.regler

import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
}
