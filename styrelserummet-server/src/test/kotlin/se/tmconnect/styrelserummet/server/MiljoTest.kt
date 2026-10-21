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
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun saknadApiNyckelGerFel() {
        val rader = kontrolleraMiljo(mapOf("STYRELSERUMMET_DATA" to "/data")) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("ANTHROPIC_API_KEY"))
        assertFalse(rader.single().contains("sk-ant-"))
    }

    @Test
    fun tomApiNyckelGerFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("ANTHROPIC_API_KEY"))
        assertFalse(rader.single().contains("sk-ant-"))
    }

    @Test
    fun felFormatApiNyckelGerFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "fel-prefix-123",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("ANTHROPIC_API_KEY"))
        assertFalse(rader.single().contains("fel-prefix-123"))
    }

    @Test
    fun giltigApiNyckelGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun nyckelvardSynsInteIFelmeddelanden() {
        val hemligNyckel = "sk-ant-hemlig-xyz"
        val okRader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to hemligNyckel,
            ),
        ) { true }
        val okText = okRader.joinToString()
        assertFalse(okText.contains("hemlig"))
        assertFalse(okText.contains(hemligNyckel))

        val felRader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "fel-nyckel",
            ),
        ) { true }
        val felText = felRader.joinToString()
        assertTrue(felRader.isNotEmpty())
        assertFalse(felText.contains("hemlig"))
        assertFalse(felText.contains(hemligNyckel))
        assertFalse(felText.contains("fel-nyckel"))
    }

    @Test
    fun portFaltTalGerFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "PORT" to "abc",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("PORT"))
    }

    @Test
    fun portForLitetGerFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "PORT" to "0",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("PORT"))
    }

    @Test
    fun portForStortGerFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "PORT" to "99999",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("PORT"))
    }

    @Test
    fun giltigPortGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "PORT" to "8080",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun saknadPortGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun proxyFelVardeGerVarning() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "STYRELSERUMMET_PROXY" to "true",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Varning:"))
        assertTrue(rader.single().contains("STYRELSERUMMET_PROXY"))
    }

    @Test
    fun proxyTomGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "STYRELSERUMMET_PROXY" to "",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun proxyEttGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "STYRELSERUMMET_PROXY" to "1",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun modellSaknasGerFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "STYRELSERUMMET_MODELL" to "",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Fel:"))
        assertTrue(rader.single().contains("STYRELSERUMMET_MODELL"))
    }

    @Test
    fun modellOkandGerVarning() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "STYRELSERUMMET_MODELL" to "claude-opus-99",
            ),
        ) { true }
        assertEquals(1, rader.size)
        assertTrue(rader.single().startsWith("Varning:"))
        assertTrue(rader.single().contains("STYRELSERUMMET_MODELL"))
    }

    @Test
    fun giltigModellGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
                "STYRELSERUMMET_MODELL" to "claude-sonnet-5-5",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }

    @Test
    fun saknadModellGerIngaFel() {
        val rader = kontrolleraMiljo(
            mapOf(
                "STYRELSERUMMET_DATA" to "/data",
                "ANTHROPIC_API_KEY" to "sk-ant-test",
            ),
        ) { true }
        assertEquals(emptyList<String>(), rader)
    }
}
