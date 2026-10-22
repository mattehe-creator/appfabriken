package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class StammotidplanHandlingarTest {

    @Test
    fun testSenasteRevisionTillStyrelsen() {
        val stammodatum = LocalDate.of(2026, 5, 15)
        val expected = LocalDate.of(2026, 4, 24)
        val actual = senasteRevisionTillStyrelsen(stammodatum)
        assertEquals(expected, actual)
    }

    @Test
    fun testForstaDagArsredovisningTillganglig() {
        val stammodatum = LocalDate.of(2026, 5, 15)
        val expected = LocalDate.of(2026, 5, 1)
        val actual = forstaDagArsredovisningTillganglig(stammodatum)
        assertEquals(expected, actual)
    }
}
