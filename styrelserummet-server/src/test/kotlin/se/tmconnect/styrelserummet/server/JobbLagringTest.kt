package se.tmconnect.styrelserummet.server

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.writeText

class JobbLagringTest {
    private lateinit var tempDir: Path

    @Before
    fun setUp() {
        tempDir = createTempDirectory("jobb_lagring_test")
    }

    @After
    fun tearDown() {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach { it.toFile().delete() }
    }

    @Test
    fun korruptMetaOgiltigJson() {
        val jobId = "test1"
        JobbLagring(tempDir).skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test",
            ),
        )
        (JobbLagring(tempDir).jobbKatalog(jobId) / "meta.json").writeText("{")

        var loggmeddelande: String? = null
        val lagringMedLogg = JobbLagring(tempDir) { msg ->
            loggmeddelande = msg
        }

        assertNull(lagringMedLogg.lasMeta(jobId))
        assertEquals("Skadad meta.json-fil, jobb hoppas över", loggmeddelande)
        assertFalse(loggmeddelande!!.contains(jobId))

        val allaJobb = lagringMedLogg.allaJobb()
        assertEquals(1, allaJobb.size)
        assertEquals(jobId, allaJobb.single())

        rensaGamlaJobb(lagringMedLogg)
        assertEquals(1, lagringMedLogg.allaJobb().size)
    }

    @Test
    fun korruptMetaTomFil() {
        val jobId = "test2"
        JobbLagring(tempDir).skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test",
            ),
        )
        (JobbLagring(tempDir).jobbKatalog(jobId) / "meta.json").writeText("")

        var loggmeddelande: String? = null
        val lagringMedLogg = JobbLagring(tempDir) { msg ->
            loggmeddelande = msg
        }

        assertNull(lagringMedLogg.lasMeta(jobId))
        assertEquals("Skadad meta.json-fil, jobb hoppas över", loggmeddelande)
        assertFalse(loggmeddelande!!.contains(jobId))
    }

    @Test
    fun korruptMetaHalvFil() {
        val jobId = "test3"
        JobbLagring(tempDir).skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test",
            ),
        )
        (JobbLagring(tempDir).jobbKatalog(jobId) / "meta.json").writeText(
            """{"id": "test", "forening": "Testförening", """,
        )

        var loggmeddelande: String? = null
        val lagringMedLogg = JobbLagring(tempDir) { msg ->
            loggmeddelande = msg
        }

        assertNull(lagringMedLogg.lasMeta(jobId))
        assertEquals("Skadad meta.json-fil, jobb hoppas över", loggmeddelande)
        assertFalse(loggmeddelande!!.contains(jobId))
    }

    @Test
    fun samtidigSkrivMetaGerGiltigJson() {
        val jobId = "test4"
        val lagring = JobbLagring(tempDir)
        lagring.skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test",
            ),
        )

        val threads = mutableListOf<Thread>()
        val antalUppdateringar = 20

        repeat(antalUppdateringar) {
            val thread = Thread {
                repeat(10) {
                    lagring.uppdateraAktivitet(jobId)
                }
            }
            threads.add(thread)
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        // Kontrollera att metafilen är giltig
        val meta = lagring.lasMeta(jobId)
        assertNotNull(meta)
        assertEquals(jobId, meta!!.id)
        assertEquals("Testförening", meta.forening)
    }
}
