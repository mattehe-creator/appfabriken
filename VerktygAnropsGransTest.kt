package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class VerktygAnropsGransTest {
    
    @Test
    fun testFarAnropaNyIP() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // En ny IP bör kunna anropa
        assertTrue(grans.farAnropa("1.2.3.4", nuMs))
    }
    
    @Test
    fun testFarAnropaInomGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // Registrera 59 anrop
        for (i in 0 until 59) {
            grans.registreraAnrop("1.2.3.4", nuMs + i)
        }
        
        // Borde fortfarande vara tillåtet
        assertTrue(grans.farAnropa("1.2.3.4", nuMs + 100))
    }
    
    @Test
    fun testFarAnropaOverGrans() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // Registrera 60 anrop (max tillåtna)
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", nuMs + i)
        }
        
        // Borde inte längre vara tillåtet
        assertFalse(grans.farAnropa("1.2.3.4", nuMs + 100))
    }
    
    @Test
    fun testFarAnropaDifferentIP() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // Registrera 60 anrop på en IP
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", nuMs + i)
        }
        
        // Samma antal anrop på en annan IP bör fortfarande vara tillåtet
        assertTrue(grans.farAnropa("5.6.7.8", nuMs + 100))
    }
    
    @Test
    fun testFarAnropaTimmeGone() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // Registrera 60 anrop, men låt dem bli för gamla (en timme)
        for (i in 0 until 60) {
            grans.registreraAnrop("1.2.3.4", nuMs + i)
        }
        
        // Efter en timme (3_600_000 ms) bör gamla anrop räknas bort
        assertTrue(grans.farAnropa("1.2.3.4", nuMs + 3_600_000))
    }
    
    @Test
    fun testFarAnropaTimmeGonePartial() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // Registrera 60 anrop, men bara några av dem är gamla
        for (i in 0 until 50) {
            grans.registreraAnrop("1.2.3.4", nuMs + i)
        }
        
        // Låt de första 10 anropen bli gamla
        for (i in 50 until 60) {
            grans.registreraAnrop("1.2.3.4", nuMs + 3_600_000 + i)
        }
        
        // Borde vara tillåtet eftersom bara gamla anrop räknas bort
        assertTrue(grans.farAnropa("1.2.3.4", nuMs + 3_600_000 + 100))
    }
    
    @Test
    fun testFarAnropaExactLimit() {
        val grans = VerktygAnropsGrans()
        val nuMs = 0L
        
        // Registrera exakt 60 anrop
        for (i in 0 until MAX_VERKTYG_ANROP_PER_IP_TIMME) {
            grans.registreraAnrop("1.2.3.4", nuMs + i)
        }
        
        // Borde inte vara tillåtet efter max antal
        assertFalse(grans.farAnropa("1.2.3.4", nuMs + 100))
    }
}
