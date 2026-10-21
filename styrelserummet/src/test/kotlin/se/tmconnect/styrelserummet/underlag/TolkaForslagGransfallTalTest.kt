package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Sakerhet

class TolkaForslagGransfallTalTest {
    private val filnamn = "underhallsplan.pdf"

    private val enKomponent =
        """{"namn":"Tak","grupp":"TAK","anskaffningsvarde":1200000,"anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL"}"""

    @Test
    fun tolkaDubblaKomponenterMedSammaNamn() {
        val json =
            """{"komponenter":[$enKomponent,$enKomponent],"anmarkningar":[]}"""
        val resultat = tolkaForslag(json, filnamn)
        assertEquals(2, resultat.komponenter.size)
        assertEquals("Tak", resultat.komponenter[0].namn)
        assertEquals("Tak", resultat.komponenter[1].namn)
    }

    @Test
    fun tolkaAnskaffningsarSomStrang() {
        val komponent = enKomponent.replace("\"anskaffningsar\":2000", "\"anskaffningsar\":\"2015\"")
        val json = """{"komponenter":[$komponent],"anmarkningar":[]}"""
        val resultat = tolkaForslag(json, filnamn)
        assertEquals(2015, resultat.komponenter.single().anskaffningsar)
    }

    @Test
    fun tolkaNegativtAnskaffningsvardeGerAnmarkningOchLagSakerhet() {
        val komponent = enKomponent.replace("\"anskaffningsvarde\":1200000", "\"anskaffningsvarde\":-500")
        val json = """{"komponenter":[$komponent],"anmarkningar":[]}"""
        val resultat = tolkaForslag(json, filnamn)
        assertTrue(
            resultat.anmarkningar.any {
                it.contains("anskaffningsvarde") && it.contains("negativ", ignoreCase = true)
            },
        )
        assertEquals(Sakerhet.LAG, resultat.komponenter.single().sakerhet)
    }
}
