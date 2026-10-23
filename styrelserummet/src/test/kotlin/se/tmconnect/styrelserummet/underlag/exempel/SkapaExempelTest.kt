package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test
import se.tmconnect.styrelserummet.pdf.PdfLasare
import java.nio.file.Files
import kotlin.io.path.createTempDirectory

class SkapaExempelTest {

    @Test
    fun skrivExempelHandingarSkaparTvaPdf() {
        val tmpDir = createTempDirectory("skapa_exempel")
        try {
            skrivExempelHandingar(tmpDir)

            val planPath = tmpDir.resolve("underhallsplan-exempel.pdf")
            val arPath = tmpDir.resolve("arsredovisning-exempel.pdf")

            Assert.assertTrue(Files.exists(planPath))
            Assert.assertTrue(Files.exists(arPath))
            Assert.assertTrue(Files.size(planPath) > 1000)
            Assert.assertTrue(Files.size(arPath) > 1000)

            val planText = PdfLasare().lasSomText(Files.readAllBytes(planPath)).getOrThrow()
            Assert.assertTrue(planText.contains("Brf Exempelgården"))

            val arText = PdfLasare().lasSomText(Files.readAllBytes(arPath)).getOrThrow()
            Assert.assertTrue(arText.contains("Byggnader och mark"))
        } finally {
            Files.walk(tmpDir).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
            }
        }
    }
}
