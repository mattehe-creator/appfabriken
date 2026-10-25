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
        val json = bygghaHandelseJson(forslag, "påminn mig")
        assertEquals(
            """{"typ":"PAMINNELSE","titel":"Ring tandläkaren","start":"2026-10-26T08:00:00Z","slut":null,"plats":null,"original":"påminn mig"}""",
            json
        )
    }

    @Test
    fun testBygghaHandelseJsonMedSpecialTecken() {
        val forslag = Forslag(
            typ = ForslagTyp.ATT_GORA,
            titel = "Säg \"hej\"\nigen",
            start = null,
            slut = null,
            plats = "Hemmet",
            svar = null
        )
        val json = bygghaHandelseJson(forslag, "säg hej igen")
        assertEquals(
            """{"typ":"ATT_GORA","titel":"Säg \"hej\"\nigen","start":null,"slut":null,"plats":"Hemmet","original":"säg hej igen"}""",
            json
        )
    }
}
