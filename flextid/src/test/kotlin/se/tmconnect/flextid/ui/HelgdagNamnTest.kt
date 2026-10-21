package se.tmconnect.flextid.ui

import org.junit.Assert.assertNotNull
import org.junit.Test
import se.tmconnect.flextid.regler.svenskaAftnar
import se.tmconnect.flextid.regler.svenskaHelgdagar

class HelgdagNamnTest {
    @Test
    fun allaHelgdagarOchAftnarHarStrangresurs() {
        for (ar in listOf(2008, 2026, 2027)) {
            (svenskaHelgdagar(ar) + svenskaAftnar(ar)).forEach { helg ->
                assertNotNull("Saknar strängresurs för ${helg.namn}", helgdagNamnRes(helg.namn))
            }
        }
    }
}
