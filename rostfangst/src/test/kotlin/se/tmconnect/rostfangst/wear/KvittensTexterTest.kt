package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.*

class KvittensTexterTest {
    
    @Test
    fun testKvittensText() {
        assertEquals("Skickar…", kvittensText(Overforing.VANTAR))
        assertEquals("Skickat till telefonen", kvittensText(Overforing.SKICKAD))
        assertEquals("Ingen telefon i närheten", kvittensText(Overforing.INGEN_TELEFON))
        assertEquals("Kunde inte skicka", kvittensText(Overforing.FEL))
    }
}
