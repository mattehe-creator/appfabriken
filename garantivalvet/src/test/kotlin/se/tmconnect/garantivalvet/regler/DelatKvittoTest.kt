package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DelatKvittoTest {
    @Test
    fun bildOchPdfKanTasEmot() {
        assertTrue(arDelbartKvitto("image/jpeg"))
        assertTrue(arDelbartKvitto("image/png"))
        assertTrue(arDelbartKvitto("application/pdf"))
        assertFalse(arDelbartKvitto("text/plain"))
        assertFalse(arDelbartKvitto("video/mp4"))
        assertFalse(arDelbartKvitto(null))
    }

    @Test
    fun pdfSkalasTillMalbredd() {
        // A4 i punkter, 595 x 842
        val (b, h) = beraknaPdfStorlek(595, 842)
        assertEquals(PDF_MAL_BREDD, b)
        assertEquals(2_264, h) // 842 * 1600 / 595, avrundat nedåt
    }

    @Test
    fun langtKvittoBegransasIHojd() {
        val (b, h) = beraknaPdfStorlek(226, 2_000)
        assertEquals(PDF_MAX_HOJD, h)
        assertTrue(b < PDF_MAL_BREDD)
        assertEquals(361, b) // 226 * 3200 / 2000
    }

    @Test
    fun ogiltigStorlekGerKvadratFaIngenKrasch() {
        assertEquals(PDF_MAL_BREDD to PDF_MAL_BREDD, beraknaPdfStorlek(0, 100))
        assertEquals(PDF_MAL_BREDD to PDF_MAL_BREDD, beraknaPdfStorlek(100, -1))
    }
}
