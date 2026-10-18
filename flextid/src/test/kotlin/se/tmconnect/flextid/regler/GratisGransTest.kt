package se.tmconnect.flextid.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GratisGransTest {
    @Test
    fun kanLaggaTillDagPostEnligtExemplen() {
        assertTrue(kanLaggaTillDagPost(14, arPro = false))
        assertFalse(kanLaggaTillDagPost(15, arPro = false))
        assertTrue(kanLaggaTillDagPost(15, arPro = true))
        assertTrue(kanLaggaTillDagPost(500, arPro = true))
    }
}
