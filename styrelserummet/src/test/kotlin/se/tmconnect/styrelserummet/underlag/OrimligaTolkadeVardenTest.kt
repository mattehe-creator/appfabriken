package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.tolking.Tolkare

class OrimligaTolkadeVardenTest {
    private fun tolkareMed(json: String) =
        object : Tolkare {
            override fun foreslaKomponenter(underhallsplanUtdrag: String, sidor: List<Int>, filnamn: String) =
                Result.success(json)
        }

    @Test
    fun orimligtArOchPeriodErsatts() {
        val json =
            """{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":-2000000000,"nyttjandeperiodAr":0,"kalla":"plan, sida 1","sakerhet":"HOG"}],"anmarkningar":[]}"""
        val utkast =
            byggUtkast(
                underhallsplanText = "Tak 500 000 kr",
                arsredovisningText = "",
                forening = Forening(namn = "Brf Test", byggnadsar = 1970),
                tolkare = tolkareMed(json),
            )
        val tak = utkast.komponenter.single()
        assertEquals(1970, tak.anskaffningsar)
        assertTrue(tak.nyttjandeperiodAr in 1..150)
        assertTrue(utkast.anmarkningar.any { it.contains("anskaffningsåret") })
        assertTrue(utkast.anmarkningar.any { it.contains("nyttjandeperioden") })
    }

    @Test
    fun felkoderBlirLasbara() {
        assertEquals("tjänsten var överbelastad", tolkningsFelText("http_529"))
        assertTrue(tolkningsFelText("svaret_for_langt").contains(TOLKNING_FOR_LANG_TEXT))
        assertEquals("fel från tjänsten (400)", tolkningsFelText("http_400"))
        assertEquals("okänt fel", tolkningsFelText(null))
    }
}
