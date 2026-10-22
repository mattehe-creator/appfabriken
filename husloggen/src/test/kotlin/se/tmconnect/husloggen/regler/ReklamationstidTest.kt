package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReklamationstidTest {
    @Test
    fun tioAr() {
        assertEquals(LocalDate.of(2036, 3, 12), sistaReklamationsdag(LocalDate.of(2026, 3, 12)))
        assertEquals(LocalDate.of(2034, 2, 28), sistaReklamationsdag(LocalDate.of(2024, 2, 29)))
    }

    @Test
    fun barMedUtforareISverige() {
        assertTrue(visaReklamationstid("Byggfirma AB", "SE"))
        assertFalse(visaReklamationstid(null, "SE"))
        assertFalse(visaReklamationstid("  ", "SE"))
        assertFalse(visaReklamationstid("Byggfirma AB", "GB"))
    }
}
