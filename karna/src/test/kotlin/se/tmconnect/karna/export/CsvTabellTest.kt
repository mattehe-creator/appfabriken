package se.tmconnect.karna.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvTabellTest {
    @Test
    fun prisSomDecimalFormat() {
        assertEquals("50.00", prisSomDecimal(5_000L))
        assertEquals("4999.00", prisSomDecimal(499_900L))
    }

    @Test
    fun csvCellEscaparSemikolonOchFormel() {
        assertEquals("'=1+1", csvCell("=1+1"))
        assertEquals("\"hej;du\"", csvCell("hej;du"))
    }

    @Test
    fun csvDokumentAnvanderCrlf() {
        val doc = csvDokument(listOf("a;b", "c;d"))
        assertTrue(doc.endsWith("\r\n"))
        assertEquals("a;b\r\nc;d\r\n", doc)
    }
}
