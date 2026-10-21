package se.tmconnect.styrelserummet.server

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JobbLagringTest {
    @TempDir
    lateinit var tempDir: Path

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    @Test
    fun korruptMetaOgiltigJson() {
        val lagring = JobbLagring(tempDir)
        
        // Skapa ett jobb med korrupt meta.json (ogiltig JSON)
        val jobId = "test1"
        lagring.skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test"
            )
        )
        
        // Skriv korrupt JSON
        val metaFil = lagring.jobbKatalog(jobId) / "meta.json"
        metaFil.writeText("{") // Ogiltig JSON
        
        var loggmeddelande: String? = null
        val lagringMedLogg = JobbLagring(tempDir) { msg ->
            loggmeddelande = msg
        }
        
        // Testa att lasMeta returnerar null för korrupt fil
        val meta = lagringMedLogg.lasMeta(jobId)
        assertNull(meta)
        
        // Kontrollera att loggmeddelandet är korrekt
        assertEquals("Skadad meta.json-fil, jobb hoppas över", loggmeddelande)
        
        // Testa att allaJobb inte kraschar och returnerar jobbet trots korrupt fil
        val allaJobb = lagringMedLogg.allaJobb()
        assertEquals(1, allaJobb.size)
        assertEquals(jobId, allaJobb[0])
    }

    @Test
    fun korruptMetaTomFil() {
        val lagring = JobbLagring(tempDir)
        
        // Skapa ett jobb med tom meta.json
        val jobId = "test2"
        lagring.skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test"
            )
        )
        
        // Skriv tom fil
        val metaFil = lagring.jobbKatalog(jobId) / "meta.json"
        metaFil.writeText("") // Tom fil
        
        var loggmeddelande: String? = null
        val lagringMedLogg = JobbLagring(tempDir) { msg ->
            loggmeddelande = msg
        }
        
        // Testa att lasMeta returnerar null för tom fil
        val meta = lagringMedLogg.lasMeta(jobId)
        assertNull(meta)
        
        // Kontrollera att loggmeddelandet är korrekt
        assertEquals("Skadad meta.json-fil, jobb hoppas över", loggmeddelande)
    }

    @Test
    fun korruptMetaHalvFil() {
        val lagring = JobbLagring(tempDir)
        
        // Skapa ett jobb med halv JSON
        val jobId = "test3"
        lagring.skapa(
            JobbMeta(
                id = jobId,
                forening = "Testförening",
                arsslut = 2023,
                status = JobbStatus.VANTAR.name,
                skapadMs = System.currentTimeMillis(),
                senastAktivMs = System.currentTimeMillis(),
                klientIpHash = "test"
            )
        )
        
        // Skriv halv JSON
        val metaFil = lagring.jobbKatalog(jobId) / "meta.json"
        metaFil.writeText("""{"id": "test", "forening": "Testförening", """) // Halv JSON
        
        var loggmeddelande: String? = null
        val lagringMedLogg = JobbLagring(tempDir) { msg ->
            loggmeddelande = msg
        }
        
        // Testa att lasMeta returnerar null för halv fil
        val meta = lagringMedLogg.lasMeta(jobId)
        assertNull(meta)
        
        // Kontrollera att loggmeddelandet är korrekt
        assertEquals("Skadad meta.json-fil, jobb hoppas över", loggmeddelande)
    }
}
