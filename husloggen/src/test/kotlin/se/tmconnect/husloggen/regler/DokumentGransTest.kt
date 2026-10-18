package se.tmconnect.husloggen.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DokumentGransTest {
    @Test
    fun filstorlekEnligtExemplen() {
        assertTrue(dokumentFilStorlekTillaten(24L * 1024 * 1024))
        assertFalse(dokumentFilStorlekTillaten(26L * 1024 * 1024))
    }

    @Test
    fun mimeTyper() {
        assertTrue(dokumentMimeTillaten("application/pdf"))
        assertTrue(dokumentMimeTillaten("image/jpeg"))
        assertFalse(dokumentMimeTillaten("text/plain"))
    }
}
