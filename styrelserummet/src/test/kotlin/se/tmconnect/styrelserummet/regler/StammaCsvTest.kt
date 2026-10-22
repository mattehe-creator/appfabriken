package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertTrue
import org.junit.Test

class StammaCsvTest {

    @Test
    fun stammaCsvHarMinstSjuPrisbasbeloppOchRattVarde2026() {
        val resource = checkNotNull(javaClass.getResourceAsStream("/stamma.csv"))
        val lines = resource.bufferedReader().readLines()

        val prisbasbeloppLines = lines.drop(1).filter { it.startsWith("prisbasbelopp;") }
        assertTrue(
            prisbasbeloppLines.size >= 7,
        )

        val line2026 = lines.drop(1).firstOrNull { it.startsWith("prisbasbelopp;2026;") }
        assertTrue(line2026?.contains(";59200;") == true)
    }
}
