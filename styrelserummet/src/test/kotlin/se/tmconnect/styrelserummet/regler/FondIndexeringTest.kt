package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class FondIndexeringTest {

    @Test
    fun `indexeratBelopp enligt uppgiftens exempel`() {
        assertEquals(104_040L, indexeratBelopp(100_000, 2028, 2026, 20))
        assertEquals(649_460L, indexeratBelopp(600_000, 2030, 2026, 20))
        assertEquals(100_000L, indexeratBelopp(100_000, 2026, 2026, 20))
        assertEquals(128_010L, indexeratBelopp(100_000, 2036, 2026, 25))
        assertEquals(100_000L, indexeratBelopp(100_000, 2025, 2026, 20))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `indexeratBelopp kastar vid okning over 200`() {
        indexeratBelopp(100_000, 2026, 2026, 201)
    }

    @Test
    fun `indexeradeAtgarder uppräknar belopp och behåller ar och beskrivning`() {
        val atgarder = listOf(
            PlaneradAtgard(2026, 100_000, "Tak"),
            PlaneradAtgard(2028, 600_000, "Fönster"),
            PlaneradAtgard(2030, 200_000, "Ventilation"),
        )
        val basAr = 2026
        val okning = 20

        val indexerade = indexeradeAtgarder(atgarder, basAr, okning)

        assertEquals(3, indexerade.size)
        for (i in atgarder.indices) {
            assertEquals(atgarder[i].ar, indexerade[i].ar)
            assertEquals(atgarder[i].beskrivning, indexerade[i].beskrivning)
            assertEquals(
                indexeratBelopp(atgarder[i].belopp, atgarder[i].ar, basAr, okning),
                indexerade[i].belopp,
            )
        }
    }
}
