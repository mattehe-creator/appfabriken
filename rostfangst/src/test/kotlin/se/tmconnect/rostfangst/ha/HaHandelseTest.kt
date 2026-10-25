package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.Instant

class HaHandelseTest {
    @Test
    fun bygger_json_med_start_och_null_for_slut_och_plats() {
        val forslag = Forslag(
            typ = ForslagTyp.PAMINNELSE,
            titel = "Ring tandläkaren",
            start = Instant.parse("2026-10-26T08:00:00Z"),
            slut = null,
            plats = null,
            svar = null
        )
        assertEquals(
            """{"typ":"PAMINNELSE","titel":"Ring tandläkaren","start":"2026-10-26T08:00:00Z","slut":null,"plats":null,"original":"påminn mig"}""",
            bygghaHandelseJson(forslag, "påminn mig")
        )
    }

    @Test
    fun kodar_citattecken_och_radbrytning() {
        val forslag = Forslag(
            typ = ForslagTyp.ATT_GORA,
            titel = "Säg \"hej\"\nigen",
            start = null,
            slut = null,
            plats = "Hemmet",
            svar = null
        )
        assertEquals(
            """{"typ":"ATT_GORA","titel":"Säg \"hej\"\nigen","start":null,"slut":null,"plats":"Hemmet","original":"säg hej igen"}""",
            bygghaHandelseJson(forslag, "säg hej igen")
        )
    }
}
