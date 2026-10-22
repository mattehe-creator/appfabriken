package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class KommandeTest {

    @Test
    fun `forekomster med senastGjord och intervall inom span`() {
        val result =
            forekomster(
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2025, 1, 1),
                3,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 2, 28),
            )
        assertEquals(
            listOf(
                LocalDate.of(2026, 4, 15),
                LocalDate.of(2026, 7, 15),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2027, 1, 15),
            ),
            result,
        )
    }

    @Test
    fun `forekomster utan senastGjord tar startDatum som forsta`() {
        val result =
            forekomster(
                null,
                LocalDate.of(2026, 11, 1),
                12,
                LocalDate.of(2026, 10, 22),
                LocalDate.of(2027, 10, 21),
            )
        assertEquals(listOf(LocalDate.of(2026, 11, 1)), result)
    }

    @Test
    fun `forekomster med forsenad forsta förekomst`() {
        val result =
            forekomster(
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 1, 1),
                12,
                LocalDate.of(2026, 10, 22),
                LocalDate.of(2027, 10, 21),
            )
        assertEquals(
            listOf(LocalDate.of(2026, 9, 1), LocalDate.of(2027, 9, 1)),
            result,
        )
    }

    @Test
    fun `forekomster med manadsslut`() {
        val result =
            forekomster(
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2025, 1, 1),
                1,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 4, 30),
            )
        assertEquals(
            listOf(
                LocalDate.of(2026, 2, 28),
                LocalDate.of(2026, 3, 31),
                LocalDate.of(2026, 4, 30),
            ),
            result,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `forekomster kastar vid intervall mindre an ett`() {
        forekomster(
            LocalDate.of(2026, 1, 15),
            LocalDate.of(2025, 1, 1),
            0,
            LocalDate.of(2026, 3, 1),
            LocalDate.of(2027, 2, 28),
        )
    }
}
