package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class BokfortVardeGransfallGranserTest {

    @Test
    fun langPeriodExempelar2025() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(720_000L, bokfortVarde(k, 2025))
    }

    @Test
    fun langPeriodEfterFullAvskrivningAr2055() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(0L, bokfortVarde(k, 2055))
    }

    @Test
    fun langPeriodForeAnskaffningsar() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(1_200_000L, bokfortVarde(k, 2009))
    }

    @Test
    fun langPeriodBokfortVardeMellanNollOchAnskaffningsvarde() {
        val k = komponent(1_200_000, 2010, 40, 0)
        for (arsslut in 2005..2060) {
            assertInGranser(bokfortVarde(k, arsslut), k.anskaffningsvarde)
        }
    }

    @Test
    fun kortPeriodMedAvrundningsrestInomGranser() {
        val k = komponent(100_000, 2020, 3, 0)
        for (arsslut in 2018..2025) {
            assertInGranser(bokfortVarde(k, arsslut), k.anskaffningsvarde)
        }
    }

    private fun assertInGranser(bokfort: Long, anskaffningsvarde: Long) {
        assertTrue("bokfört värde $bokfort ska vara ≥ 0", bokfort >= 0)
        assertTrue(
            "bokfört värde $bokfort ska vara ≤ anskaffningsvärdet $anskaffningsvarde",
            bokfort <= anskaffningsvarde,
        )
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
