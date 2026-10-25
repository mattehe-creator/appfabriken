package se.tmconnect.rostfangst.regler

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaminnelseFordrojningTest {

    @Test
    fun `should return duration when start is after now`() {
        val now = Instant.parse("2023-10-19T10:00:00Z")
        val start = Instant.parse("2023-10-19T11:00:00Z")
        val expected = java.time.Duration.ofHours(1)

        val result = fordrojning(now, start)

        assertEquals(expected, result)
    }

    @Test
    fun `should return null when start is before now`() {
        val now = Instant.parse("2023-10-19T11:00:00Z")
        val start = Instant.parse("2023-10-19T10:00:00Z")

        val result = fordrojning(now, start)

        assertNull(result)
    }

    @Test
    fun `should return null when start is equal to now`() {
        val now = Instant.parse("2023-10-19T10:00:00Z")
        val start = Instant.parse("2023-10-19T10:00:00Z")

        val result = fordrojning(now, start)

        assertNull(result)
    }
}
