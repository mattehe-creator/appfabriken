package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningHeltalTest {
    
    private fun komponent(varde: Long, anskaffningsar: Int, period: Int, restvarde: Long) = 
        se.tmconnect.styrelserummet.modell.Komponent(
            namn = "Tak",
            grupp = Komponentgrupp.TAK,
            anskaffningsvarde = varde,
            anskaffningsar = anskaffningsar,
            nyttjandeperiodAr = period,
            restvarde = restvarde,
            kalla = "test",
            sakerhet = Sakerhet.MEDEL,
        )
    
    @Test
    fun testExempel1() {
        val k = komponent(1_000_000, 2020, 3, 0)
        
        assertEquals(0L, avskrivningFor(k, 2019))
        assertEquals(333333L, avskrivningFor(k, 2020))
        assertEquals(333333L, avskrivningFor(k, 2021))
        assertEquals(333334L, avskrivningFor(k, 2022))
        assertEquals(0L, avskrivningFor(k, 2023))
    }
    
    @Test
    fun testExempel2() {
        val k = komponent(100, 2020, 3, 0)
        
        assertEquals(33L, avskrivningFor(k, 2020))
        assertEquals(33L, avskrivningFor(k, 2021))
        assertEquals(34L, avskrivningFor(k, 2022))
    }
    
    @Test
    fun testExempel3() {
        val k = komponent(101, 2020, 2, 0)
        
        assertEquals(51L, avskrivningFor(k, 2020))
        assertEquals(50L, avskrivningFor(k, 2021))
    }
    
    @Test
    fun testExempel4() {
        val k = komponent(500_000, 2018, 10, 50_000)
        
        assertEquals(45_000L, avskrivningFor(k, 2018))
        assertEquals(45_000L, avskrivningFor(k, 2027))
    }
    
    @Test
    fun testExempel5() {
        val k = komponent(9_000_000_000_000L, 2020, 7, 0)
        
        // Kontrollera att summan över alla år är korrekt
        var summa = 0L
        for (ar in 2020..2026) {
            summa += avskrivningFor(k, ar)
        }
        assertEquals(9_000_000_000_000L, summa)
    }
    
    @Test
    fun testNyttjandeperiodNoll() {
        val k = komponent(100_000, 2020, 0, 0)
        
        assertEquals(0L, avskrivningFor(k, 2019))
        assertEquals(0L, avskrivningFor(k, 2020))
        assertEquals(0L, avskrivningFor(k, 2021))
    }
}
