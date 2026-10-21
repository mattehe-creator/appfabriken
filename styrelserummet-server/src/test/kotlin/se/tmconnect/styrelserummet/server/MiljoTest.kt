package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MiljoTest {
    @Test
    fun saknadDatakatalogGerFel() {
        val rader = kontrolleraMiljo(mapOf("STYRELSERUMMET_DATA" to null))
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("STYRELSERUMMET_DATA"))
    }

    @Test
    fun oskrivbarDatakatalogGerFel() {
        val rader = kontrolleraMiljo(mapOf("STYRELSERUMMET_DATA" to "/data")) { false }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("/data"))
    }

    @Test
    fun giltigSkrivbarDatakatalogGerIngaFel() {
        val rader = kontrolleraMiljo(mapOf("STYRELSERUMMET_DATA" to "/data")) { true }
        assertEquals(emptyList<String>(), rader)
    }
    
    @Test
    fun saknadApiNyckelGerFel() {
        val rader = kontrolleraMiljo(mapOf("ANTHROPIC_API_KEY" to null))
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("ANTHROPIC_API_KEY"))
        assertFalse(rader.single().contains("sk-ant-"))
    }
    
    @Test
    fun tomApiNyckelGerFel() {
        val rader = kontrolleraMiljo(mapOf("ANTHROPIC_API_KEY" to ""))
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("ANTHROPIC_API_KEY"))
        assertFalse(rader.single().contains("sk-ant-"))
    }
    
    @Test
    fun felFormatApiNyckelGerFel() {
        val rader = kontrolleraMiljo(mapOf("ANTHROPIC_API_KEY" to "fel-prefix-123"))
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("ANTHROPIC_API_KEY"))
        assertFalse(rader.single().contains("fel-prefix-123"))
        assertFalse(rader.single().contains("sk-ant-"))
    }
    
    @Test
    fun giltigApiNyckelGerIngaFel() {
        val rader = kontrolleraMiljo(mapOf("ANTHROPIC_API_KEY" to "sk-ant-test")) { true }
        assertEquals(emptyList<String>(), rader)
    }
}
