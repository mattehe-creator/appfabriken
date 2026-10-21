package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FelHanteringTest {
    @Test
    fun ingenLasbarTextForklararOchHintarTextbaseradPdf() {
        val meddelande = felTillMeddelande(ServerFel.IngenLasbarText)
        assertTrue(meddelande.contains("läsbar"))
        assertTrue(meddelande.contains("textbaserad", ignoreCase = true))
        assertTrue(meddelande.contains("Ladda upp"))
        assertFalse(meddelande.contains(".pdf"))
        assertFalse(meddelande.contains("Exception", ignoreCase = true))
    }

    @Test
    fun krypteradPdfForklararLosenordOchOlaskt() {
        val meddelande = felTillMeddelande(ServerFel.KrypteradPdf)
        assertTrue(meddelande.contains("lösenord"))
        assertTrue(meddelande.contains("olåst") || meddelande.contains("utan lösenord"))
        assertTrue(meddelande.contains("Ladda upp"))
    }
}
