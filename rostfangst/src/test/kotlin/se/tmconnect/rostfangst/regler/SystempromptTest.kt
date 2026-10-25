package se.tmconnect.rostfangst.regler

import java.time.ZonedDateTime
import java.time.Month
import java.time.ZoneId
import org.junit.Test
import org.junit.Assert

class SystempromptTest {
    
    @Test
    fun testByggSystemprompt() {
        // Skapa en ZonedDateTime för en känd datum och tidzon
        val nu = ZonedDateTime.of(2026, 10, 19, 12, 0, 0, 0, ZoneId.of("Europe/Stockholm"))
        
        // Anropa funktionen
        val result = byggSystemprompt(nu)
        
        // Kontrollera att resultatet innehåller förväntade delar
        Assert.assertTrue(result.contains("måndag"))
        Assert.assertTrue(result.contains("2026-10-19"))
        Assert.assertTrue(result.contains("Europe/Stockholm"))
        Assert.assertTrue(result.contains("Du tolkar korta svenska röstmemo"))
        Assert.assertTrue(result.contains("Schema:"))
    }
}
