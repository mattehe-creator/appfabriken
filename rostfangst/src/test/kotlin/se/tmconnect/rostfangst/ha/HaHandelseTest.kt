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
        
        // Kontrollera att JSON innehåller rätt fält
        val obj = org.json.JSONObject(result)
        assertEquals("PAMINNELSE", obj.getString("typ"))
        assertEquals("Ring tandläkaren", obj.getString("titel"))
        assertEquals("2026-10-26T08:00:00Z", obj.getString("start"))
        assertEquals(JSONObject.NULL, obj.get("slut"))
        assertEquals(JSONObject.NULL, obj.get("plats"))
        assertEquals("påminn mig", obj.getString("original"))
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
        
        // Kontrollera att JSON innehåller rätt fält
        val obj = org.json.JSONObject(result)
        assertEquals("ATT_GORA", obj.getString("typ"))
        assertEquals("Säg \"hej\"\nigen", obj.getString("titel"))
        assertEquals(JSONObject.NULL, obj.get("start"))
        assertEquals(JSONObject.NULL, obj.get("slut"))
        assertEquals("Hemmet", obj.getString("plats"))
        assertEquals("köp mjölk", obj.getString("original"))
    }
}
