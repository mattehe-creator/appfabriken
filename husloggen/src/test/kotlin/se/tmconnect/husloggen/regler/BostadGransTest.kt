package se.tmconnect.husloggen.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BostadGransTest {
    @Test
    fun kanLaggaTillBostadEnligtExemplen() {
        assertTrue(kanLaggaTillBostad(2, arPro = false))
        assertFalse(kanLaggaTillBostad(3, arPro = false))
        assertTrue(kanLaggaTillBostad(3, arPro = true))
    }
}
