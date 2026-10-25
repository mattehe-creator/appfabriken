package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.*

class FangstMeddelandeTest {

    @Test
    fun testKodaOchAvkoda() {
        val text = "  hej  "
        val kodad = kodaFangst(text)
        val avkodad = avkodaFangst(kodad)
        assertEquals("hej", avkodad)
    }

    @Test
    fun testTomStrang() {
        val kodad = kodaFangst("   ")
        val avkodad = avkodaFangst(kodad)
        assertNull(avkodad)
    }

    @Test
    fun testForLangStrang() {
        val forLang = CharArray(2001) { 'a' }.joinToString("")
        val kodad = kodaFangst(forLang)
        val avkodad = avkodaFangst(kodad)
        assertNull(avkodad)
    }

    @Test
    fun testSvenskaTecken() {
        val text = "påminn mig"
        val kodad = kodaFangst(text)
        val avkodad = avkodaFangst(kodad)
        assertEquals(text, avkodad)
    }
}
