package se.tmconnect.flextid.export

import org.junit.Test
import java.time.YearMonth
import org.junit.Assert.assertEquals

class ManadsCsvFilnamnTest {

    @Test
    fun testManadsCsvFilnamn() {
        assertEquals("flextid-2026-03.csv", manadsCsvFilnamn(YearMonth.of(2026, 3)))
        assertEquals("flextid-2026-12.csv", manadsCsvFilnamn(YearMonth.of(2026, 12)))
        assertEquals("flextid-2027-01.csv", manadsCsvFilnamn(YearMonth.of(2027, 1)))
    }
}
