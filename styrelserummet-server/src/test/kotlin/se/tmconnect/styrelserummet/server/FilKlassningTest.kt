package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilKlassningTest {
    private val arsText = "Förvaltningsberättelse\nResultaträkning\n...\nBalansräkning\n"
    private val planText = "Underhållsplan 2026-2055\nTak 2031 450 000 kr\n"

    @Test
    fun namnetAvgor() {
        assertTrue(arArsredovisning("arsredovisning.pdf", planText))
        assertTrue(arArsredovisning("01-_rsredovisning_2025.pdf", planText))
        assertTrue(arArsredovisning("Bokslut 2025.pdf", ""))
        assertFalse(arArsredovisning("underhallsplan.pdf", arsText))
    }

    @Test
    fun arsISNamnetRackerInte() {
        assertFalse(arArsredovisning("Brf Marsvinet plan.pdf", planText))
        assertFalse(arArsredovisning("Lars plan.pdf", planText))
    }

    @Test
    fun innehalletAvgorNarNamnetInteSagerNagot() {
        assertTrue(arArsredovisning("dokument1.pdf", arsText))
        assertFalse(arArsredovisning("dokument2.pdf", planText))
    }

    @Test
    fun lagringsnamnArUnika() {
        assertEquals("01-plan.pdf", lagringsnamn(1, "plan.pdf"))
        assertEquals("02-plan.pdf", lagringsnamn(2, "mapp/plan.pdf"))
        assertEquals("10-fil.pdf", lagringsnamn(10, ".."))
    }
}
