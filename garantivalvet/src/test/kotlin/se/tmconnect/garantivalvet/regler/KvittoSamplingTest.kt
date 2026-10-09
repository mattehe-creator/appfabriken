package se.tmconnect.garantivalvet.regler

import org.junit.Test
import org.junit.Assert.*

class KvittoSamplingTest {
    
    @Test
    fun `test beraknaInSampleSize`() {
        assertEquals(4, beraknaInSampleSize(8000, 6000))
        assertEquals(2, beraknaInSampleSize(4000, 3000))
        assertEquals(1, beraknaInSampleSize(3999, 3000))
        assertEquals(1, beraknaInSampleSize(1200, 800))
        assertEquals(8, beraknaInSampleSize(16000, 9000))
    }
}
