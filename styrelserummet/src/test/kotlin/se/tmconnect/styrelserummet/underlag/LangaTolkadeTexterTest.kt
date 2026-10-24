package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class LangaTolkadeTexterTest {
    @Test
    fun forLangtNamnOchKallaKapas() {
        val namn = "Tak".repeat(200)
        val kalla = "plan, sida 2 ".repeat(50)
        val json =
            """{"komponenter":[{"namn":"$namn","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"$kalla","sakerhet":"HOG"}],"anmarkningar":[]}"""
        val k = tolkaForslag(json, "plan.pdf").komponenter.single()
        assertEquals(MAX_TEXT_KOMPONENT, k.namn.length)
        assertEquals(namn.take(MAX_TEXT_KOMPONENT), k.namn)
        assertEquals(MAX_TEXT_KOMPONENT, k.kalla.length)
    }

    @Test
    fun kortTextLamnasOrord() {
        assertEquals("Tak", kapaText("Tak"))
        val exakt = "x".repeat(MAX_TEXT_KOMPONENT)
        assertEquals(exakt, kapaText(exakt))
    }

    @Test
    fun surrogatparDelasInte() {
        // Ett tecken utanför BMP (två UTF-16-enheter) som hamnar på gränsen.
        val text = "x".repeat(MAX_TEXT_KOMPONENT - 1) + "🏠" + "y"
        val kapad = kapaText(text)
        assertEquals(MAX_TEXT_KOMPONENT - 1, kapad.length)
        assertFalse(kapad.last().isHighSurrogate())
    }
}
