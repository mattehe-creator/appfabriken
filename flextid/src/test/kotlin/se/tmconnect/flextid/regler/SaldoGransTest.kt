package se.tmconnect.flextid.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SaldoGransTest {
    @Test
    fun exempel() {
        assertEquals(SaldoStatus.OVER_TAKET, saldoStatus(2500, 2400, -600))
        assertEquals(-100, minuterTillGrans(2500, 2400, -600))
        assertEquals(SaldoStatus.INOM, saldoStatus(1200, 2400, -600))
        assertEquals(1200, minuterTillGrans(1200, 2400, -600))
        assertEquals(SaldoStatus.UNDER_GOLVET, saldoStatus(-700, 2400, -600))
        assertEquals(-100, minuterTillGrans(-700, 2400, -600))
        assertEquals(SaldoStatus.INOM, saldoStatus(-300, null, -600))
        assertEquals(300, minuterTillGrans(-300, null, -600))
        assertEquals(SaldoStatus.INOM, saldoStatus(500, null, null))
        assertNull(minuterTillGrans(500, null, null))
        assertNull(minuterTillGrans(-300, 2400, null))
    }
}
