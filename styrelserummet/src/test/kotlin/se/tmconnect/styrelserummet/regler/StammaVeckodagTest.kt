package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StammaVeckodagTest {

    @Test
    fun datumMedVeckodagSvenskVeckodagGemener() {
        assertEquals("2026-05-31 (söndag)", datumMedVeckodag(LocalDate.of(2026, 5, 31)))
        assertEquals("2026-05-15 (fredag)", datumMedVeckodag(LocalDate.of(2026, 5, 15)))
        assertEquals("2026-06-06 (lördag)", datumMedVeckodag(LocalDate.of(2026, 6, 6)))
    }
}
