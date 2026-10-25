package se.tmconnect.rostfangst.wear

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class KlockRubrikTest {

    private val zon = ZoneId.of("Europe/Stockholm")
    private val idag = LocalDate.of(2026, 10, 25)

    @Test
    fun paminnelseIMorgon() {
        val start = Instant.parse("2026-10-26T06:00:00Z")
        assertEquals("Påminnelse: ring Eva, i morgon 07:00", klockRubrik("PAMINNELSE", "ring Eva", start, zon, idag))
    }

    @Test
    fun paminnelseSammaDag() {
        val start = Instant.parse("2026-10-25T10:30:00Z")
        assertEquals("Påminnelse: mjölk, 11:30", klockRubrik("PAMINNELSE", "mjölk", start, zon, idag))
    }

    @Test
    fun paminnelseAnnanDag() {
        val start = Instant.parse("2026-10-27T06:00:00Z")
        assertEquals("Påminnelse: ring Eva, 27/10 07:00", klockRubrik("PAMINNELSE", "ring Eva", start, zon, idag))
    }

    @Test
    fun attGoraUtanTid() {
        assertEquals("Att göra: mjölk", klockRubrik("ATT_GORA", "mjölk", null, zon, idag))
    }

    @Test
    fun hemUtanTid() {
        assertEquals("Hem: tänd lampan", klockRubrik("HEM", "tänd lampan", null, zon, idag))
    }

    @Test
    fun okandTypBlirForslag() {
        assertEquals("Förslag: x", klockRubrik("OKAND", "x", null, zon, idag))
    }

    @Test
    fun langTitelKortasTillExaktFyrtioTecken() {
        val resultat = klockRubrik("PAMINNELSE", "a".repeat(50), null, zon, idag)
        assertEquals("Påminnelse: " + "a".repeat(27) + "…", resultat)
        assertEquals(40, resultat.length)
    }

    @Test
    fun langTitelMedTidBehallerTiden() {
        val start = Instant.parse("2026-10-26T06:00:00Z")
        val resultat = klockRubrik("PAMINNELSE", "a".repeat(30), start, zon, idag)
        assertEquals(40, resultat.length)
        assertEquals(true, resultat.endsWith("…, i morgon 07:00"))
    }
}
