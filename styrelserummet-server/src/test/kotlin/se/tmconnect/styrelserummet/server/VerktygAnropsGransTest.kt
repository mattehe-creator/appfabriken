package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class VerktygAnropsGransTest {
    
    @Test
    fun testNyGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        assertTrue("Ny gräns ska tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testUnderGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Gör 59 anrop
        for (i in 0 until 59) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue("Under gränsen ska tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testOverGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Gör 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertFalse("Över gränsen ska inte tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testOlikaIp() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Gör 60 anrop från en IP
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        // Anrop från en annan IP ska tillåtas
        assertTrue("Anrop från olika IP ska tillåtas", grans.farAnropa("5.6.7.8", nuMs))
    }
    
    @Test
    fun testGamlaAnrop() {
        val grans = VerktygAnropsGrans()
        val nuMs = 3_600_000L // 1 timme senare
        
        // Gör 60 anrop från början av timmen
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue("Gamla anrop ska räknas inte längre", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testNarGransOverskrids() {
        val grans = VerktygAnropsGrans()
        val nuMs = 3_599_999L // En millisekund innan en timme
        
        // Gör 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertFalse("När gränsen är nådd ska det inte tillåtas", grans.farAnropa("1.2.3.4", nuMs))
    }
}
