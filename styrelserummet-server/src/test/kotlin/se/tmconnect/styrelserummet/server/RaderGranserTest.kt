package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RaderGranserTest {
    private val ok = KomponentDto("Tak", "TAK", 500_000, 2010, 40, 0, "plan, sida 2", "HOG")

    @Test
    fun rimligaRaderGodkanns() {
        assertTrue(raderInomGranser(listOf(ok), 2025, nuAr = 2025))
        assertTrue(raderInomGranser(emptyList(), 2025, nuAr = 2025))
        assertTrue(raderInomGranser(listOf(ok.copy(anskaffningsar = 2026)), 2025, nuAr = 2025))
    }

    @Test
    fun orimligaVardenAvvisas() {
        val fall =
            listOf(
                ok.copy(anskaffningsar = -2_000_000_000),
                ok.copy(anskaffningsar = 1799),
                ok.copy(anskaffningsar = 2027),
                ok.copy(nyttjandeperiodAr = 0),
                ok.copy(nyttjandeperiodAr = 151),
                ok.copy(anskaffningsvarde = -1),
                ok.copy(anskaffningsvarde = Long.MAX_VALUE),
                ok.copy(restvarde = -1),
                ok.copy(restvarde = 600_000),
                ok.copy(namn = "x".repeat(301)),
                ok.copy(kalla = "x".repeat(301)),
            )
        fall.forEach { assertFalse(it.toString(), raderInomGranser(listOf(it), 2025, nuAr = 2025)) }
    }

    @Test
    fun komponentAnskaffadEfterRakenskapsaretGarAttSpara() {
        // Tolkningen godtar anskaffningsår till och med nästa kalenderår. Ett utkast för
        // räkenskapsåret 2023 med en takrenovering 2025 måste gå att spara oförändrat.
        assertTrue(raderInomGranser(listOf(ok.copy(anskaffningsar = 2025)), 2023, nuAr = 2026))
        assertTrue(raderInomGranser(listOf(ok.copy(anskaffningsar = 2027)), 2023, nuAr = 2026))
        assertFalse(raderInomGranser(listOf(ok.copy(anskaffningsar = 2028)), 2023, nuAr = 2026))
    }

    @Test
    fun forMangaRaderAvvisas() {
        assertTrue(raderInomGranser(List(500) { ok }, 2025, nuAr = 2025))
        assertFalse(raderInomGranser(List(501) { ok }, 2025, nuAr = 2025))
    }
}
