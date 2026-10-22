package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

class AvskrivningsplanCsvTest {

    @Test
    fun tvaPlanArGerRubrikRaderOchUtkastRad() {
        val plan = listOf(
            PlanAr(2026, 60_000, 780_000),
            PlanAr(2027, 60_000, 720_000),
        )
        val csv = avskrivningsplanCsv(plan)
        assertEquals(
            "år;avskrivning;bokfört värde vid årets slut\n2026;60000;780000\n2027;60000;720000\n$UTKAST_RAD\n",
            csv,
        )
    }

    @Test
    fun tomListaHarRubrikOchUtkastRad() {
        val csv = avskrivningsplanCsv(emptyList())
        assertEquals(
            "år;avskrivning;bokfört värde vid årets slut\n$UTKAST_RAD\n",
            csv,
        )
    }
}
