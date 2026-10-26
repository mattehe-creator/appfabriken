package se.tmconnect.rostfangst.lokal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OllamaFragaTest {

    @Test
    fun exaktForm() {
        val result = byggOllamaChat("qwen3:8b", "Du tolkar.", "ring tandläkaren", listOf("PAMINNELSE", "ANTECKNING"))
        val expected = """{"model":"qwen3:8b","stream":false,"messages":[{"role":"system","content":"Du tolkar."},{"role":"user","content":"ring tandläkaren"}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":["PAMINNELSE","ANTECKNING"]},"titel":{"type":"string"},"start":{"type":"string"},"slut":{"type":"string"},"plats":{"type":"string"},"svar":{"type":"string"}},"required":["typ","titel"]}}"""
        assertEquals(expected, result)
    }

    @Test
    fun specialtecknKodas() {
        val result = byggOllamaChat("m", "s", "a\"b\nc", listOf("X"))
        assertTrue(result.contains("\"content\":\"a\\\"b\\nc\""))
    }

    @Test
    fun tomTypLista() {
        val result = byggOllamaChat("m", "s", "t", listOf())
        assertTrue(result.contains("\"enum\":[]"))
    }

    @Test
    fun enTyp() {
        val result = byggOllamaChat("model", "system", "text", listOf("HEM"))
        assertTrue(result.contains("\"enum\":[\"HEM\"]"))
    }
}
