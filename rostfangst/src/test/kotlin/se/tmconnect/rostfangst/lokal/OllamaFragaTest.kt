package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class OllamaFragaTest {
    
    @Test
    fun testBasic() {
        val result = byggOllamaChat("qwen3:8b", "Du tolkar.", "ring tandläkaren", listOf("PAMINNELSE", "ANTECKNING"))
        val expected = """{"model":"qwen3:8b","stream":false,"messages":[{"role":"system","content":"Du tolkar."},{"role":"user","content":"ring tandläkaren"}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":["PAMINNELSE","ANTECKNING"]},"rubrik":{"type":"string"},"start":{"type":"string"},"text":{"type":"string"}},"required":["typ","rubrik"]}}"""
        assertEquals(expected, result)
    }
    
    @Test
    fun testSpecialCharacters() {
        val result = byggOllamaChat("m", "s", "a\"b\nc", listOf("X"))
        assertTrue(result.contains("\"content\":\"a\\\"b\\nc\""))
    }
    
    @Test
    fun testEmptyTypes() {
        val result = byggOllamaChat("m", "s", "t", emptyList())
        assertTrue(result.contains("\"enum\":[]"))
    }
}
