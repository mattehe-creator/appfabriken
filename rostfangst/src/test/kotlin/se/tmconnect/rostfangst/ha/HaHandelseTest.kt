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
        
        // Kontrollera att JSON innehåller rätt fält genom att analysera strängen
        assertEquals(
            """{"typ":"PAMINNELSE","titel":"Ring tandläkaren","start":"2026-10-26T08:00:00Z","slut":null,"plats":null,"original":"påminn mig"}""",
            result
        )
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
        
        // Kontrollera att JSON innehåller rätt fält genom att analysera strängen
        assertEquals(
            """{"typ":"ATT_GORA","titel":"Säg \"hej\"\nigen","start":null,"slut":null,"plats":"Hemmet","original":"köp mjölk"}""",
            result
        )
    }
}
