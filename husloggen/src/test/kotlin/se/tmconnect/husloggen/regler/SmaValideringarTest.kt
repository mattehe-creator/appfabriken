package se.tmconnect.husloggen.regler

import org.junit.Test
import org.junit.Assert.*

class SmaValideringarTest {

    @Test
    fun testValideraDokument() {
        // Normal fall
        assertNull(valideraDokument("Kvitto tak", true))
        
        // Titel saknas
        assertEquals(DokumentFel.TitelSaknas, valideraDokument("  ", true))
        assertEquals(DokumentFel.TitelSaknas, valideraDokument("", false))
        
        // Fil saknas
        assertEquals(DokumentFel.FilSaknas, valideraDokument("Manual", false))
    }

    @Test
    fun testValideraMatare() {
        // Normal fall
        assertNull(valideraMatare("El", "kWh"))
        
        // Namn saknas
        assertEquals(MatareFel.NamnSaknas, valideraMatare(" ", "kWh"))
        
        // Enhet saknas
        assertEquals(MatareFel.EnhetSaknas, valideraMatare("El", ""))
    }

    @Test
    fun testTolkaValfrittOreHeltal() {
        // Normal fall
        assertEquals(1500L, tolkaValfrittOreHeltal(" 1500 "))
        assertEquals(0L, tolkaValfrittOreHeltal("0"))
        
        // Tom sträng
        assertNull(tolkaValfrittOreHeltal(""))
        
        // Negativt tal
        assertNull(tolkaValfrittOreHeltal("-1"))
        
        // Ogiltigt format (komma)
        assertNull(tolkaValfrittOreHeltal("12,5"))
    }
}
