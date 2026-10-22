package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class StammotidplanTest {

    @Test
    fun testTidplan() {
        val result1 = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 5, 15),
            kallelsefristVeckor = 4..6,
        )
        assertEquals(LocalDate.of(2026, 5, 15), result1.stammodatum)
        assertEquals(LocalDate.of(2026, 6, 30), result1.senasteStammodag)
        assertEquals(LocalDate.of(2026, 4, 3), result1.tidigasteKallelsedag)
        assertEquals(LocalDate.of(2026, 4, 17), result1.senasteKallelsedag)
        assertEquals(LocalDate.of(2026, 4, 24), result1.senasteRevisionTillStyrelsen)
        assertEquals(LocalDate.of(2026, 5, 1), result1.forstaDagArsredovisningTillganglig)
        assertEquals(true, result1.stammaInomLagligFrist)

        val result2 = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 5, 15),
            kallelsefristVeckor = 2..4,
        )
        assertEquals(LocalDate.of(2026, 5, 15), result2.stammodatum)
        assertEquals(LocalDate.of(2026, 6, 30), result2.senasteStammodag)
        assertEquals(LocalDate.of(2026, 4, 3), result2.tidigasteKallelsedag)
        assertEquals(LocalDate.of(2026, 5, 1), result2.senasteKallelsedag)
        assertEquals(LocalDate.of(2026, 4, 24), result2.senasteRevisionTillStyrelsen)
        assertEquals(LocalDate.of(2026, 5, 1), result2.forstaDagArsredovisningTillganglig)
        assertEquals(true, result2.stammaInomLagligFrist)

        val result3 = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 8, 1),
            kallelsefristVeckor = 2..4,
        )
        assertEquals(LocalDate.of(2026, 8, 1), result3.stammodatum)
        assertEquals(LocalDate.of(2026, 6, 30), result3.senasteStammodag)
        assertEquals(LocalDate.of(2026, 6, 20), result3.tidigasteKallelsedag)
        assertEquals(LocalDate.of(2026, 7, 18), result3.senasteKallelsedag)
        assertEquals(LocalDate.of(2026, 7, 11), result3.senasteRevisionTillStyrelsen)
        assertEquals(LocalDate.of(2026, 7, 18), result3.forstaDagArsredovisningTillganglig)
        assertEquals(false, result3.stammaInomLagligFrist)
    }

    @Test
    fun testInvalidKallelsefrist() {
        try {
            tidplan(
                arsslut = LocalDate.of(2025, 12, 31),
                stammodatum = LocalDate.of(2026, 5, 15),
                kallelsefristVeckor = 4..3,
            )
            fail("Expected IllegalArgumentException was not thrown")
        } catch (_: IllegalArgumentException) {
        }

        try {
            tidplan(
                arsslut = LocalDate.of(2025, 12, 31),
                stammodatum = LocalDate.of(2026, 5, 15),
                kallelsefristVeckor = 1..4,
            )
            fail("Expected IllegalArgumentException was not thrown")
        } catch (_: IllegalArgumentException) {
        }

        try {
            tidplan(
                arsslut = LocalDate.of(2025, 12, 31),
                stammodatum = LocalDate.of(2026, 5, 15),
                kallelsefristVeckor = 4..7,
            )
            fail("Expected IllegalArgumentException was not thrown")
        } catch (_: IllegalArgumentException) {
        }
    }
}
