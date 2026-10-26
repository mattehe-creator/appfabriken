package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class JsonHjalpTest {
    
    @Test
    fun testJsonCitera() {
        assertEquals("\"hej\"", jsonCitera("hej"))
        assertEquals("\"a\\\"b\"", jsonCitera("a\"b"))
        assertEquals("\"rad1\\nrad2\"", jsonCitera("rad1\nrad2"))
        assertEquals("\"åäö\"", jsonCitera("åäö"))
        
        // Testa styrtecken
        assertEquals("\"\\u0001\"", jsonCitera("\u0001"))
        assertEquals("\"\\u0002\"", jsonCitera("\u0002"))
        assertEquals("\"\\u0003\"", jsonCitera("\u0003"))
        
        // Testa backslash
        assertEquals("\"\\\\\"", jsonCitera("\\"))
    }
    
    @Test
    fun testLasJsonStrang() {
        assertEquals("Hej världen", lasJsonStrang("{\"text\": \"Hej världen\"}", "text"))
        assertEquals("x\ny", lasJsonStrang("{\"a\":1,\"text\":\"x\\ny\"}", "text"))
        assertEquals("å", lasJsonStrang("{\"text\":\"\\u00e5\"}", "text"))
        
        // Testa att returnera null vid fel
        assertNull(lasJsonStrang("{\"text\":5}", "text"))
        assertNull(lasJsonStrang("{}", "text"))
        assertNull(lasJsonStrang("{\"text\":\"oavslutad", "text"))
        
        // Testa flera nycklar
        assertEquals("Hej", lasJsonStrang("{\"first\":\"Hej\",\"second\":\"Världen\"}", "first"))
    }
}
