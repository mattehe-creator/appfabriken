package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift

class KommandeUnderhallTest {

    @Test
    fun manadsintervallInomEttAr() {
        val idag = LocalDate.of(2026, 6, 1)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 1L,
                    namn = "Filter",
                    senastGjord = LocalDate.of(2026, 5, 1),
                    startDatum = LocalDate.of(2026, 5, 1),
                    intervallManader = 1,
                ),
            )
        val poster =
            listOf(
                Post(
                    id = 10L,
                    uppgiftId = 1L,
                    datum = LocalDate.of(2026, 5, 1),
                    vad = "Filter",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 10_000L,
                ),
            )

        val result = kommandeUnderhall(uppgifter, poster, idag, antalAr = 1)

        assertEquals(2, result.size)
        val y2026 = result.first { it.ar == 2026 }
        assertEquals(7, y2026.rader.size)
        assertEquals(70_000L, y2026.summaOre)
        assertFalse(y2026.harOkandKostnad)
        val y2027 = result.first { it.ar == 2027 }
        assertEquals(5, y2027.rader.size)
        assertEquals(50_000L, y2027.summaOre)
    }

    @Test
    fun arsintervallOverFleraAr() {
        val idag = LocalDate.of(2026, 10, 22)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 2L,
                    namn = "Hängrännor",
                    senastGjord = LocalDate.of(2025, 10, 1),
                    startDatum = LocalDate.of(2025, 10, 1),
                    intervallManader = 12,
                ),
            )
        val poster =
            listOf(
                Post(
                    uppgiftId = 2L,
                    datum = LocalDate.of(2025, 10, 1),
                    vad = "Rensning",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 120_000L,
                ),
            )

        val result = kommandeUnderhall(uppgifter, poster, idag, antalAr = 5)

        val ar = result.map { it.ar }
        assertTrue(ar.contains(2026))
        assertTrue(ar.contains(2027))
        val ar2026 = result.first { it.ar == 2026 }
        assertEquals(LocalDate.of(2026, 10, 1), ar2026.rader.single().datum)
        assertEquals(120_000L, ar2026.summaOre)
    }

    @Test
    fun uppgiftUtanKostnadGerNullOchMarkering() {
        val idag = LocalDate.of(2026, 1, 1)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 3L,
                    namn = "Sotning",
                    senastGjord = null,
                    startDatum = LocalDate.of(2026, 6, 1),
                    intervallManader = 12,
                ),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag)

        val grupp = result.first { it.ar == 2026 }
        assertEquals(null, grupp.rader.single().uppskattadKostnadOre)
        assertEquals(0L, grupp.summaOre)
        assertTrue(grupp.harOkandKostnad)
    }

    @Test
    fun uppgiftUtanIntervallIngarInte() {
        val idag = LocalDate.of(2026, 1, 1)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 4L,
                    namn = "Engång",
                    startDatum = LocalDate.of(2026, 2, 1),
                    intervallManader = 0,
                ),
                Uppgift(
                    id = 5L,
                    namn = "Regelbunden",
                    senastGjord = LocalDate.of(2025, 12, 1),
                    startDatum = LocalDate.of(2025, 12, 1),
                    intervallManader = 6,
                ),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 1)

        assertTrue(result.flatMap { it.rader }.none { it.uppgiftId == 4L })
        assertTrue(result.flatMap { it.rader }.any { it.uppgiftId == 5L })
    }

    @Test
    fun skottdagMedArsintervall() {
        val idag = LocalDate.of(2025, 3, 1)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 6L,
                    namn = "Årlig",
                    senastGjord = LocalDate.of(2024, 2, 29),
                    startDatum = LocalDate.of(2024, 1, 1),
                    intervallManader = 12,
                ),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 2)

        val datum = result.flatMap { it.rader }.map { it.datum }
        assertTrue(datum.contains(LocalDate.of(2025, 2, 28)))
    }

    @Test
    fun antalArEttBegransarSlutdatum() {
        val idag = LocalDate.of(2026, 10, 22)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 7L,
                    namn = "Kvartalsvis",
                    senastGjord = LocalDate.of(2026, 10, 1),
                    startDatum = LocalDate.of(2026, 1, 1),
                    intervallManader = 3,
                ),
            )

        val result = kommandeUnderhall(uppgifter, emptyList(), idag, antalAr = 1)

        val sista = result.flatMap { it.rader }.maxOf { it.datum }
        assertTrue(sista <= LocalDate.of(2027, 10, 21))
        assertTrue(sista >= LocalDate.of(2027, 7, 1))
    }
}
