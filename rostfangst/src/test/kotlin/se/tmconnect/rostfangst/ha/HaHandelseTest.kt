package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
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
        // Using the exact format that JSONObject produces
        val expected = """{"typ":"PAMINNELSE","titel":"Ring tandläkaren","start":"2026-10-26T08:00:00Z","slut":null,"plats":null,"original":"påminn mig"}"""
        assertEquals(expected, json)
    }

    @Test
    fun testSpecialCharactersInTitle() {
        val forslag = Forslag(
            typ = ForslagTyp.ATT_GORA,
            titel = "Säg \"hej\"\nigen",
            start = null,
            slut = null,
            plats = null,
            svar = null
        )
        val json = bygghaHandelseJson(forslag, "original text")
        // Using the exact format that JSONObject produces
        val expected = """{"typ":"ATT_GORA","titel":"Säg \"hej\"\nigen","start":null,"slut":null,"plats":null,"original":"original text"}"""
        assertEquals(expected, json)
    }
}
