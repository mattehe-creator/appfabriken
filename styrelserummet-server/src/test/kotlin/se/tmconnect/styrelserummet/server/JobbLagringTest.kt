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
import kotlin.io.path.exists
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
        assertTrue(meta != null)
        assertEquals(jobId, meta!!.id)
        assertEquals("Testförening", meta.forening)
    }

    @Test
    fun saknadJobbKatalogGerNullInteUndantag() {
        val jobId = "test5"
        val lagring = JobbLagring(tempDir)
        
        // Skapa ett jobb för att få en giltig katalog
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
        
        // Radera jobbkatalogen manuellt från filsystemet (inte via raderaJobb)
        val katalog = lagring.jobbKatalog(jobId)
        assertTrue(katalog.exists())
        Files.walk(katalog)
            .sorted(Comparator.reverseOrder())
            .forEach { it.toFile().delete() }
        
        // Kontrollera att metoder inte kraschar utan returnerar null/tom lista
        assertNull(lagring.lasMeta(jobId))
        
        val utkast = UtkastDto(
            forening = "Testförening",
            arsslut = 2023,
            byggnadensVarde = 0,
            komponenter = emptyList(),
            avstamning = AvstamningDto(0, 0, 0, true),
            anmarkningar = emptyList(),
        )
        
        lagring.sparaUtkast(jobId, utkast)
        assertNull(lagring.lasUtkast(jobId))
        
        assertTrue(lagring.listaFiler(jobId).isEmpty())
    }

    @Test
    fun sparaFilSakertNamnLangtOchUnicode() {
        val jobId = "test_long_filename"
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

        // Skapa ett långt filnamn med svenska tecken
        val longFilename = "Årsredovisning Testförening 2025.pdf".repeat(10) // Gör det minst 200 tecken
        val bytes = "Testinnehåll för filen".encodeToByteArray()

        lagring.sparaFil(jobId, longFilename, bytes)

        // Kontrollera att filen finns i filer-katalogen
        val filerDir = lagring.jobbKatalog(jobId) / "filer"
        assertTrue(filerDir.exists())
        
        // Hämta filnamnet som det sparades (det säkra namnet)
        val savedFiles = filerDir.listDirectoryEntries().toList()
        assertEquals(1, savedFiles.size)
        
        val savedFile = savedFiles[0]
        val fileName = savedFile.fileName.toString()
        
        // Kontrollera att filnamnet bara innehåller tillåtna tecken
        assertTrue("Filnamn innehåller ogiltiga tecken: $fileName", 
            fileName.matches(Regex("^[a-zA-Z0-9._-]+$")))
        
        // Kontrollera att innehållet är oförändrat
        val readBytes = savedFile.readBytes()
        assertArrayEquals(bytes, readBytes)
    }
}
