package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.*

class UtforMeddelandeTest {

    @Test
    fun testKodaUtfor() {
        val data = kodaUtfor(99)
        assertArrayEquals("99".toByteArray(), data)
    }

    @Test
    fun testAvkodaUtfor() {
        assertEquals(99L, avkodaUtfor(kodaUtfor(99)))
        assertNull(avkodaUtfor("abc".toByteArray()))
        assertNull(avkodaUtfor(kodaUtfor(0)))
        assertNull(avkodaUtfor(ByteArray(0)))
    }
}
