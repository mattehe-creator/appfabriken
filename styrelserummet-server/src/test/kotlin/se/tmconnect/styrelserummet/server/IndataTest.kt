package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IndataTest {
    @Test
    fun sakertFilnamnTarBortSokvag() {
        assertEquals("hemlig.pdf", sakertFilnamn("../../hemlig.pdf"))
    }

    @Test
    fun arPdfKannerIgenPrefix() {
        assertTrue(arPdf("%PDF-1.4 rest".encodeToByteArray()))
    }

    @Test
    fun arPdfFalseForTomArray() {
        assertFalse(arPdf(ByteArray(0)))
    }

    @Test
    fun arPdfFalseForFelPrefix() {
        assertFalse(arPdf("NOTPDF".encodeToByteArray()))
    }

    @Test
    fun valideraForeningsnamnTomtGerFel() {
        assertNotNull(valideraForeningsnamn(""))
    }

    @Test
    fun valideraArsslutGiltigtAr() {
        assertEquals(2025, valideraArsslut("2025", nuAr = 2025))
    }

    @Test
    fun valideraArsslutForGammaltAr() {
        assertNull(valideraArsslut("1999", nuAr = 2025))
    }
}
