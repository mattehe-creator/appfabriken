package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Test
import org.junit.Assert.*

class FormularValideringTest {
    
    private val idag = LocalDate.of(2026, 6, 1)
    
    @Test
    fun `test tolkaPrisTillOre`() {
        assertEquals(129950L, tolkaPrisTillOre("1 299,50"))
        assertEquals(129950L, tolkaPrisTillOre("1299,5"))
        assertEquals(129900L, tolkaPrisTillOre("1299"))
        assertEquals(129900L, tolkaPrisTillOre("1 299"))
        assertEquals(1999L, tolkaPrisTillOre("19,99"))
        assertNull(tolkaPrisTillOre(null))
        assertNull(tolkaPrisTillOre(""))
        assertNull(tolkaPrisTillOre("   "))
        assertNull(tolkaPrisTillOre("abc"))
    }
    
    @Test
    fun `test valideraKopFormular`() {
        // Testa VAD_SAKNAS
        assertEquals(listOf(FormularFel.VAD_SAKNAS), 
            valideraKopFormular("  ", 24, LocalDate.of(2026, 1, 1), idag, null))
        
        // Testa GARANTI_OGILTIG
        assertEquals(listOf(FormularFel.GARANTI_OGILTIG), 
            valideraKopFormular("TV", 121, LocalDate.of(2026, 1, 1), idag, null))
        
        // Testa KOPDATUM_FRAMTID
        assertEquals(listOf(FormularFel.KOPDATUM_FRAMTID), 
            valideraKopFormular("TV", 24, LocalDate.of(2026, 7, 1), idag, null))
        
        // Testa PRIS_OGILTIGT
        assertEquals(listOf(FormularFel.PRIS_OGILTIGT), 
            valideraKopFormular("TV", 24, LocalDate.of(2026, 1, 1), idag, "abc"))
        
        // Testa att inga fel returneras vid giltiga värden
        assertEquals(emptyList<FormularFel>(), 
            valideraKopFormular("TV", 24, LocalDate.of(2026, 1, 1), idag, "1 299,50"))
        
        // Testa att flera fel returneras samtidigt
        assertEquals(listOf(FormularFel.VAD_SAKNAS, FormularFel.GARANTI_OGILTIG), 
            valideraKopFormular("  ", 121, LocalDate.of(2026, 1, 1), idag, null))
    }
}
