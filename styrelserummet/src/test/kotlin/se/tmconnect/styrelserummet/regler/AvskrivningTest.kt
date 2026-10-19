package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningTest {
    
    @Test
    fun testAvskrivningForExempel1() {
        val k = komponent(1_200_000, 2010, 40, 0)
        
        // Testa år 2010 till 2049 - alla ska ge 30000
        for (ar in 2010..2049) {
            assertEquals(30000L, avskrivningFor(k, ar))
        }
        
        // Testa år före och efter perioden
        assertEquals(0L, avskrivningFor(k, 2009))
        assertEquals(0L, avskrivningFor(k, 2050))
    }
    
    @Test
    fun testAvskrivningForExempel2() {
        val k = komponent(100_000, 2020, 3, 0)
        
        // Testa år 2020 till 2022
        assertEquals(33333L, avskrivningFor(k, 2020))
        assertEquals(33333L, avskrivningFor(k, 2021))
        assertEquals(33334L, avskrivningFor(k, 2022)) // Sista året tar resten
        
        // Testa år före och efter perioden
        assertEquals(0L, avskrivningFor(k, 2019))
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
