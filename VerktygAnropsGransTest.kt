package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class VerktygAnropsGransTest {
    
    @Test
    fun testNyGrans() {
        val grans = VerktygAnropsGrans()
        assertTrue("Ny gräns ska tillåta anrop", grans.farAnropa("1.2.3.4", 0))
    }
    
    @Test
    fun testUnderGrans() {
        val grans = VerktygAnropsGrans()
        // Registrera 59 anrop
        for (i in 0 until 59) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        assertTrue("Efter 59 anrop ska tillåta fler anrop", grans.farAnropa("1.2.3.4", 100))
    }
    
    @Test
    fun testOverGrans() {
        val grans = VerktygAnropsGrans()
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        assertFalse("Efter 60 anrop ska neka fler anrop", grans.farAnropa("1.2.3.4", 100))
    }
    
    @Test
    fun testDifferentIp() {
        val grans = VerktygAnropsGrans()
        // Registrera 60 anrop på en IP
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        // Anrop från en annan IP ska tillåtas
        assertTrue("Anrop från annan IP ska tillåtas", grans.farAnropa("5.6.7.8", 100))
    }
    
    @Test
    fun testTimmar() {
        val grans = VerktygAnropsGrans()
        // Registrera 60 anrop vid tiden 0
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        // Efter en timme (3_600_000 ms) ska gamla anrop räknas bort
        assertTrue("Gamla anrop ska räknas bort efter en timme", grans.farAnropa("1.2.3.4", 3_600_000))
    }
    
    @Test
    fun testTimmarGrans() {
        val grans = VerktygAnropsGrans()
        // Registrera 60 anrop vid tiden 0
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        // Efter nästan en timme ska gamla anrop fortfarande räknas bort
        assertFalse("Gamla anrop ska räknas bort nästan en timme sen", grans.farAnropa("1.2.3.4", 3_599_999))
    }
}
