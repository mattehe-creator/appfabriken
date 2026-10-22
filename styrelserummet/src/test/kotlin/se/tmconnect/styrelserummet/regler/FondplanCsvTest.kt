package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

class FondplanCsvTest {

    @Test
    fun ettFondArGerRubrikRadOchUtkastRad() {
        val utveckling = listOf(FondAr(2026, 180_000, 100_000, 380_000))
        val csv = fondplanCsv(utveckling)
        assertEquals(
            "år;avsättning;uttag;fond vid årets slut\n2026;180000;100000;380000\n$UTKAST_RAD\n",
            csv,
        )
    }

    @Test
    fun negativtUtgaendeSkrivsMedMinustecken() {
        val utveckling = listOf(FondAr(2028, 180_000, 900_000, -160_000))
        val csv = fondplanCsv(utveckling)
        assertEquals(
            "år;avsättning;uttag;fond vid årets slut\n2028;180000;900000;-160000\n$UTKAST_RAD\n",
            csv,
        )
    }

    @Test
    fun tomListaHarRubrikOchUtkastRad() {
        val csv = fondplanCsv(emptyList())
        assertEquals(
            "år;avsättning;uttag;fond vid årets slut\n$UTKAST_RAD\n",
            csv,
        )
    }
}
