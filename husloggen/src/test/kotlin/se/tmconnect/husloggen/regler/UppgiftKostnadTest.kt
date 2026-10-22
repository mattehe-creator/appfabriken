package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class UppgiftKostnadTest {
    @Test
    fun senasteKostnadPerUppgiftEnligtExempel() {
        val poster =
            listOf(
                Post(
                    id = 1L,
                    uppgiftId = 7L,
                    datum = LocalDate.of(2025, 5, 1),
                    vad = "A",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 120_000L,
                ),
                Post(
                    id = 2L,
                    uppgiftId = 7L,
                    datum = LocalDate.of(2024, 5, 1),
                    vad = "B",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 150_000L,
                    rotRutAvdragOre = 30_000L,
                ),
                Post(
                    id = 3L,
                    uppgiftId = 7L,
                    datum = LocalDate.of(2026, 9, 1),
                    vad = "C",
                    typ = PostTyp.UNDERHALL,
                ),
                Post(
                    id = 4L,
                    uppgiftId = 8L,
                    datum = LocalDate.of(2026, 1, 1),
                    vad = "D",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 50_000L,
                ),
                Post(
                    id = 5L,
                    uppgiftId = 8L,
                    datum = LocalDate.of(2026, 1, 1),
                    vad = "E",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 60_000L,
                    rotRutAvdragOre = 10_000L,
                ),
                Post(
                    id = 6L,
                    datum = LocalDate.of(2026, 2, 1),
                    vad = "F",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 99_900L,
                ),
            )
        assertEquals(mapOf(7L to 120_000L, 8L to 50_000L), senasteKostnadPerUppgift(poster))
    }

    @Test
    fun tomListaGerTomMap() {
        assertTrue(senasteKostnadPerUppgift(emptyList()).isEmpty())
    }
}
