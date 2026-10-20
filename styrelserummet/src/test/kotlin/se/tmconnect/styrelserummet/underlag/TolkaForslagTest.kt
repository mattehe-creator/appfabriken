package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class TolkaForslagTest {
    @Test
    fun tolkaRobustJsonOchTal() {
        val filnamn = "underhallsplan.pdf"
        val enKomponentRaw =
            """{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":"1 200 000","anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL"}],"anmarkningar":[]}"""

        val utanStaket = tolkaForslag(enKomponentRaw, filnamn)
        val medStaket = tolkaForslag(
            """
            ```json
            $enKomponentRaw
            ```
            """.trimIndent(),
            filnamn,
        )
        assertEquals(utanStaket, medStaket)
        assertEquals(1, utanStaket.komponenter.size)
        assertEquals(1200000L, utanStaket.komponenter[0].anskaffningsvarde)

        val textFore = tolkaForslag("""Notera detta {"komponenter":[],"anmarkningar":[]}""", filnamn)
        assertEquals(emptyList<ForslagKomponent>(), textFore.komponenter)
        assertTrue(textFore.anmarkningar.isEmpty())

        val utanMellanslag = enKomponentRaw.replace("\"1 200 000\"", "\"1200000\"")
        val franStrang = tolkaForslag(utanMellanslag, filnamn)
        assertEquals(1200000L, franStrang.komponenter.single().anskaffningsvarde)

        val ogiltigtTal = enKomponentRaw.replace("\"1 200 000\"", "\"abc\"")
        val ogiltigtResultat = tolkaForslag(ogiltigtTal, filnamn)
        assertTrue(
            ogiltigtResultat.anmarkningar.any { it.contains("anskaffningsvarde") && it.contains("heltal") },
        )
        assertEquals(Sakerhet.LAG, ogiltigtResultat.komponenter.single().sakerhet)

        val inteJson = tolkaForslag("inte json alls", filnamn)
        assertEquals(emptyList<ForslagKomponent>(), inteJson.komponenter)
        assertTrue(inteJson.anmarkningar.any { it.contains("Ogiltig JSON") })

        val trailingComma =
            """{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":100,"anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL",},],"anmarkningar":[],}"""
        val medKomma = tolkaForslag(trailingComma, filnamn)
        assertEquals(1, medKomma.komponenter.size)
        assertEquals(Komponentgrupp.TAK, medKomma.komponenter[0].grupp)
    }
}
