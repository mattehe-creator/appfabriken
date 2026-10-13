package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TemaLageTest {
    @Test
    fun systemFoljerTelefonen() {
        assertTrue(TemaLage.SYSTEM.arMorkt(telefonenArMork = true))
        assertFalse(TemaLage.SYSTEM.arMorkt(telefonenArMork = false))
    }

    @Test
    fun ljustOchMorktGalIgenomTelefonensVal() {
        assertFalse(TemaLage.LJUST.arMorkt(telefonenArMork = true))
        assertTrue(TemaLage.MORKT.arMorkt(telefonenArMork = false))
    }

    @Test
    fun okantNamnGerSystem() {
        assertEquals(TemaLage.MORKT, TemaLage.franNamn("MORKT"))
        assertEquals(TemaLage.SYSTEM, TemaLage.franNamn(null))
        assertEquals(TemaLage.SYSTEM, TemaLage.franNamn("blå"))
    }
}
