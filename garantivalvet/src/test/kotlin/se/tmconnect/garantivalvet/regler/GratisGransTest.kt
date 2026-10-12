package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GratisGransTest {
    @Test
    fun gratisanvandareUnderGrans() {
        assertTrue(kanLaggaTill(14, arPro = false))
    }

    @Test
    fun gratisanvandarePaGrans() {
        assertFalse(kanLaggaTill(15, arPro = false))
    }

    @Test
    fun proPaGrans() {
        assertTrue(kanLaggaTill(15, arPro = true))
    }

    @Test
    fun proMedMangaKop() {
        assertTrue(kanLaggaTill(500, arPro = true))
    }

    @Test
    fun skaVisaGratisGransVarningNarGransNarmarSig() {
        assertFalse(skaVisaGratisGransVarning(11, arPro = false))
        assertTrue(skaVisaGratisGransVarning(12, arPro = false))
        assertTrue(skaVisaGratisGransVarning(14, arPro = false))
        assertFalse(skaVisaGratisGransVarning(15, arPro = false))
        assertFalse(skaVisaGratisGransVarning(12, arPro = true))
    }

    @Test
    fun kvittonKvarGratisRaknarRätt() {
        assertEquals(3, kvittonKvarGratis(12))
        assertEquals(1, kvittonKvarGratis(14))
        assertEquals(0, kvittonKvarGratis(15))
        assertEquals(0, kvittonKvarGratis(16))
    }
}
