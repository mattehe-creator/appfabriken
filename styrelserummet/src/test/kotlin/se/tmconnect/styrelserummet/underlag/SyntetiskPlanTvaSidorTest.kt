package se.tmconnect.styrelserummet.underlag

import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.tolking.Tolkare
import se.tmconnect.styrelserummet.tolking.TolkareResultat
import java.io.ByteArrayOutputStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import kotlin.test.assertEquals

class SyntetiskPlanTvaSidorTest {
    
    @Test
    fun planTvaSidorFortsattning() {
        // Skapa en PDF med två sidor
        val pdfBytes = skapaTvåSidigPdf()
        
        // Skapa en stubb av Tolkare som registrerar sidor och returnerar JSON
        val tolkareStub = object : Tolkare {
            private val anropadeSidorna = mutableListOf<Int>()
            
            override fun foreslaKomponenter(
                underhallsplanUtdrag: String,
                sidor: List<Int>,
                filnamn: String
            ): Result<TolkareResultat> {
                anropadeSidorna.addAll(sidor)
                
                // Returnera JSON baserat på vilken sida som anropas
                val json = when (sidor.size) {
                    1 -> {
                        if (sidor[0] == 1) {
                            """
                            {
                                "komponenter": [
                                    {
                                        "namn": "Ventilation",
                                        "grupp": "VVS",
                                        "anskaffningsvarde": 800000,
                                        "anskaffningsar": 2023,
                                        "nyttjandeperiodAr": 15,
                                        "kalla": "Förslag",
                                        "sakerhet": "LAG"
                                    }
                                ]
                            }
                            """
                        } else {
                            """
                            {
                                "komponenter": [
                                    {
                                        "namn": "Hiss",
                                        "grupp": "ELEKTRISK",
                                        "anskaffningsvarde": 200000,
                                        "anskaffningsar": 2023,
                                        "nyttjandeperiodAr": 20,
                                        "kalla": "Förslag",
                                        "sakerhet": "LAG"
                                    }
                                ]
                            }
                            """
                        }
                    }
                    2 -> {
                        // När båda sidorna anropas samtidigt, returnera båda komponenterna
                        """
                        {
                            "komponenter": [
                                {
                                    "namn": "Ventilation",
                                    "grupp": "VVS",
                                    "anskaffningsvarde": 800000,
                                    "anskaffningsar": 2023,
                                    "nyttjandeperiodAr": 15,
                                    "kalla": "Förslag",
                                    "sakerhet": "LAG"
                                },
                                {
                                    "namn": "Hiss",
                                    "grupp": "ELEKTRISK",
                                    "anskaffningsvarde": 200000,
                                    "anskaffningsar": 2023,
                                    "nyttjandeperiodAr": 20,
                                    "kalla": "Förslag",
                                    "sakerhet": "LAG"
                                }
                            ]
                        }
                        """
                    }
                    else -> {
                        // För annat antal sidor returnera tom lista
                        """
                        {
                            "komponenter": []
                        }
                        """
                    }
                }
                
                return Result.success(TolkareResultat(json))
            }
            
            fun getAnropadeSidorna(): List<Int> = anropadeSidorna.toList()
        }
        
        // Skapa en enkel årsredovisning med byggnadens värde
        val arsredovisningText = """
            Balansräkning
            2023
            Byggnad 1 000 000
            """.trimIndent()
        
        // Anropa byggUtkast
        val forening = Forening(
            namn = "Testförening",
            orgnummer = "1234567890",
            byggnadsar = 2023
        )
        
        val utkast = byggUtkast(
            underhallsplanText = pdfBytes.toString(Charsets.UTF_8),
            arsredovisningText = arsredovisningText,
            forening = forening,
            tolkare = tolkareStub
        )
        
        // Kontrollera att vi har två komponenter
        assertEquals(2, utkast.komponenter.size)
        
        // Kontrollera att båda komponenterna finns
        val ventilation = utkast.komponenter.find { it.namn == "Ventilation" }
        val hiss = utkast.komponenter.find { it.namn == "Hiss" }
        
        assertEquals(800000, ventilation?.anskaffningsvarde)
        assertEquals(200000, hiss?.anskaffningsvarde)
        
        // Kontrollera att avstämning finns (byggnadens värde = summan av komponenterna)
        assertEquals(1000000, utkast.byggnadensVarde)
        
        // Kontrollera att avstämning är korrekt
        assertEquals(true, utkast.avstamning?.stammer)
        
        // Kontrollera att anmärkningar finns för LAG-sakerhet
        val anmarkningar = utkast.anmarkningar
        assertTrue(anmarkningar.any { it.contains("Ventilation") && it.contains("LAG") })
        assertTrue(anmarkningar.any { it.contains("Hiss") && it.contains("LAG") })
    }
    
    private fun skapaTvåSidigPdf(): ByteArray {
        PDDocument().use { doc ->
            // Sida 1
            val page1 = PDPage()
            doc.addPage(page1)
            PDPageContentStream(doc, page1).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText("Ventilation 800 000")
                cs.endText()
            }
            
            // Sida 2
            val page2 = PDPage()
            doc.addPage(page2)
            PDPageContentStream(doc, page2).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText("Hiss 200 000")
                cs.endText()
            }
            
            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
