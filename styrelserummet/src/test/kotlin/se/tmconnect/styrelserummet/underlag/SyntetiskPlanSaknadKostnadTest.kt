package se.tmconnect.styrelserummet.underlag

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
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
    
    private fun skapaTestPDF(namn: String, kostnad: String?): PDDocument {
        val document = PDDocument()
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)
        
        val contentStream = PDPageContentStream(document, page)
        val font: PDFont = PDType1Font.HELVETICA
        
        contentStream.beginText()
        contentStream.setFont(font, 12f)
        contentStream.newLineAtOffset(50f, 750f)
        
        // Lägg till en rad med åtgärd och kostnad
        val text = if (kostnad != null) {
            "$namn: $kostnad tusen kronor"
        } else {
            "$namn: Kostnad saknas i planen"
        }
        
        contentStream.showText(text)
        contentStream.endText()
        contentStream.close()
        
        return document
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
        
        // Skapa en PDF med två åtgärder
        val pdfDoc = skapaTestPDF("Stomme", "6300")
        val pdfDoc2 = skapaTestPDF("Fasad", null)
        
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
