package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class ValideraTest {
    
    @Test
    fun `validera returnerar tom lista för giltig komponent`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = 100000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 20,
            restvarde = 10000L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(0, resultat.size)
    }
    
    @Test
    fun `validera returnerar fel för nyttjandeperiod mindre än 1`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = 100000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 0,
            restvarde = 10000L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(1, resultat.size)
        assertEquals("NYTTJANDEPERIOD", resultat[0].kod)
    }
    
    @Test
    fun `validera returnerar fel för negativt anskaffningsvärde`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = -100000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 20,
            restvarde = -150000L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(1, resultat.size)
        assertEquals("NEGATIVT_VARDE", resultat[0].kod)
    }
    
    @Test
    fun `validera returnerar fel för restvärde större än anskaffningsvärde`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = 100000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 20,
            restvarde = 100001L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(1, resultat.size)
        assertEquals("RESTVARDE", resultat[0].kod)
    }
    
    @Test
    fun `validera returnerar fel för anskaffningsår mindre än 1800`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = 100000L,
            anskaffningsar = 1799,
            nyttjandeperiodAr = 20,
            restvarde = 10000L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(1, resultat.size)
        assertEquals("ANSKAFFNINGSAR", resultat[0].kod)
    }
    
    @Test
    fun `validera returnerar fel för anskaffningsår större än nuvarande år + 1`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = 100000L,
            anskaffningsar = 2027,
            nyttjandeperiodAr = 20,
            restvarde = 10000L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(1, resultat.size)
        assertEquals("ANSKAFFNINGSAR", resultat[0].kod)
    }
    
    @Test
    fun `validera returnerar flera fel om flera villkor är fel`() {
        val k = Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = -100000L,
            anskaffningsar = 1799,
            nyttjandeperiodAr = 0,
            restvarde = 100001L,
            kalla = "test",
            sakerhet = Sakerhet.HOG
        )
        
        val resultat = validera(k, 2025)
        assertEquals(4, resultat.size)
    }
}
