package se.tmconnect.husloggen.export

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.Uppgift

class SakerhetskopiaUppgiftBostadTest {
    @Test
    fun bostadTillSakerhetskopia() {
        val bostad = Bostad(id = 4, namn = "Villan", adress = "Storgatan 1", anteckning = null)
        val export = bostadTillSakerhetskopia(bostad)
        assertEquals(
            SakerhetskopiaBostad(namn = "Villan", adress = "Storgatan 1", anteckning = null),
            export,
        )
    }

    @Test
    fun sakerhetskopiaBostadTillBostad() {
        val export =
            SakerhetskopiaBostad(namn = " Villan ", adress = " Storgatan 1 ", anteckning = "")
        val bostad = sakerhetskopiaBostadTillBostad(export)
        assertEquals(Bostad(id = 0, namn = "Villan", adress = "Storgatan 1", anteckning = null), bostad)
    }

    @Test
    fun uppgiftTillSakerhetskopia() {
        val uppgift =
            Uppgift(
                id = 3,
                bostadId = 2,
                namn = "Rensa hängrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 4, 1),
                senastGjord = LocalDate.of(2026, 4, 15),
                paminnelse = false,
                anteckning = "Stege i garaget",
            )
        val export = uppgiftTillSakerhetskopia(uppgift, mapOf(2L to 1))
        assertEquals(
            SakerhetskopiaUppgift(
                namn = "Rensa hängrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 4, 1),
                senastGjord = LocalDate.of(2026, 4, 15),
                anteckning = "Stege i garaget",
                bostadIndex = 1,
                paminnelse = false,
            ),
            export,
        )
    }

    @Test
    fun sakerhetskopiaUppgiftTillUppgift() {
        val export =
            SakerhetskopiaUppgift(
                namn = " Byt filter ",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
                senastGjord = null,
                anteckning = "  ",
                bostadIndex = 0,
                paminnelse = true,
            )
        val uppgift = sakerhetskopiaUppgiftTillUppgift(export, bostadId = 4)
        assertEquals(0L, uppgift.id)
        assertEquals(4L, uppgift.bostadId)
        assertEquals("Byt filter", uppgift.namn)
        assertEquals(6, uppgift.intervallManader)
        assertEquals(LocalDate.of(2026, 1, 1), uppgift.startDatum)
        assertNull(uppgift.senastGjord)
        assertTrue(uppgift.paminnelse)
        assertNull(uppgift.anteckning)
    }

    @Test
    fun arSakerhetskopiaUppgiftLikIgnorerarBostadIndexOchPaminnelse() {
        val bas =
            SakerhetskopiaUppgift(
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
            )
        assertTrue(arSakerhetskopiaUppgiftLik(bas, bas.copy(bostadIndex = 3, paminnelse = false)))
    }

    @Test
    fun arSakerhetskopiaUppgiftLikSkillnadSenastGjord() {
        val bas =
            SakerhetskopiaUppgift(
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
            )
        assertFalse(arSakerhetskopiaUppgiftLik(bas, bas.copy(senastGjord = LocalDate.of(2026, 7, 1))))
    }

    @Test
    fun arSakerhetskopiaUppgiftLikSkillnadNamnSkiftlage() {
        val bas =
            SakerhetskopiaUppgift(
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
            )
        assertFalse(arSakerhetskopiaUppgiftLik(bas, bas.copy(namn = "byt filter")))
    }

    @Test
    fun arSakerhetskopiaUppgiftDubblettTrue() {
        val bas =
            SakerhetskopiaUppgift(
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
            )
        val export = bas.copy(bostadIndex = 3, paminnelse = false)
        val befintlig =
            Uppgift(
                id = 1,
                bostadId = 9,
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
                paminnelse = true,
            )
        assertTrue(arSakerhetskopiaUppgiftDubblett(export, befintlig))
    }

    @Test
    fun arSakerhetskopiaUppgiftDubblettFalse() {
        val bas =
            SakerhetskopiaUppgift(
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
            )
        val befintlig =
            Uppgift(
                id = 1,
                bostadId = 9,
                namn = "Byt filter",
                intervallManader = 6,
                startDatum = LocalDate.of(2026, 1, 1),
                anteckning = "x",
            )
        assertFalse(arSakerhetskopiaUppgiftDubblett(bas, befintlig))
    }
}
