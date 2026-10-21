package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Test

class GranserTest {
    @Test
    fun skattaTeckenMedTomLista() {
        assertEquals(0, skattaTecken(emptyList()))
    }

    @Test
    fun skattaTeckenMedTreStrangar() {
        assertEquals(350, skattaTecken(listOf("a".repeat(100), "b".repeat(200), "c".repeat(50))))
    }

    @Test
    fun skattaTeckenMedSummaOverGrans() {
        assertEquals(400_001, skattaTecken(listOf("a".repeat(200_000), "b".repeat(200_001))))
    }
}
