package se.tmconnect.rostfangst.wear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FangstMeddelandeTest {
    @Test
    fun roundtrip_trimmar() {
        assertEquals("hej", avkodaFangst(kodaFangst("  hej  ")))
    }

    @Test
    fun blanksteg_ger_null() {
        assertNull(avkodaFangst("   ".toByteArray()))
    }

    @Test
    fun for_lang_text_ger_null() {
        assertNull(avkodaFangst(ByteArray(2001) { 'a'.code.toByte() }))
    }

    @Test
    fun svenska_tecken_roundtrip() {
        assertEquals("påminn mig", avkodaFangst(kodaFangst("påminn mig")))
    }
}
