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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists

class JobbLagringSamtidighetTest {
    private lateinit var tempDir: Path

    @Before
    fun setUp() {
        tempDir = createTempDirectory("jobb_lagring_samtidighet")
    }

    @After
    fun tearDown() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
    }

    /**
     * Klienten pollar (uppdateraAktivitet) medan kön sätter status KLAR. Med läsning och skrivning
     * i två steg kunde en gammal kopia med BEARBETAR skrivas tillbaka efter KLAR, så att jobbet fastnade.
     */
    @Test
    fun aktivitetSkriverAldrigOverStatusKlar() {
        val lagring = JobbLagring(tempDir)
        repeat(20) { varv ->
            val id = "samtidig$varv"
            lagring.skapa(
                JobbMeta(
                    id = id,
                    forening = "Brf Test",
                    arsslut = 2025,
                    status = JobbStatus.BEARBETAR.name,
                    skapadMs = 0L,
                    senastAktivMs = 0L,
                    klientIpHash = "test",
                ),
            )
            val start = CountDownLatch(1)
            val lastNull = AtomicBoolean(false)
            val pollare = (1..8).map {
                Thread {
                    start.await()
                    repeat(200) {
                        if (lagring.uppdateraAktivitet(id) == null) lastNull.set(true)
                    }
                }
            }
            val ko = Thread {
                start.await()
                Thread.sleep(2)
                lagring.uppdateraMeta(id) { it.copy(status = JobbStatus.KLAR.name, felmeddelande = null) }
            }
            val alla = pollare + ko
            alla.forEach { it.start() }
            start.countDown()
            alla.forEach { it.join() }

            val meta = lagring.lasMeta(id)!!
            assertEquals("varv $varv", JobbStatus.KLAR.name, meta.status)
            assertTrue(meta.senastAktivMs > 0L)
            // Atomär skrivning: en läsare ser aldrig en halvskriven meta.json.
            assertFalse("meta.json var tillfälligt oläsbar i varv $varv", lastNull.get())
            val tmpKvar = Files.list(lagring.jobbKatalog(id)).use { s ->
                s.anyMatch { it.fileName.toString().endsWith(".tmp") }
            }
            assertFalse(tmpKvar)
        }
    }

    @Test
    fun uppdateraMetaPaSaknatJobbSkriverIngenting() {
        val lagring = JobbLagring(tempDir)
        assertNull(lagring.uppdateraMeta("finnsinte") { it.copy(betald = true) })
        assertNull(lagring.uppdateraAktivitet("finnsinte"))
        assertFalse(lagring.jobbKatalog("finnsinte").exists())
    }
}
