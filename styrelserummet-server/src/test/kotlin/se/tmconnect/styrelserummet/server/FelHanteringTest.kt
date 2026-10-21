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
        assertTrue(meddelande.contains("PDF", ignoreCase = true))
        assertTrue(
            meddelande.contains("accepteras", ignoreCase = true) ||
                meddelande.contains("Ladda upp"),
        )
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
    fun aiTimeoutForklararForseningOchUppmanarForsokIgen() {
        val meddelande = felTillMeddelande(ServerFel.AiTimeout)
        assertTrue(
            meddelande.contains("hann", ignoreCase = true) ||
                meddelande.contains("svara", ignoreCase = true),
        )
        assertTrue(meddelande.contains("Försök igen", ignoreCase = true))
        assertFalse(meddelande.contains("504"))
        assertFalse(meddelande.contains("HTTP", ignoreCase = true))
        assertFalse(meddelande.contains("Exception", ignoreCase = true))
        assertFalse(meddelande.contains("claude", ignoreCase = true))
    }

    @Test
    fun aiOgiltigtSvarForklararKomponenterUnderhallsplanOchForsokIgen() {
        val meddelande = felTillMeddelande(ServerFel.AiOgiltigtSvar)
        assertTrue(
            meddelande.contains("komponent", ignoreCase = true) ||
                meddelande.contains("förslag", ignoreCase = true),
        )
        assertTrue(meddelande.contains("underhållsplan", ignoreCase = true))
        assertTrue(meddelande.contains("Försök igen", ignoreCase = true))
        assertFalse(meddelande.contains("502"))
        assertFalse(meddelande.contains("HTTP", ignoreCase = true))
        assertFalse(meddelande.contains("JSON", ignoreCase = true))
        assertFalse(meddelande.contains("Exception", ignoreCase = true))
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

    @Test
    fun forMangaJobbForklararFemJobbPerTimmeOchNastaForsok() {
        val meddelande = felTillMeddelande(ServerFel.ForMangaJobb(1_700_000_000_000L))
        assertTrue(meddelande.contains("fem jobb per timme"))
        assertTrue(meddelande.contains("klockan"))
        assertFalse(meddelande.contains("Exception", ignoreCase = true))
        assertFalse(meddelande.contains("503"))
        assertFalse(meddelande.contains("HTTP", ignoreCase = true))
    }

    @Test
    fun okantForklararOkantFel() {
        val meddelande = felTillMeddelande(ServerFel.Okant)
        assertTrue(meddelande.contains("okänt fel"))
        assertTrue(meddelande.contains("Försök igen"))
        assertFalse(meddelande.contains("Exception", ignoreCase = true))
        assertFalse(meddelande.contains("500"))
        assertFalse(meddelande.contains("HTTP", ignoreCase = true))
    }
}
