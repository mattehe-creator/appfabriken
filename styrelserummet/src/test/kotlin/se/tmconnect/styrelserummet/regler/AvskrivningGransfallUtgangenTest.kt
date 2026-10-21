package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningGransfallUtgangenTest {

    @Test
    fun utgangenPeriodGerNollKr() {
        val k = komponent(100_000, 2010, 10, 0)
        assertEquals(0L, avskrivningFor(k, 2025))
    }

    @Test
    fun anskaffningsvardeNollGerNollKrInomOchUtanforNyttjandeperiod() {
        val k = komponent(0, 2020, 20, 0)
        assertEquals(0L, avskrivningFor(k, 2020))
        assertEquals(0L, avskrivningFor(k, 2045))
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
