package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class BokfortVardeGransfallRestvardeTest {

    private val k = komponent(1_000_000, 2010, 10, 100_000)

    @Test
    fun bokfortVardeSistaAvskrivningsar() {
        assertEquals(100_000L, bokfortVarde(k, 2019))
    }

    @Test
    fun bokfortVardeEfterAvskrivningsperiod() {
        assertEquals(100_000L, bokfortVarde(k, 2025))
    }

    @Test
    fun bokfortVardeMittIPerioden() {
        assertEquals(550_000L, bokfortVarde(k, 2014))
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
