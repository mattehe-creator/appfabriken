package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.tolking.Tolkare

class ByggUtkastTest {
    private fun fakeTolkare(json: String) = object : Tolkare {
        override fun foreslaKomponenter(
            underhallsplanUtdrag: String,
            sidor: List<Int>,
            filnamn: String,
        ) = Result.success(json)
    }

    @Test
    fun byggUtkastStammerMotByggnadensVardeMedFakeTolkare() {
        val json = """
            {"komponenter":[
              {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":6300000,"anskaffningsar":1975,"nyttjandeperiodAr":100,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"HOG"},
              {"namn":"Fasad","grupp":"FASAD","anskaffningsvarde":3700000,"anskaffningsar":1975,"nyttjandeperiodAr":40,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"MEDEL"}
            ],"anmarkningar":[]}
        """.trimIndent()
        val tolkare = fakeTolkare(json)
        val plan = "Underhåll stomme och fasad."
        val ar = """
            Balansräkning
            Byggnader och mark 10 000 tusen kronor
            Ackumulerade avskrivningar 2 000
            Byggnadsår 1975
        """.trimIndent()
        val utkast = byggUtkast(plan, ar, Forening("Testföreningen", 1975), tolkare, nuAr = 2025)
        assertEquals(10_000_000L, utkast.byggnadensVarde)
        assertNotNull(utkast.avstamning)
        assertTrue(utkast.avstamning!!.stammer)
        assertEquals(10_000_000L, utkast.avstamning!!.summa)
        assertEquals(2, utkast.komponenter.size)
        assertTrue(utkast.anmarkningar.none { it.contains("differens") })
    }

    @Test
    fun fordelaSaknadeVardeEnligtTabell() {
        val json = """
            {"komponenter":[
              {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":6300000,"anskaffningsar":1975,"nyttjandeperiodAr":100,"kalla":"plan.pdf, sida 1","sakerhet":"HOG"},
              {"namn":"Fasad","grupp":"FASAD","anskaffningsvarde":null,"anskaffningsar":null,"nyttjandeperiodAr":40,"kalla":"plan.pdf, sida 2","sakerhet":"LAG"}
            ],"anmarkningar":["Kostnad för fasad saknas i planen."]}
        """.trimIndent()
        val tolkare = fakeTolkare(json)
        val ar = "Byggnader 10 000 000"
        val utkast = byggUtkast("plan", ar, Forening("Test", 1975), tolkare, nuAr = 2025)
        val fasad = utkast.komponenter.single { it.grupp == Komponentgrupp.FASAD }
        assertEquals(Sakerhet.UPPSKATTNING, fasad.sakerhet)
        assertEquals(3_700_000L, fasad.anskaffningsvarde)
        assertTrue(utkast.avstamning!!.stammer)
    }
}
