package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningGransfallRakenskapsarTest {

    @Test
    fun forstaAvskrivningsarGerHelarsbelopp() {
        val k = komponent(90_000, 2025, 3, 0)
        assertEquals(30_000L, avskrivningFor(k, 2025))
    }

    @Test
    fun arForeAnskaffningsarGerNoll() {
        val k = komponent(90_000, 2025, 3, 0)
        assertEquals(0L, avskrivningFor(k, 2024))
    }

    @Test
    fun helarsbeloppSammaForstaTreArINyttiandeperiod() {
        val k = komponent(90_000, 2025, 3, 0)
        val ar2025 = avskrivningFor(k, 2025)
        val ar2026 = avskrivningFor(k, 2026)
        val ar2027 = avskrivningFor(k, 2027)
        assertEquals(30_000L, ar2025)
        assertEquals(30_000L, ar2026)
        assertEquals(30_000L, ar2027)
    }

    private fun komponent(varde: Long, anskaffningsar: Int, period: Int, restvarde: Long) = Komponent(
        namn = "Test",
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = varde,
        anskaffningsar = anskaffningsar,
        nyttjandeperiodAr = period,
        restvarde = restvarde,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )
}
