package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.*

class FangstMeddelandeTest {

    @Test
    fun testKodaOchAvkoda() {
        val text = "  hej  "
        val encoded = kodaFangst(text)
        val decoded = avkodaFangst(encoded)
        assertEquals("hej", decoded)
    }

    @Test
    fun testTomStrang() {
        val encoded = kodaFangst("   ")
        val decoded = avkodaFangst(encoded)
        assertNull(decoded)
    }

    @Test
    fun testForLangStrang() {
        val longText = "a".repeat(2001)
        val encoded = kodaFangst(longText)
        val decoded = avkodaFangst(encoded)
        assertNull(decoded)
    }

    @Test
    fun testSvenskaTecken() {
        val text = "påminn mig"
        val encoded = kodaFangst(text)
        val decoded = avkodaFangst(encoded)
        assertEquals(text, decoded)
    }
}
