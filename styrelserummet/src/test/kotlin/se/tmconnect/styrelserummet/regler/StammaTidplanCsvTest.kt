package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class StammaTidplanCsvTest {

    @Test
    fun enRadMedEtikettTest() {
        val csv = tidplanCsv(listOf(StammaTabellRad("Test", "2026-01-01 (torsdag)", "", "")))
        assertEquals(
            """
            etikett;datum;anmärkning;källa
            Test;2026-01-01 (torsdag);;
            $STAMMA_RADGIVNING_RAD

            """.trimIndent(),
            csv,
        )
    }

    @Test
    fun tomListaHarRubrikOchRadgivning() {
        val csv = tidplanCsv(emptyList())
        assertEquals(
            """
            etikett;datum;anmärkning;källa
            $STAMMA_RADGIVNING_RAD

            """.trimIndent(),
            csv,
        )
    }

    @Test
    fun semikolonIAnmarkningCiteras() {
        val csv = tidplanCsv(listOf(StammaTabellRad("Test", "2026-01-01", "a;b", "")))
        assertEquals(
            """
            etikett;datum;anmärkning;källa
            Test;2026-01-01;"a;b";
            $STAMMA_RADGIVNING_RAD

            """.trimIndent(),
            csv,
        )
    }
}
