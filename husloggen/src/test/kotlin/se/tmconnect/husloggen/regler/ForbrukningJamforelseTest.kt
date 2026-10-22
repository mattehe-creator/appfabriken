package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class ForbrukningJamforelseTest {
    @Test
    fun treArMedForandringMotForegaendeAr() {
        val perAr =
            mapOf(
                2024 to 12_000_000L,
                2025 to 13_200_000L,
                2026 to 12_540_000L,
            )
        val expected =
            listOf(
                ArsJamforelse(2026, 12_540_000L, -50L),
                ArsJamforelse(2025, 13_200_000L, 100L),
                ArsJamforelse(2024, 12_000_000L, null),
            )
        assertEquals(expected, jamforArsforbrukning(perAr))
    }

    @Test
    fun saknatMellanliggandeArGerNullForandring() {
        val perAr =
            mapOf(
                2023 to 1000L,
                2025 to 1500L,
            )
        val expected =
            listOf(
                ArsJamforelse(2025, 1500L, null),
                ArsJamforelse(2023, 1000L, null),
            )
        assertEquals(expected, jamforArsforbrukning(perAr))
    }

    @Test
    fun foregaendeArNollGerNullForandring() {
        val perAr =
            mapOf(
                2025 to 0L,
                2026 to 100L,
            )
        val expected =
            listOf(
                ArsJamforelse(2026, 100L, null),
                ArsJamforelse(2025, 0L, null),
            )
        assertEquals(expected, jamforArsforbrukning(perAr))
    }

    @Test
    fun tomKartaGerTomLista() {
        assertEquals(emptyList<ArsJamforelse>(), jamforArsforbrukning(emptyMap()))
    }
}
