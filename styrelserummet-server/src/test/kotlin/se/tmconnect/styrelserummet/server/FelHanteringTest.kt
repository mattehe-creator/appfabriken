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

    @Test
    fun forStorFil() {
        val meddelande = felTillMeddelande(ServerFel.ForStorFil)
        assertTrue(meddelande.contains("25"))
        assertTrue(meddelande.contains("MB") || meddelande.contains("megabyte", ignoreCase = true))
        assertTrue(
            meddelande.contains("tillsammans", ignoreCase = true) ||
                meddelande.contains("total", ignoreCase = true),
        )
        assertFalse(meddelande.contains("Exception", ignoreCase = true))
    }

    @Test
    fun intePdf() {
        val meddelande = felTillMeddelande(ServerFel.IntePdf)
        assertTrue(meddelande.contains("PDF") || meddelande.contains("pdf"))
        assertTrue(
            meddelande.contains("accepteras", ignoreCase = true) ||
                meddelande.contains("Ladda upp"),
        )
    }
}
