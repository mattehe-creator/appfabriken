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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import se.tmconnect.styrelserummet.tolking.Tolkare
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
        assertEquals(felText(serverFelTillKod(ServerFel.IngenLasbarText)), metaResult.felmeddelande)
    }

    @Test
    fun ogiltigtAiSvarGerAiOgiltigtSvarFel() {
        val id = "test_jobb_2"
        skapaJobbMedPdf(id, pdfMedText("Underhall tak 500000"))

        JobbKo(lagring, FalskTolkare(lage = SvarLage.OGILTIGT_JSON), scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.FEL.name, metaResult.status)
        assertEquals(felText(serverFelTillKod(ServerFel.AiOgiltigtSvar)), metaResult.felmeddelande)
    }

    @Test
    fun aiTimeoutGerAiTimeoutFel() {
        val id = "test_jobb_3"
        skapaJobbMedPdf(id, pdfMedText("Underhall tak 500000"))

        JobbKo(lagring, FalskTolkare(lage = SvarLage.TIMEOUT), scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.FEL.name, metaResult.status)
        assertEquals(felText(serverFelTillKod(ServerFel.AiTimeout)), metaResult.felmeddelande)
    }

    @Test
    fun felLoggarJobbIdOchFelkodUtanKansligData() {
        val id = "test_jobb_logg_1"
        val loggar = mutableListOf<String>()
        skapaJobbMedPdf(id, skannadPdfUtanText())

        JobbKo(
            lagring,
            FalskTolkare(),
            scope = scope,
            synkron = true,
            loggSkriv = loggar::add,
        ).lagTill(id)

        assertEquals(1, loggar.size)
        val rad = loggar.single()
        assertTrue(rad.contains(id))
        assertTrue(rad.contains("IngenLasbarText"))
        assertFalse(rad.contains("19800101-1234"))
        assertFalse(rad.contains("underhallsplan.pdf"))
        assertFalse(rad.contains(felText(serverFelTillKod(ServerFel.IngenLasbarText))))
    }

    @Test
    fun ogiltigtAiSvarLoggarUtanPersonnummerEllerFilnamn() {
        val id = "test_jobb_logg_2"
        val loggar = mutableListOf<String>()
        skapaJobbMedPdf(id, pdfMedText("19800101-1234 Underhall tak"))

        JobbKo(
            lagring,
            FalskTolkare(lage = SvarLage.OGILTIGT_JSON),
            scope = scope,
            synkron = true,
            loggSkriv = loggar::add,
        ).lagTill(id)

        assertEquals(1, loggar.size)
        val rad = loggar.single()
        assertTrue(rad.contains(id))
        assertTrue(rad.contains("AiOgiltigtSvar"))
        assertFalse(rad.contains("19800101-1234"))
        assertFalse(rad.contains("Underhall tak"))
        assertFalse(rad.contains("underhallsplan.pdf"))
        assertFalse(rad.contains(felText(serverFelTillKod(ServerFel.AiOgiltigtSvar))))
    }
    
    @Test
    fun forStorHandlingAnroparInteTolkare() {
        val id = "test_jobb_for_stor"
        val spionTolkare = SpionTolkare()
        
        // Skapa en PDF med text som överskrider gränsen
        skapaJobbMedPdf(id, pdfMedLangText(MAX_TECKEN_TILL_TOLKNING + 1000))

        JobbKo(lagring, spionTolkare, scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.FEL.name, metaResult.status)
        assertEquals(felText(Felkoder.FOR_STOR_HANDLING), metaResult.felmeddelande)
        assertEquals(0, spionTolkare.antalAnrop)
    }
    
    @Test
    fun underGransenAnroparTolkare() {
        val id = "test_jobb_under_grans"
        val spionTolkare = SpionTolkare()
        
        // Skapa en PDF med text som är under gränsen
        skapaJobbMedPdf(id, pdfMedLangText(MAX_TECKEN_TILL_TOLKNING - 1000))

        JobbKo(lagring, spionTolkare, scope = scope, synkron = true).lagTill(id)

        val metaResult = lagring.lasMeta(id)!!
        assertEquals(JobbStatus.KLAR.name, metaResult.status)
        assertTrue(spionTolkare.antalAnrop > 0)
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

    private fun pdfMedLangText(tecken: Int): ByteArray {
        val chunkSize = 2000
        val sidor =
            (0 until tecken step chunkSize).map { start ->
                "x".repeat(minOf(chunkSize, tecken - start))
            }
        return pdfMedText(sidor)
    }

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

private class SpionTolkare(
    private val jsonSvar: String = """
        {"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"plan, sida 2","sakerhet":"HOG"}],"anmarkningar":[]}
    """.trimIndent(),
) : Tolkare {
    var antalAnrop = 0

    override fun foreslaKomponenter(
        underhallsplanUtdrag: String,
        sidor: List<Int>,
        filnamn: String,
    ): Result<String> {
        antalAnrop++
        return Result.success(jsonSvar)
    }
}
