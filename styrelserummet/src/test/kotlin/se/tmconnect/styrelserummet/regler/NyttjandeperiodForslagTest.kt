package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class NyttjandeperiodForslagTest {

    @Test
    fun forslagForGrupp() {
        val tabell = lasNyttjandeperioderFranResurs()

        assertEquals(50, forslagFor(Komponentgrupp.TAK, tabell))
        assertEquals(50, forslagFor(Komponentgrupp.STAMMAR, tabell))
        assertEquals(null, forslagFor(Komponentgrupp.MARK_OCH_GARAGE, tabell))

        val baraTak = listOf(
            NyttjandeperiodRad(Komponentgrupp.TAK, 30..60, 50, 4),
        )
        assertEquals(null, forslagFor(Komponentgrupp.STAMMAR, baraTak))
    }
    
    @Test
    fun arInomIntervall() {
        val tabell = lasNyttjandeperioderFranResurs()
        
        // Rad FASAD (intervallAr = 40..100, forslagAr = 40)
        val fasadRad = tabell.find { it.grupp == Komponentgrupp.FASAD }
        assertEquals(true, arInomIntervall(40, fasadRad!!))
        assertEquals(true, arInomIntervall(100, fasadRad!!))
        assertEquals(false, arInomIntervall(30, fasadRad!!))
        
        // Rad STAMMAR (intervallAr = null, forslagAr = 50)
        val stammarRad = tabell.find { it.grupp == Komponentgrupp.STAMMAR }
        assertEquals(true, arInomIntervall(50, stammarRad!!))
        assertEquals(false, arInomIntervall(49, stammarRad!!))
    }
}
