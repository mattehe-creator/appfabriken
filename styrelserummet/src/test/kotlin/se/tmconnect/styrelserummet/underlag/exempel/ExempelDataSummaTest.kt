package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExempelDataSummaTest {
    
    @Test
    fun testAtgarder() {
        assertEquals(14, EXEMPEL_ATGARDER.size)
        
        val balkonger = EXEMPEL_ATGARDER.find { it.namn == "Balkonger" }
        assertTrue { balkonger?.kostnadKronor == null }
        
        assertEquals(BYGGNADENS_VARDE, summaAtgarderMedKostnad())
        
        val atgardsnamn = EXEMPEL_ATGARDER.map { it.namn }
        assertTrue { atgardsnamn.any { it.contains("tak") } }
        assertTrue { atgardsnamn.any { it.contains("fasad") } }
        assertTrue { atgardsnamn.any { it.contains("fönster") } }
        assertTrue { atgardsnamn.any { it.contains("stammar") } }
        assertTrue { atgardsnamn.any { it.contains("el") } }
        assertTrue { atgardsnamn.any { it.contains("värm") } }
        assertTrue { atgardsnamn.any { it.contains("ventilation") } }
        assertTrue { atgardsnamn.any { it.contains("hiss") } }
        assertTrue { atgardsnamn.any { it.contains("tvättstuga") } }
        assertTrue { atgardsnamn.any { it.contains("mark") } }
    }
}
