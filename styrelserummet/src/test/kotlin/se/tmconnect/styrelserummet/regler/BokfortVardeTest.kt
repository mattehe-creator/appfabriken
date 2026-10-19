package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class BokfortVardeTest {

    @Test
    fun bokfortVardeVidArsslut2025() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(720_000L, bokfortVarde(k, 2025))
    }

    @Test
    fun bokfortVardeForeAnskaffningsar() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(1_200_000L, bokfortVarde(k, 2009))
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
