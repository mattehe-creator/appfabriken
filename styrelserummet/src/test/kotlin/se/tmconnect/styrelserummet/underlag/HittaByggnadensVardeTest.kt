package se.tmconnect.styrelserummet.underlag

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HittaByggnadensVardeTest {

    @Test
    fun `hittaByggnadensVardeNegativa`() {
        // Testa negativt värde med parentes
        val result1 = hittaByggnadensVarde("Byggnader och mark (2 500) tkr")
        assertEquals(Belopp(-2500000, iTusentals = true), result1)

        // Testa negativt värde med minus
        val result2 = hittaByggnadensVarde("Byggnader och mark -500 tkr")
        assertEquals(Belopp(-500000, iTusentals = true), result2)

        // Testa positivt värde (ska fortfarande fungera)
        val result3 = hittaByggnadensVarde("Byggnader och mark 1 234 tkr")
        assertEquals(Belopp(1234000, iTusentals = true), result3)

        // Testa negativt värde utan tusentals
        val result4 = hittaByggnadensVarde("Byggnader och mark (1 234)")
        assertEquals(Belopp(-1234, false), result4)
    }
}
