package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import kotlin.test.assertEquals

class NyttjandeperiodForslagTest {
    
    @Test
    fun `forslagForGrupp`() {
        val tabell = lasNyttjandeperioderFranResurs()
        
        // Testa TAK som har forslagAr = 50
        assertEquals(50, forslagFor(Komponentgrupp.TAK, tabell))
        
        // Testa STAMMAR som har forslagAr = 50
        assertEquals(50, forslagFor(Komponentgrupp.STAMMAR, tabell))
        
        // Testa MARK_OCH_GARAGE som ska returnera null
        assertEquals(null, forslagFor(Komponentgrupp.MARK_OCH_GARAGE, tabell))
        
        // Testa att en grupp som inte finns i tabellen returnerar null
        assertEquals(null, forslagFor(Komponentgrupp.FONSTER_DORRAR, tabell))
    }
}
