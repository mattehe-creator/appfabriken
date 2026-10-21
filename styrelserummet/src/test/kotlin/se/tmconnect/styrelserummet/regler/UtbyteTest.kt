package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class UtbyteTest {

    @Test
    fun utrangering300kUtbyte2026() {
        val k = komponent(300_000, 2020, 10, 0)
        assertEquals(120_000L, utrangeringsbelopp(k, 2026))
    }

    @Test
    fun utrangering1_2MUtbyte2026() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(720_000L, utrangeringsbelopp(k, 2026))
    }

    @Test
    fun utrangeringEfterFullAvskrivning() {
        val k = komponent(300_000, 2020, 10, 0)
        assertEquals(0L, utrangeringsbelopp(k, 2035))
    }

    @Test
    fun utrangeringMedRestvarde() {
        val k = komponent(500_000, 2018, 10, 50_000)
        assertEquals(50_000L, utrangeringsbelopp(k, 2030))
    }

    @Test
    fun utrangeringSammaArSomAnskaffning() {
        val k = komponent(300_000, 2020, 10, 0)
        assertEquals(300_000L, utrangeringsbelopp(k, 2020))
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
