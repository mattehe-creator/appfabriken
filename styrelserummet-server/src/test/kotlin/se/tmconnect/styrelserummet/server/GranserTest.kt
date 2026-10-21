package se.tmconnect.styrelserummet.server

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GranserTest {
    
    @Test
    fun `skattaTecken med tom lista`() {
        val resultat = skattaTecken(emptyList())
        assertEquals(0, resultat)
    }
    
    @Test
    fun `skattaTecken med tre strängar`() {
        val resultat = skattaTecken(listOf("a".repeat(100), "b".repeat(200), "c".repeat(50)))
        assertEquals(350, resultat)
    }
    
    @Test
    fun `skattaTecken med summa över gräns`() {
        val resultat = skattaTecken(listOf("a".repeat(200_000), "b".repeat(200_001)))
        assertEquals(400_001, resultat)
    }
}
