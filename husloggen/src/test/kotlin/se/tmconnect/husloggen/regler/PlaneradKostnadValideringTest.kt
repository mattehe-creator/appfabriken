package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlaneradKostnadValideringTest {
    private val innevarandeAr = 2026

    @Test
    fun beloppNollAvvisas() {
        assertEquals(
            PlaneradKostnadFel.BELOPP_SAKNAS_ELLER_NOLL,
            valideraPlaneradKostnad("Tak", 0L, 2028, innevarandeAr),
        )
    }

    @Test
    fun arForeInnevarandeAvvisas() {
        assertEquals(
            PlaneradKostnadFel.AR_FORE_INNEVARANDE,
            valideraPlaneradKostnad("Tak", 1_000_000L, 2025, innevarandeAr),
        )
    }

    @Test
    fun giltigPlanGodkanns() {
        assertNull(valideraPlaneradKostnad("Tak", 4_000_000L, 2028, innevarandeAr))
    }
}
