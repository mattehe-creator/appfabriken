package se.tmconnect.rostfangst.regler

import java.time.Instant
import java.time.Duration
import org.junit.Test
import org.junit.Assert.*

class PaminnelseFordrojningTest {
    
    @Test
    fun `test fordrojning when start is after now`() {
        val now = Instant.parse("2026-10-19T10:00:00Z")
        val start = Instant.parse("2026-10-19T11:00:00Z")
        val result = fordrojning(now, start)
        assertEquals(Duration.ofHours(1), result)
    }
    
    @Test
    fun `test fordrojning when start is before now`() {
        val now = Instant.parse("2026-10-19T11:00:00Z")
        val start = Instant.parse("2026-10-19T10:00:00Z")
        val result = fordrojning(now, start)
        assertNull(result)
    }
    
    @Test
    fun `test fordrojning when start is equal to now`() {
        val now = Instant.parse("2026-10-19T10:00:00Z")
        val start = Instant.parse("2026-10-19T10:00:00Z")
        val result = fordrojning(now, start)
        assertNull(result)
    }
}
