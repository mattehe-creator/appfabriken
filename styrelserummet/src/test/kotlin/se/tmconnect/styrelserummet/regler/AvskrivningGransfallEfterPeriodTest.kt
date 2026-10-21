package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningGransfallEfterPeriodTest {
    
    @Test
    fun langPeriodSistaArOchDagenEfter() {
        val k = komponent(1_200_000, 2010, 40, 0)
        
        // Sista avskrivningsåret ska ge ett belopp > 0
        assertEquals(30000L, avskrivningFor(k, 2049))
        
        // Året efter sista avskrivningsåret ska ge 0
        assertEquals(0L, avskrivningFor(k, 2050))
    }
    
    @Test
    fun kortPeriodSistaArOchDagenEfter() {
        val k = komponent(100_000, 2020, 3, 0)
        
        // Sista avskrivningsåret ska ge 33334 (resten)
        assertEquals(33334L, avskrivningFor(k, 2022))
        
        // Året efter sista avskrivningsåret ska ge 0
        assertEquals(0L, avskrivningFor(k, 2023))
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
