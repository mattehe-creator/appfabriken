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
}
