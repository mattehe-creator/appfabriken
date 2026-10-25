package se.tmconnect.rostfangst.regler

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Test
import org.junit.Assert

class SystempromptTest {
    
    @Test
    fun testByggSystemprompt() {
        // Skapa en fast datum och tid för testet
        val testDatum = ZonedDateTime.of(2026, 10, 19, 12, 0, 0, 0, ZoneId.of("Europe/Stockholm"))
        
        val result = byggSystemprompt(testDatum)
        
        // Kontrollera att resultatet innehåller förväntade värden
        Assert.assertTrue(result.contains("måndag"))
        Assert.assertTrue(result.contains("2026-10-19"))
        Assert.assertTrue(result.contains("Europe/Stockholm"))
        Assert.assertTrue(result.contains("Du tolkar korta svenska röstmemo"))
        Assert.assertTrue(result.contains("Schema:"))
    }
}
