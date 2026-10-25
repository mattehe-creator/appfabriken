package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.*

class FangstMeddelandeTest {

    @Test
    fun testKodaOchAvkodaFangst() {
        val text = "  hej  "
        val encoded = FangstMeddelande.kodaFangst(text)
        val decoded = FangstMeddelande.avkodaFangst(encoded)
        assertEquals("hej", decoded)
    }

    @Test
    fun testAvkodaTomStrang() {
        val result = FangstMeddelande.avkodaFangst("   ".toByteArray())
        assertNull(result)
    }

    @Test
    fun testAvkodaForLangStrang() {
        val longString = "a".repeat(2001)
        val result = FangstMeddelande.avkodaFangst(longString.toByteArray())
        assertNull(result)
    }

    @Test
    fun testSvenskaTecken() {
        val text = "påminn mig"
        val encoded = FangstMeddelande.kodaFangst(text)
        val decoded = FangstMeddelande.avkodaFangst(encoded)
        assertEquals(text, decoded)
    }
}
