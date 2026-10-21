package se.tmconnect.styrelserummet.server

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.writeText

class JobbLagringTest {
    private lateinit var tempDir: java.nio.file.Path

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

    // ... rest of the file remains unchanged

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
        val langtNamn = "Årsredovisning Testförening 2025.pdf"
        val bytes = "Testinnehåll".encodeToByteArray()
        
        lagring.sparaFil(jobId, langtNamn, bytes)
        
        // Kontrollera att filen finns i filernas katalog
        val filer = lagring.listaFiler(jobId)
        assertEquals(1, filer.size)
        
        // Kontrollera att filnamnet är säkert (endast tillåtna tecken)
        val sparadFil = filer.first()
        val filnamn = sparadFil.fileName.toString()
        
        // Filnamnet ska bara innehålla tillåtna tecken
        assertTrue("Filnamn får endast innehålla a-z, A-Z, 0-9, ., _, - och å, ä, ö", 
            filnamn.matches(Regex("""^[a-zA-Z0-9._\-åäöÅÄÖ]+$""")))
        
        // Kontrollera att innehållet är oförändrat
        val readBytes = Files.readAllBytes(sparadFil)
        assertEquals(bytes.contentToString(), readBytes.contentToString())
    }
}
