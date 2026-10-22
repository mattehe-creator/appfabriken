package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NyckeltalTest {

    @Test
    fun avrundaKvotPositivaExempel() {
        assertEquals(4, avrundaKvot(7, 2))
        assertEquals(2, avrundaKvot(5, 3))
        assertEquals(0, avrundaKvot(1, 3))
        assertEquals(808, avrundaKvot(4_200_000, 5_200))
        assertEquals(0, avrundaKvot(0, 5))
    }

    @Test
    fun avrundaKvotNegativTaljare() {
        assertEquals(-4, avrundaKvot(-7, 2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun avrundaKvotNollNamnare() {
        avrundaKvot(5, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun avrundaKvotNegativNamnare() {
        avrundaKvot(5, -2)
    }

    @Test
    fun ytaOchSkuldsattningPerKvmExempel() {
        val u = NyckeltalUnderlag(
            arsavgifter = 4_200_000,
            rantebarandeSkulder = 38_000_000,
            aretsResultat = -350_000,
            avskrivningar = 1_600_000,
            utrangeringar = 0,
            planeratUnderhall = 450_000,
            justeringar = 0,
            energikostnad = 1_150_000,
            rorelseintakter = 4_600_000,
            kvmBostadsratt = 5_200,
            kvmHyresratt = 300,
        )
        assertEquals(5_500L, totalYta(u))
        assertEquals(808L, arsavgiftPerKvmBostadsratt(u))
        assertEquals(6_909L, skuldsattningPerKvm(u))
        assertEquals(7_308L, skuldsattningPerKvmBostadsratt(u))

        val nollKvm = u.copy(kvmBostadsratt = 0, kvmHyresratt = 0)
        assertEquals(0L, totalYta(nollKvm))
        assertNull(arsavgiftPerKvmBostadsratt(nollKvm))
        assertNull(skuldsattningPerKvm(nollKvm))
        assertNull(skuldsattningPerKvmBostadsratt(nollKvm))
    }

    @Test
    fun justeratResultatOchSparandePerKvmExempel() {
        val u = NyckeltalUnderlag(
            arsavgifter = 4_200_000,
            rantebarandeSkulder = 38_000_000,
            aretsResultat = -350_000,
            avskrivningar = 1_600_000,
            utrangeringar = 0,
            planeratUnderhall = 450_000,
            justeringar = 0,
            energikostnad = 1_150_000,
            rorelseintakter = 4_600_000,
            kvmBostadsratt = 5_200,
            kvmHyresratt = 300,
        )
        assertEquals(1_700_000L, justeratResultat(u))
        assertEquals(309L, sparandePerKvm(u))

        val medJustering = u.copy(justeringar = -2_000_000)
        assertEquals(-300_000L, justeratResultat(medJustering))
        assertEquals(-55L, sparandePerKvm(medJustering))

        val nollKvm = u.copy(kvmBostadsratt = 0, kvmHyresratt = 0)
        assertNull(sparandePerKvm(nollKvm))
    }

    @Test
    fun rantaEnergiOchArsavgiftsandelExempel() {
        val u = NyckeltalUnderlag(
            arsavgifter = 4_200_000,
            rantebarandeSkulder = 38_000_000,
            aretsResultat = -350_000,
            avskrivningar = 1_600_000,
            utrangeringar = 0,
            planeratUnderhall = 450_000,
            justeringar = 0,
            energikostnad = 1_150_000,
            rorelseintakter = 4_600_000,
            kvmBostadsratt = 5_200,
            kvmHyresratt = 300,
        )
        assertEquals(90L, rantekanslighetTiondelar(u))
        assertEquals(209L, energikostnadPerKvm(u))
        assertEquals(913L, arsavgiftsandelTiondelsProcent(u))

        val nollArsavgifter = u.copy(arsavgifter = 0)
        assertNull(rantekanslighetTiondelar(nollArsavgifter))
    }
}
