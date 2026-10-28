package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class VerktygAnropsGransTest {
    
    @Test
    fun testFarAnropaNyGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        assertTrue("Ny gräns ska tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testFarAnropaInomGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Registrera 59 anrop
        for (i in 0 until 59) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue("Inom gränsen ska tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testFarAnropaUtanGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertFalse("Utan gränsen ska inte tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testFarAnropaDifferentIp() {
        val grans = VerktygAnropsGrans()
        val nuMs = 100L
        
        // Registrera 60 anrop på en IP
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue("Different IP ska tillåta anrop", grans.farAnropa("5.6.7.8", nuMs))
    }
    
    @Test
    fun testFarAnropaGamlaAnrop() {
        val grans = VerktygAnropsGrans()
        val nuMs = 3_600_000L
        
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertTrue("Gamla anrop ska räknas bort, så tillåt anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testFarAnropaNarGransOverskrids() {
        val grans = VerktygAnropsGrans()
        val nuMs = 3_599_999L
        
        // Registrera 60 anrop
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", i.toLong())
        }
        
        assertFalse("När gränsen är nådd ska inte tillåta anrop", grans.farAnropa("1.2.3.4", nuMs))
    }
}
