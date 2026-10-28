package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class VerktygAnropsGransTest {
    
    @Test
    fun testNyGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        assertTrue(grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testFemtonAnrop() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Registrera 59 anrop
        for (i in 0 until 59) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue(grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testLagtTillGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertFalse(grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testOlikaIp() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Registrera 60 anrop från en IP
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        // Anrop från en annan IP bör gå bra
        assertTrue(grans.farAnropa("5.6.7.8", nuMs))
    }
    
    @Test
    fun testGamlaAnrop() {
        val grans = VerktygAnropsGrans()
        val nuMs = 3_600_000L // 1 timme senare
        
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue(grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testNagotTidigare() {
        val grans = VerktygAnropsGrans()
        val nuMs = 3_599_999L // 1 ms före en timme
        
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertFalse(grans.farAnropa("1.2.3.4", nuMs))
    }
}
