package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import kotlin.test.assertEquals
import org.junit.Test

class StammotidplanGransfallTest {

    @Test
    fun `räkenskapsår slut 31 augusti 2024 ger senaste stämma 28 februari 2025`() {
        val arsslut = LocalDate.of(2024, 8, 31)
        val expected = LocalDate.of(2025, 2, 28)
        val actual = senasteStammodag(arsslut)
        assertEquals(expected, actual)
    }

    @Test
    fun `räkenskapsår slut 31 augusti 2023 ger senaste stämma 29 februari 2024`() {
        val arsslut = LocalDate.of(2023, 8, 31)
        val expected = LocalDate.of(2024, 2, 29)
        val actual = senasteStammodag(arsslut)
        assertEquals(expected, actual)
    }

    @Test
    fun `stämma på exakt senaste stammodag ger stammaInomLagligFrist true`() {
        val arsslut = LocalDate.of(2024, 8, 31)
        val stammodatum = LocalDate.of(2025, 2, 28)
        val tidplan = tidplan(arsslut, stammodatum, 4..4)
        assertEquals(true, tidplan.stammaInomLagligFrist)
    }

    @Test
    fun `stämma dagen efter senaste stammodag ger stammaInomLagligFrist false`() {
        val arsslut = LocalDate.of(2024, 8, 31)
        val stammodatum = LocalDate.of(2025, 3, 1)
        val tidplan = tidplan(arsslut, stammodatum, 4..4)
        assertEquals(false, tidplan.stammaInomLagligFrist)
    }

    @Test
    fun `kallelsefristVeckor 2..2 ger senaste kallelse två veckor före stämman`() {
        val stammodatum = LocalDate.of(2025, 2, 28)
        val expected = LocalDate.of(2025, 2, 14)
        val actual = senasteKallelsedag(stammodatum, 2)
        assertEquals(expected, actual)
    }
}
