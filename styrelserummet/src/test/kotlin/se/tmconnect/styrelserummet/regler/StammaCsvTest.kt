package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class StammaCsvTest {

    @Test
    fun `should contain prisbasbelopp data`() {
        val resource = javaClass.getResourceAsStream("/stamma.csv")
        val lines = resource.bufferedReader().readLines()
        
        // Kontrollera att det finns minst 7 rader med prisbasbelopp
        val prisbasbeloppLines = lines.drop(1).filter { it.startsWith("prisbasbelopp;") }
        assertTrue(prisbasbeloppLines.size >= 7, "Should contain at least 7 prisbasbelopp rows")
        
        // Kontrollera att 2026 har rätt värde
        val line2026 = lines.drop(1).firstOrNull { it.startsWith("prisbasbelopp;2026;") }
        assertTrue(line2026?.contains(";59200;") == true, "2026 should have value 59200")
    }
}
