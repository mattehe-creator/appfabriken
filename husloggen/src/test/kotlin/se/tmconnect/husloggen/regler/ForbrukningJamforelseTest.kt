package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class ForbrukningJamforelseTest {
    @Test
    fun exempel() {
        assertEquals(
            listOf(ArsJamforelse(2026, 12_540_000, -50), ArsJamforelse(2025, 13_200_000, 100), ArsJamforelse(2024, 12_000_000, null)),
            jamforArsforbrukning(mapOf(2024 to 12_000_000L, 2025 to 13_200_000L, 2026 to 12_540_000L)),
        )
        assertEquals(
            listOf(ArsJamforelse(2025, 1500, null), ArsJamforelse(2023, 1000, null)),
            jamforArsforbrukning(mapOf(2023 to 1000L, 2025 to 1500L)),
        )
        assertEquals(
            listOf(ArsJamforelse(2026, 100, null), ArsJamforelse(2025, 0, null)),
            jamforArsforbrukning(mapOf(2025 to 0L, 2026 to 100L)),
        )
        assertEquals(emptyList<ArsJamforelse>(), jamforArsforbrukning(emptyMap()))
    }
}
