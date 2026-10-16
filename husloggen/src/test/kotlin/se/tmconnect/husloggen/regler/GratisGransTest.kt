package se.tmconnect.husloggen.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GratisGransTest {
    @Test
    fun kanLaggaTillPostEnligtExemplen() {
        assertTrue(kanLaggaTillPost(14, arPro = false))
        assertFalse(kanLaggaTillPost(15, arPro = false))
        assertTrue(kanLaggaTillPost(15, arPro = true))
        assertTrue(kanLaggaTillPost(500, arPro = true))
    }
}
