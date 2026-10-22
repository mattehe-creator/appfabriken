package se.tmconnect.husloggen.regler

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.Uppgift

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

    @Test
    fun `kommandePerManad grupperar och sorterar enligt exemplen A B C`() {
        val idag = LocalDate.of(2026, 10, 22)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 1L,
                    namn = "Rensa hängrännor",
                    senastGjord = LocalDate.of(2025, 10, 1),
                    startDatum = LocalDate.of(2025, 10, 1),
                    intervallManader = 12,
                ),
                Uppgift(
                    id = 2L,
                    namn = "Byta filter",
                    senastGjord = LocalDate.of(2026, 9, 15),
                    startDatum = LocalDate.of(2026, 9, 15),
                    intervallManader = 3,
                ),
                Uppgift(
                    id = 3L,
                    namn = "Sotning",
                    senastGjord = null,
                    startDatum = LocalDate.of(2027, 3, 1),
                    intervallManader = 24,
                ),
            )

        val result = kommandePerManad(uppgifter, idag)

        val expected =
            mapOf(
                YearMonth.of(2026, 10) to
                    listOf(
                        KommandeJobb(1L, "Rensa hängrännor", LocalDate.of(2026, 10, 1), true),
                    ),
                YearMonth.of(2026, 12) to
                    listOf(
                        KommandeJobb(2L, "Byta filter", LocalDate.of(2026, 12, 15), false),
                    ),
                YearMonth.of(2027, 3) to
                    listOf(
                        KommandeJobb(3L, "Sotning", LocalDate.of(2027, 3, 1), false),
                        KommandeJobb(2L, "Byta filter", LocalDate.of(2027, 3, 15), false),
                    ),
                YearMonth.of(2027, 6) to
                    listOf(
                        KommandeJobb(2L, "Byta filter", LocalDate.of(2027, 6, 15), false),
                    ),
                YearMonth.of(2027, 9) to
                    listOf(
                        KommandeJobb(2L, "Byta filter", LocalDate.of(2027, 9, 15), false),
                    ),
                YearMonth.of(2027, 10) to
                    listOf(
                        KommandeJobb(1L, "Rensa hängrännor", LocalDate.of(2027, 10, 1), false),
                    ),
            )

        assertEquals(expected, result)
    }
}
