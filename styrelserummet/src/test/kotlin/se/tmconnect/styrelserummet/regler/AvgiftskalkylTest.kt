package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class AvgiftskalkylTest {

    private fun huvudexempelUnderlag(ovrigaIntakter: Long = 250_000L) = AvgiftUnderlag(
        driftkostnader = 3_100_000L,
        kapitalkostnader = 1_520_000L,
        avsattning = 600_000L,
        ovrigaIntakter = ovrigaIntakter,
        nuvarandeArsavgifter = 4_200_000L,
        kvmBostadsratt = 5_200L,
    )

    @Test
    fun huvudexemplet() {
        val u = huvudexempelUnderlag()
        assertEquals(4_970_000L, behovdaArsavgifter(u))
        assertEquals(80L, avgiftPerKvmOchManad(4_970_000L, 5_200L))
        assertEquals(183L, avgiftsforandringTiondelsProcent(u))
    }

    @Test
    fun hogaOvrigaIntakterGerBehovNoll() {
        val u = huvudexempelUnderlag(ovrigaIntakter = 10_000_000L)
        assertEquals(0L, behovdaArsavgifter(u))
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativDriftkostnadKastar() {
        behovdaArsavgifter(
            AvgiftUnderlag(
                driftkostnader = -1L,
                kapitalkostnader = 0L,
                avsattning = 0L,
                ovrigaIntakter = 0L,
                nuvarandeArsavgifter = 0L,
                kvmBostadsratt = 100L,
            ),
        )
    }
}
