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
    fun loggJobbFelKrypteradPdfInnehallerIdTypOchTidUtanAnvandarmeddelande() {
        val fel = ServerFel.KrypteradPdf
        val rad = loggJobbFel("abc123", fel, 1_700_000_000_000L)
        assertTrue(rad.contains("abc123"))
        assertTrue(rad.contains("KrypteradPdf"))
        assertTrue(rad.contains("1700000000000"))
        assertFalse(rad.contains(felTillMeddelande(fel)))
        assertFalse(rad.contains(".pdf"))
    }

    @Test
    fun loggJobbFelIngenLasbarTextInnehallerIdTypUtanMeddelandeEllerFilnamn() {
        val fel = ServerFel.IngenLasbarText
        val rad = loggJobbFel("jobb-1", fel, 0L)
        assertTrue(rad.contains("jobb-1"))
        assertTrue(rad.contains("IngenLasbarText"))
        assertFalse(rad.contains(felTillMeddelande(fel)))
        assertFalse(rad.contains(".pdf"))
    }
}
