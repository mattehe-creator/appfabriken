package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FelmeddelandenTest {
    @Test
    fun forklara() {
        assertEquals("Ladda upp minst en PDF.", Felmeddelanden.forklara(Felkoder.INGA_PDF))
        assertEquals("Bara PDF-filer kan laddas upp.", Felmeddelanden.forklara(Felkoder.INTE_PDF))
        assertEquals("Jobbet finns inte.", Felmeddelanden.forklara(Felkoder.JOBB_SAKNAS))
        assertEquals("Något gick fel. Försök igen.", Felmeddelanden.forklara("FINNS_EJ"))
        Felkoder.allaKoder().forEach { kod ->
            assertFalse(
                "Meddelande för $kod får inte innehålla ordet felkod",
                Felmeddelanden.forklara(kod).contains("felkod", ignoreCase = true),
            )
        }
    }

    @Test
    fun forStorHandlingMeddelande() {
        val meddelande = Felmeddelanden.forklara(Felkoder.FOR_STOR_HANDLING)
        assertFalse("Meddelandet får inte vara tomt", meddelande.isEmpty())
        assertTrue("Meddelandet ska vara på svenska", meddelande.contains("handlingen är för stor") || meddelande.contains("tolkning"))
        assertFalse("Meddelandet får inte nämna API eller exakt teckengräns", 
            meddelande.contains("API", ignoreCase = true) || 
            meddelande.contains("tecken", ignoreCase = true) ||
            meddelande.contains("400000", ignoreCase = true))
    }
}
