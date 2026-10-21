package se.tmconnect.husloggen.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HusNavigeringTest {
    @Test
    fun underflikarMarkerarMer() {
        assertEquals(HusFlik.MER, navigeringsknappFor(HusFlik.PLAN))
        assertEquals(HusFlik.MER, navigeringsknappFor(HusFlik.MATARE))
        assertEquals(HusFlik.MER, navigeringsknappFor(HusFlik.DOKUMENT))
        assertEquals(HusFlik.MER, navigeringsknappFor(HusFlik.MER))
    }

    @Test
    fun huvudflikarMarkerarSigSjalva() {
        assertEquals(HusFlik.ATT_GORA, navigeringsknappFor(HusFlik.ATT_GORA))
        assertEquals(HusFlik.LOGG, navigeringsknappFor(HusFlik.LOGG))
        assertEquals(HusFlik.SAMMANSTALLNING, navigeringsknappFor(HusFlik.SAMMANSTALLNING))
    }

    @Test
    fun allaFlikarNasFranHogstFyraKnappar() {
        val knappar = HusFlik.entries.map { navigeringsknappFor(it) }.toSet()
        assertEquals(4, knappar.size)
        assertTrue(HusFlik.entries.all { navigeringsknappFor(it) in knappar })
    }
}
