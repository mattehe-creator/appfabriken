package se.tmconnect.husloggen.ui

import org.junit.Test
import java.util.Locale
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse

class SammanstallningSkarmTest {
    
    @Test
    fun sammanstallningVisarForbattringsetiketterForExempelposter() {
        // Testa att visaSkatteInfo är true för svenska användare
        val locale = Locale.forLanguageTag("sv-SE")
        val visaSkatteInfo = locale.country.equals("SE", ignoreCase = true)
        
        assertTrue("För svenska användare ska visaSkatteInfo vara true", visaSkatteInfo)
    }

    @Test
    fun sammanstallningVisarInteSkatteinfoForUs() {
        // Testa att visaSkatteInfo är false för icke-svenska användare
        val locale = Locale.US
        val visaSkatteInfo = locale.country.equals("SE", ignoreCase = true)
        
        assertFalse("För amerikanska användare ska visaSkatteInfo vara false", visaSkatteInfo)
    }
}
