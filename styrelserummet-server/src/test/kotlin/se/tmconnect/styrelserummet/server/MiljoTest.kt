package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
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
}
