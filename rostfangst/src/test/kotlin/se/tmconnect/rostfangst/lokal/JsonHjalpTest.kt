package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class JsonHjalpTest {

    @Test
    fun testJsonCitera() {
        assertEquals("\"hej\"", jsonCitera("hej"))
        assertEquals("\"a\\\"b\"", jsonCitera("a\"b"))
        assertEquals("\"rad1\\nrad2\"", jsonCitera("rad1\nrad2"))
        assertEquals("\"åäö\"", jsonCitera("åäö"))
        assertEquals("\"\\u0001\"", jsonCitera("\u0001"))
    }

    @Test
    fun testLasJsonStrang() {
        assertEquals("Hej världen", lasJsonStrang("{\"text\": \"Hej världen\"}", "text"))
        assertEquals("x\ny", lasJsonStrang("{\"a\":1,\"text\":\"x\\ny\"}", "text"))
        assertEquals("å", lasJsonStrang("{\"text\":\"\\u00e5\"}", "text"))
        assertNull(lasJsonStrang("{\"text\":5}", "text"))
        assertNull(lasJsonStrang("{}", "text"))
        assertNull(lasJsonStrang("{\"text\":\"oavslutad", "text"))
    }
}
