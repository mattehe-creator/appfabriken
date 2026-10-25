package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.Instant

class HaHandelseTest {
    
    @Test
    fun testBygghaHandelseJson() {
        val forslag = Forslag(
            typ = ForslagTyp.PAMINNELSE,
            titel = "Ring tandläkaren",
            start = Instant.parse("2026-10-26T08:00:00Z"),
            slut = null,
            plats = null,
            svar = null
        )
        val original = "påminn mig"
        
        val result = bygghaHandelseJson(forslag, original)
        
        // Kontrollera att JSON-strängen innehåller rätt värden
        assertEquals(true, result.contains("\"typ\":\"PAMINNELSE\""))
        assertEquals(true, result.contains("\"titel\":\"Ring tandläkaren\""))
        assertEquals(true, result.contains("\"start\":\"2026-10-26T08:00:00Z\""))
        assertEquals(true, result.contains("\"slut\":null"))
        assertEquals(true, result.contains("\"plats\":null"))
        assertEquals(true, result.contains("\"original\":\"påminn mig\""))
    }
    
    @Test
    fun testBygghaHandelseJsonMedCitatOchRadbrytning() {
        val forslag = Forslag(
            typ = ForslagTyp.ATT_GORA,
            titel = "Säg \"hej\"\nigen",
            start = null,
            slut = null,
            plats = "Hemmet",
            svar = null
        )
        val original = "köp mjölk"
        
        val result = bygghaHandelseJson(forslag, original)
        
        // Kontrollera att JSON-strängen innehåller rätt värden
        assertEquals(true, result.contains("\"typ\":\"ATT_GORA\""))
        assertEquals(true, result.contains("\"titel\":\"Säg \\\"hej\\\"\\nigen\""))
        assertEquals(true, result.contains("\"start\":null"))
        assertEquals(true, result.contains("\"slut\":null"))
        assertEquals(true, result.contains("\"plats\":\"Hemmet\""))
        assertEquals(true, result.contains("\"original\":\"köp mjölk\""))
    }
}
