package se.tmconnect.karna.pro

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProLogikTest {
    @Test
    fun tomListaGerIntePro() {
        assertFalse(arProEfter(emptyList(), "pro"))
    }

    @Test
    fun proPurchasedGerPro() {
        val kop = listOf(KopInfo("pro", KopTillstand.PURCHASED))
        assertTrue(arProEfter(kop, "pro"))
    }

    @Test
    fun proPendingGerIntePro() {
        val kop = listOf(KopInfo("pro", KopTillstand.PENDING))
        assertFalse(arProEfter(kop, "pro"))
    }

    @Test
    fun annatProduktIdPurchasedGerIntePro() {
        val kop = listOf(KopInfo("annat", KopTillstand.PURCHASED))
        assertFalse(arProEfter(kop, "pro"))
    }
}
