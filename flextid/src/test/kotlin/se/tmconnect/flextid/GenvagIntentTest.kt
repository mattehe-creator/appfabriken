package se.tmconnect.flextid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GenvagIntentTest {
    @Test
    fun tolkarStamplaIn() {
        assertEquals(GenvagIntent.Atgard.STAMPLA_IN, GenvagIntent.atgardFranAction(GenvagIntent.ACTION_STAMPLA_IN))
    }

    @Test
    fun tolkarStamplaUt() {
        assertEquals(GenvagIntent.Atgard.STAMPLA_UT, GenvagIntent.atgardFranAction(GenvagIntent.ACTION_STAMPLA_UT))
    }

    @Test
    fun okandActionGerNull() {
        assertNull(GenvagIntent.atgardFranAction("android.intent.action.MAIN"))
        assertNull(GenvagIntent.atgardFranAction(null))
    }
}
