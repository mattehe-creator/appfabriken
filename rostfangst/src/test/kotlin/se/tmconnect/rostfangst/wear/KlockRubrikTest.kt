package se.tmconnect.rostfangst.wear

import org.junit.Test
import java.time.*
import kotlin.test.assertEquals

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
    fun testPaminnelseMedLångTitel() {
        val start = Instant.parse("2026-10-26T06:00:00Z")
        val longTitle = "a".repeat(50)
        val result = klockRubrik("PAMINNELSE", longTitle, start, zon, idag)
        assertEquals("Påminnelse: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa…, i morgon 07:00", result)
    }

    @Test
    fun testAttGoraUtanStart() {
        val result = klockRubrik("ATT_GORA", "mjölk", null, zon, idag)
        assertEquals("Att göra: mjölk", result)
    }

    @Test
    fun testHemTyp() {
        val result = klockRubrik("HEM", "tänd lampan", null, zon, idag)
        assertEquals("Hem: tänd lampan", result)
    }

    @Test
    fun testOkandTyp() {
        val result = klockRubrik("OKAND", "x", null, zon, idag)
        assertEquals("Förslag: x", result)
    }

    @Test
    fun testSammaDag() {
        val start = Instant.parse("2026-10-25T10:30:00Z")
        val result = klockRubrik("PAMINNELSE", "test", start, zon, idag)
        assertEquals("Påminnelse: test, 11:30", result)
    }

    @Test
    fun testFramtid() {
        val start = Instant.parse("2026-10-27T10:30:00Z")
        val result = klockRubrik("PAMINNELSE", "test", start, zon, idag)
        assertEquals("Påminnelse: test, 27/10 11:30", result)
    }

    @Test
    fun testFramtidMedKortTitel() {
        val start = Instant.parse("2026-10-27T10:30:00Z")
        val longTitle = "a".repeat(50)
        val result = klockRubrik("PAMINNELSE", longTitle, start, zon, idag)
        assertEquals("Påminnelse: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa…, 27/10 11:30", result)
    }
}
