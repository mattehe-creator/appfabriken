package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class TolkaForslagGransfallOkandGruppTest {

    private val filnamn = "underhallsplan.pdf"

    @Test
    fun tolkaOkandGruppHopparOverKomponent() {
        val json =
            """{"komponenter":[{"namn":"Okänd","grupp":"OGILTIG","anskaffningsvarde":100,"anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL"}],"anmarkningar":[]}"""
        val resultat = tolkaForslag(json, filnamn)
        assertEquals(0, resultat.komponenter.size)
        assertTrue(
            resultat.anmarkningar.any { it.contains("okänd grupp") && it.contains("OGILTIG") },
        )
    }

    @Test
    fun tolkaBlandadGiltigOchOkandGrupp() {
        val json =
            """{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":100,"anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL"},{"namn":"Okänd","grupp":"OGILTIG","anskaffningsvarde":50,"anskaffningsar":1990,"nyttjandeperiodAr":30,"kalla":"p","sakerhet":"LAG"}],"anmarkningar":[]}"""
        val resultat = tolkaForslag(json, filnamn)
        assertEquals(1, resultat.komponenter.size)
        assertEquals(Komponentgrupp.TAK, resultat.komponenter[0].grupp)
        assertEquals("Tak", resultat.komponenter[0].namn)
        assertTrue(
            resultat.anmarkningar.any { it.contains("okänd grupp") && it.contains("OGILTIG") },
        )
    }
}
