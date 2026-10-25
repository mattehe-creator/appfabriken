package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.assertEquals
import java.time.*

class KlockRubrikTest {

    private val zon = ZoneId.of("Europe/Stockholm")
    private val idag = LocalDate.of(2026, 10, 25)

    @Test
    fun testPaminnelseIMorgon() {
        val start = Instant.parse("2026-10-26T06:00:00Z")
        val result = klockRubrik("PAMINNELSE", "ring Eva", start, zon, idag)
        assertEquals("Påminnelse: ring Eva, i morgon 07:00", result)
    }

    @Test
    fun testPaminnelseSammaDag() {
        val start = Instant.parse("2026-10-25T10:30:00Z")
        val result = klockRubrik("PAMINNELSE", "mjölk", start, zon, idag)
        assertEquals("Påminnelse: mjölk, 11:30", result)
    }

    @Test
    fun testPaminnelseAnnadag() {
        val start = Instant.parse("2026-10-27T06:00:00Z")
        val result = klockRubrik("PAMINNELSE", "ring tandläkaren", start, zon, idag)
        assertEquals("Påminnelse: ring tandläkaren, 27/10 07:00", result)
    }

    @Test
    fun testAttGora() {
        val result = klockRubrik("ATT_GORA", "mjölk", null, zon, idag)
        assertEquals("Att göra: mjölk", result)
    }

    @Test
    fun testHem() {
        val result = klockRubrik("HEM", "tänd lampan", null, zon, idag)
        assertEquals("Hem: tänd lampan", result)
    }

    @Test
    fun testOkandTyp() {
        val result = klockRubrik("OKAND", "x", null, zon, idag)
        assertEquals("Förslag: x", result)
    }

    @Test
    fun testKortaTitel() {
        val titel = "a".repeat(50)
        val result = klockRubrik("PAMINNELSE", titel, null, zon, idag)
        assertEquals("Påminnelse: " + "a".repeat(27) + "…", result)
    }

    @Test
    fun testMaxLängd() {
        val start = Instant.parse("2026-10-26T06:00:00Z")
        val titel = "a".repeat(30)
        val result = klockRubrik("PAMINNELSE", titel, start, zon, idag)
        assertEquals(40, result.length)
    }
}
