package se.tmconnect.rostfangst.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.lang.IllegalArgumentException

class KonverterareTest {

    private val konverterare = Konverterare()

    @Test
    fun `franTyp med giltigt värde returnerar korrekt typ`() {
        assertEquals(ForslagTyp.ATT_GORA, konverterare.franTyp("ATT_GORA"))
        assertEquals(ForslagTyp.HEM, konverterare.franTyp("HEM"))
        assertEquals(ForslagTyp.PAMINNELSE, konverterare.franTyp("PAMINNELSE"))
    }

    @Test
    fun `franTyp med null returnerar null`() {
        assertNull(konverterare.franTyp(null))
    }

    @Test
    fun `tillTyp med giltigt värde returnerar korrekt sträng`() {
        assertEquals("ATT_GORA", konverterare.tillTyp(ForslagTyp.ATT_GORA))
        assertEquals("HEM", konverterare.tillTyp(ForslagTyp.HEM))
        assertEquals("PAMINNELSE", konverterare.tillTyp(ForslagTyp.PAMINNELSE))
    }

    @Test
    fun `tillTyp med null returnerar null`() {
        assertNull(konverterare.tillTyp(null))
    }

    @Test
    fun `rundresa fungerar för alla ForslagTyp-värden`() {
        for (typ in ForslagTyp.entries) {
            assertEquals(typ, konverterare.franTyp(konverterare.tillTyp(typ)))
        }
    }

    @Test
    fun `franTyp med ogiltigt värde kastar IllegalArgumentException`() {
        // Använd assertThrows eller motsvarande för att testa att ett undantag kastas
        try {
            konverterare.franTyp("FINNS_INTE")
            throw AssertionError("Expected IllegalArgumentException was not thrown")
        } catch (e: IllegalArgumentException) {
            // Testet lyckas om undantaget kastas
        }
    }
}
