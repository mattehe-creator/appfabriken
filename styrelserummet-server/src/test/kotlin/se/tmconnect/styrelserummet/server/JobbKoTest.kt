package se.tmconnect.styrelserummet.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

class JobbKoTest {
    private lateinit var tempDir: Path
    private lateinit var lagring: JobbLagring
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setUp() {
        tempDir = createTempDirectory("jobb_test")
        lagring = JobbLagring(tempDir)
    }

    @After
    fun tearDown() {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach { it.toFile().delete() }
    }

    @Test
    fun tomPdfGerIngenLasbarTextFel() {
        val id = "test_jobb_1"
        skapaJobbMedPdf(id, skannadPdfUtanText())

        JobbKo(lagring, FalskTolkare(), scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.FEL.name, metaResult.status)
        assertEquals(felTillMeddelande(ServerFel.IngenLasbarText), metaResult.felmeddelande)
    }

    @Test
    fun ogiltigtAiSvarGerAiOgiltigtSvarFel() {
        val id = "test_jobb_2"
        skapaJobbMedPdf(id, pdfMedText("Underhall tak 500000"))

        JobbKo(lagring, FalskTolkare(lage = SvarLage.OGILTIGT_JSON), scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.FEL.name, metaResult.status)
        assertEquals(felTillMeddelande(ServerFel.AiOgiltigtSvar), metaResult.felmeddelande)
    }

    @Test
    fun aiTimeoutGerAiTimeoutFel() {
        val id = "test_jobb_3"
        skapaJobbMedPdf(id, pdfMedText("Underhall tak 500000"))

        JobbKo(lagring, FalskTolkare(lage = SvarLage.TIMEOUT), scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.FEL.name, metaResult.status)
        assertEquals(felTillMeddelande(ServerFel.AiTimeout), metaResult.felmeddelande)
    }

    private fun skapaJobbMedPdf(id: String, pdfBytes: ByteArray) {
        val nu = System.currentTimeMillis()
        val meta = JobbMeta(
            id = id,
            forening = "Testförening",
            arsslut = 2023,
            status = JobbStatus.VANTAR.name,
            skapadMs = nu,
            senastAktivMs = nu,
            klientIpHash = "test",
        )
        lagring.skapa(meta)
        lagring.sparaFil(id, "underhallsplan.pdf", pdfBytes)
    }

    private fun skannadPdfUtanText(): ByteArray = pdfMedText(listOf("", ""))

    private fun pdfMedText(text: String): ByteArray = pdfMedText(listOf(text))

    private fun pdfMedText(sidor: List<String>): ByteArray {
        PDDocument().use { doc ->
            sidor.forEach { text ->
                val page = PDPage()
                doc.addPage(page)
                if (text.isNotBlank()) {
                    PDPageContentStream(doc, page).use { cs ->
                        cs.beginText()
                        cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                        cs.newLineAtOffset(50f, 700f)
                        cs.showText(text)
                        cs.endText()
                    }
                }
            }
            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
