package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class FondStigandeTest {
    private val atgarder = listOf(PlaneradAtgard(2028, 900_000, "Fönster"))

    @Test
    fun okarTvaProcent() {
        assertEquals(
            listOf(FondAr(2026, 300_000, 0, 300_000), FondAr(2027, 306_000, 0, 606_000), FondAr(2028, 312_120, 900_000, 18_120)),
            fondUtvecklingStigande(atgarder, 2026, 3, 300_000, 20, 0),
        )
    }

    @Test
    fun nollOkningSomVanlig() {
        assertEquals(
            fondUtveckling(atgarder, 2026, 3, 300_000, 0),
            fondUtvecklingStigande(atgarder, 2026, 3, 300_000, 0, 0),
        )
    }
}
