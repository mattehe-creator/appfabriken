package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.pdf.PdfLasare
import se.tmconnect.styrelserummet.tolking.Tolkare

class SyntetiskPlanTkrTextTest {
    
    private fun fakeTolkare(json: String) = object : Tolkare {
        override fun foreslaKomponenter(
            underhallsplanUtdrag: String,
            sidor: List<Int>,
            filnamn: String,
        ) = Result.success(json)
    }
    
    @Test
    fun planLopandeTextTkrGerUtkast() {
        // Skapa en PDF med löpande text istället för tabell
        val pdfText = "Takbyte planeras 2030, uppskattad kostnad 500 tkr. Stomrenovering 2040, 1 200 tkr."
        
        // Använd PdfLasare för att extrahera texten
        val pdfBytes = createPdfWithText(pdfText)
        val lasare = PdfLasare()
        val extractedText = lasare.lasSomText(pdfBytes).getOrThrow()
        
        // Skapa en stubbad Tolkare som returnerar JSON enligt förväntat
        val json = """
            {"komponenter":[
              {"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2030,"nyttjandeperiodAr":50,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"UPPSKATTNING"},
              {"namn":"Stomme","grupp":"STOMME","anskaffningsvarde":1200000,"anskaffningsar":2040,"nyttjandeperiodAr":100,"kalla":"underhallsplan.pdf, sida 1","sakerhet":"UPPSKATTNING"}
            ],"anmarkningar":[]}
        """.trimIndent()
        
        val tolkare = fakeTolkare(json)
        
        // Skapa en årsredovisning med byggnadsvärde som stämmer
        val ar = "Byggnader och mark 1 700 tkr"
        
        val utkast = byggUtkast(
            extractedText, 
            ar, 
            Forening("Testföreningen", 1975), 
            tolkare, 
            nuAr = 2025
        )
        
        // Kontrollera att vi har två komponenter
        assertEquals(2, utkast.komponenter.size)
        
        // Kontrollera att avstämning stämmer (500 + 1200 = 1700 tkr)
        assertNotNull(utkast.avstamning)
        assertTrue(utkast.avstamning!!.stammer)
        assertEquals(1700000L, utkast.avstamning!!.summa)
        
        // Kontrollera sakerhet för båda komponenterna
        val tak = utkast.komponenter.single { it.grupp == Komponentgrupp.TAK }
        assertEquals(Sakerhet.UPPSKATTNING, tak.sakerhet)
        
        val stomme = utkast.komponenter.single { it.grupp == Komponentgrupp.STOMME }
        assertEquals(Sakerhet.UPPSKATTNING, stomme.sakerhet)
        
        // Kontrollera att inga anmärkningar finns (eftersom båda är uppskattningar)
        assertTrue(utkast.anmarkningar.none { it.contains("differens") })
    }
    
    private fun createPdfWithText(text: String): ByteArray {
        // Använd PDFBox för att skapa en PDF med den angivna texten
        val pdfLasare = PdfLasare()
        return pdfLasare.lasPerSida(createPdfWithTextOnly(text)).getOrThrow().first()
    }
    
    private fun createPdfWithTextOnly(text: String): ByteArray {
        // Denna metod skapar en PDF med endast text, används för att testa extrahering
        return pdfMedText(listOf(text))
    }
    
    private fun pdfMedText(sidor: List<String>): ByteArray {
        // Denna metod är kopierad från PdfLasareTest för att skapa PDF:er i test
        val org.apache.pdfbox.pdmodel.PDDocument
        val org.apache.pdfbox.pdmodel.PDPage
        val org.apache.pdfbox.pdmodel.PDPageContentStream
        val org.apache.pdfbox.pdmodel.font.PDType1Font
        val org.apache.pdfbox.pdmodel.font.Standard14Fonts
        
        PDDocument().use { doc ->
            sidor.forEach { text ->
                val page = PDPage()
                doc.addPage(page)
                if (text.isNotBlank()) {
                    PDPageContentStream(doc, page).use { cs ->
                        cs.beginText()
                        cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                        cs.newLineAtOffset(50f, 700f)
                        cs.showText(text)
                        cs.endText()
                    }
                }
            }
            val baos = java.io.ByteArrayOutputStream()
            doc.save(baos)
            return baos.toByteArray()
        }
    }
}
