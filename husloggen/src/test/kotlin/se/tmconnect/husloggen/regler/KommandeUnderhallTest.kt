package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift

class KommandeUnderhallTest {

    private fun uppgift(
        id: Long,
        namn: String,
        intervall: Int,
        senast: LocalDate?,
        start: LocalDate = LocalDate.of(2025, 1, 1),
    ) = Uppgift(
        id = id,
        namn = namn,
        intervallManader = intervall,
        startDatum = start,
        senastGjord = senast,
    )

    private fun loggPost(uppgiftId: Long, datum: LocalDate, beloppOre: Long) =
        Post(
            datum = datum,
            vad = "Jobb",
            typ = PostTyp.UNDERHALL,
            beloppOre = beloppOre,
            uppgiftId = uppgiftId,
        )

    @Test
    fun `manadsintervall ger flera tillfall inom samma ar`() {
        val idag = LocalDate.of(2026, 10, 23)
        val uppgifter =
            listOf(
                uppgift(1L, "Filter", 3, LocalDate.of(2026, 7, 1)),
            )
        val poster = listOf(loggPost(1L, LocalDate.of(2026, 6, 1), 50_000L))

        val result = kommandeUnderhall(uppgifter, poster, idag, antalAr = 1)

        val t2026 = result.single { it.ar == 2026 }
        assertEquals(listOf(LocalDate.of(2026, 10, 1)), t2026.rader.map { it.datum })
        val t2027 = result.single { it.ar == 2027 }
        assertEquals(
            listOf(
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 4, 1),
                LocalDate.of(2027, 7, 1),
                LocalDate.of(2027, 10, 1),
            ),
            t2027.rader.map { it.datum },
        )
        assertEquals(250_000L, t2026.summaOre + t2027.summaOre)
        assertFalse(t2026.saknarKostnad)
    }

    @Test
    fun `arsintervall`() {
        val idag = LocalDate.of(2026, 10, 23)
        val uppgifter =
            listOf(
                uppgift(1L, "Tak", 12, LocalDate.of(2025, 10, 1)),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 2)

        assertEquals(
            listOf(2026, 2027, 2028),
            result.map { it.ar },
        )
        assertEquals(LocalDate.of(2026, 10, 1), result[0].rader.single().datum)
        assertEquals(LocalDate.of(2027, 10, 1), result[1].rader.single().datum)
    }

    @Test
    fun `uppgift utan kostnad har null och markering`() {
        val idag = LocalDate.of(2026, 10, 23)
        val uppgifter =
            listOf(
                uppgift(1L, "Med", 12, LocalDate.of(2025, 1, 1)),
                uppgift(2L, "Utan", 12, LocalDate.of(2025, 6, 1)),
            )
        val poster = listOf(loggPost(1L, LocalDate.of(2025, 1, 1), 100_000L))

        val result = kommandeUnderhall(uppgifter, poster, idag, antalAr = 2)
        val grupp2026 = result.first { it.ar == 2026 }

        assertTrue(grupp2026.saknarKostnad)
        assertEquals(100_000L, grupp2026.summaOre)
        val utan = grupp2026.rader.single { it.uppgiftId == 2L }
        assertNull(utan.kostnadOre)
    }

    @Test
    fun `uppgift utan intervall ingar inte`() {
        val idag = LocalDate.of(2026, 10, 23)
        val uppgifter =
            listOf(
                uppgift(1L, "Giltig", 6, LocalDate.of(2026, 1, 1)),
                uppgift(2L, "Ogiltig", 0, LocalDate.of(2026, 1, 1)),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 1)

        assertTrue(result.flatMap { it.rader }.none { it.uppgiftId == 2L })
    }

    @Test
    fun skottdag() {
        val idag = LocalDate.of(2026, 2, 1)
        val uppgifter =
            listOf(
                uppgift(1L, "Månad", 1, LocalDate.of(2026, 1, 31)),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 1)

        val allaDatum = result.flatMap { it.rader }.map { it.datum }
        assertEquals(LocalDate.of(2026, 2, 28), allaDatum.first())
        assertTrue(allaDatum.contains(LocalDate.of(2026, 3, 31)))
    }

    @Test
    fun `antalAr 1 begransar sista datum`() {
        val idag = LocalDate.of(2026, 10, 23)
        val uppgifter =
            listOf(
                uppgift(1L, "A", 3, LocalDate.of(2026, 9, 1)),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 1)

        val allaDatum = result.flatMap { it.rader }.map { it.datum }
        assertTrue(allaDatum.all { !it.isAfter(idag.plusYears(1)) })
        assertEquals(LocalDate.of(2027, 9, 1), allaDatum.last())
    }
}
