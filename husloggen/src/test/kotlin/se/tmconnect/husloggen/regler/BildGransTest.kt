package se.tmconnect.husloggen.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BildGransTest {
    @Test
    fun kanLaggaTillBildEnligtExemplen() {
        assertTrue(kanLäggaTillBild(0))
        assertTrue(kanLäggaTillBild(9))
        assertFalse(kanLäggaTillBild(10))
    }
}
