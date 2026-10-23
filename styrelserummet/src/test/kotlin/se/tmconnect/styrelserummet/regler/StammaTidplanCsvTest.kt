package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StammaTidplanCsvTest {

    @Test
    fun enRadMedRubrikOchRadgivning() {
        val csv = tidplanCsv(listOf(StammaTabellRad("Test", "2026-01-01 (torsdag)", "", "")))
        assertTrue(csv.startsWith("etikett;datum;anmärkning;källa\n"))
        assertTrue(csv.contains("Test;2026-01-01 (torsdag);;\n"))
        assertTrue(csv.endsWith("$STAMMA_RADGIVNING_RAD\n"))
    }

    @Test
    fun tomListaBaraRubrikOchRadgivning() {
        val csv = tidplanCsv(emptyList())
        assertEquals("etikett;datum;anmärkning;källa\n$STAMMA_RADGIVNING_RAD\n", csv)
    }
}
