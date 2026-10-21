package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.*
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.tolking.Tolkare

class SyntetiskPlanSaknadKostnadTest {
    
    private fun fakeTolkare(json: String) = object : Tolkare {
        override fun foreslaKomponenter(
            underhallsplanUtdrag: String,
            sidor: List<Int>,
            filnamn: String,
        ) = Result.success(json)
    }
    
    @Test
    fun planSaknadKostnadGerUppskattning() {
        // Skapa en stubb-JSON med en komponent som saknar kostnad
        val json = """
            {"komponenter":[
              {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":6300000,"anskaffningsar":1975,"nyttjandeperiodAr":100,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"HOG"},
              {"namn":"Fasad","grupp":"FASAD","anskaffningsvarde":null,"anskaffningsar":null,"nyttjandeperiodAr":40,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"LAG"}
            ],"anmarkningar":["Kostnad saknas i planen."]}
        """.trimIndent()
        
        val tolkare = fakeTolkare(json)
        
        // Använd en enkel årsredovisning som ger rätt byggnadsvärde
        val ar = "Byggnader 10 000 000"
        
        // Kör byggUtkast
        val utkast = byggUtkast("underhallsplan", ar, Forening("Testföreningen", 1975), tolkare, nuAr = 2025)
        
        // Kontrollera att vi har två komponenter
        assertEquals(2, utkast.komponenter.size)
        
        // Hitta fasaden och kontrollera att den har rätt sakerhet
        val fasad = utkast.komponenter.single { it.grupp == Komponentgrupp.FASAD }
        assertEquals(Sakerhet.UPPSKATTNING, fasad.sakerhet)
        
        // Kontrollera att avstämningen stämmer (stammer)
        assertNotNull(utkast.avstamning)
        assertTrue(utkast.avstamning!!.stammer)
        assertEquals(10_000_000L, utkast.avstamning!!.summa)
        
        // Kontrollera att anmärkningen finns
        assertTrue(utkast.anmarkningar.any { it.contains("saknas") })
    }
}
