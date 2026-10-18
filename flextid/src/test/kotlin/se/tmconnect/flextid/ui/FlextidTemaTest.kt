package se.tmconnect.flextid.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.karna.tema.TemaLage

/** Kontrollerar att Flextid använder samma tema-modell som övriga appar i :karna. */
class FlextidTemaTest {
    @Test
    fun morktLageFoljerTelefonenNarSystemValt() {
        assertTrue(TemaLage.SYSTEM.arMorkt(telefonenArMork = true))
        assertFalse(TemaLage.SYSTEM.arMorkt(telefonenArMork = false))
    }

    @Test
    fun morktOchLjustArFasta() {
        assertTrue(TemaLage.MORKT.arMorkt(telefonenArMork = false))
        assertFalse(TemaLage.LJUST.arMorkt(telefonenArMork = true))
    }
}
