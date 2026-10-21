package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningGransfallEnArTest {

    @Test
    fun avskrivningHelaBeloppetSammaArSomAnskaffningRestvardeNoll() {
        val k = komponent(50_000, 2024, 1, 0)

        assertEquals(50_000L, avskrivningFor(k, 2024))
        assertEquals(0L, avskrivningFor(k, 2023))
        assertEquals(0L, avskrivningFor(k, 2025))
    }

    @Test
    fun avskrivningHelaBeloppetSammaArSomAnskaffningMedRestvarde() {
        val k = komponent(50_000, 2024, 1, 5_000)

        assertEquals(45_000L, avskrivningFor(k, 2024))
        assertEquals(0L, avskrivningFor(k, 2023))
        assertEquals(0L, avskrivningFor(k, 2025))
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
