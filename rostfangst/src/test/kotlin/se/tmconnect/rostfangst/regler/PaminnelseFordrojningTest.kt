package se.tmconnect.rostfangst.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Duration
import java.time.Instant

class PaminnelseFordrojningTest {

    @Test
    fun `start efter nu ger tidsskillnaden`() {
        val nu = Instant.parse("2023-10-19T10:00:00Z")
        val start = Instant.parse("2023-10-19T11:00:00Z")

        assertEquals(Duration.ofHours(1), fordrojning(nu, start))
    }

    @Test
    fun `start före nu ger null`() {
        val nu = Instant.parse("2023-10-19T11:00:00Z")
        val start = Instant.parse("2023-10-19T10:00:00Z")

        assertNull(fordrojning(nu, start))
    }

    @Test
    fun `start lika med nu ger null`() {
        val nu = Instant.parse("2023-10-19T10:00:00Z")

        assertNull(fordrojning(nu, nu))
    }
}
