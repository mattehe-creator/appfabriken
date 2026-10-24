package se.tmconnect.styrelserummet.server

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.createTempDirectory

class IpGransSamtidighetTest {
    private lateinit var tempDir: Path

    @Before
    fun setUp() {
        tempDir = createTempDirectory("ip_grans_test")
    }

    @After
    fun tearDown() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
    }

    @Test
    fun samtidigaReservationerPasserarInteGransen() {
        val grans = IpGrans(tempDir)
        val antal = 40
        val start = CountDownLatch(1)
        val beviljade = AtomicInteger()
        val pool = Executors.newFixedThreadPool(antal)
        val nu = System.currentTimeMillis()
        repeat(antal) { i ->
            pool.submit {
                start.await()
                if (grans.reservera("203.0.113.5", nu + i) != null) beviljade.incrementAndGet()
            }
        }
        start.countDown()
        pool.shutdown()
        pool.awaitTermination(30, TimeUnit.SECONDS)
        assertEquals(MAX_JOBB_PER_IP_TIMME, beviljade.get())
        assertNull(grans.reservera("203.0.113.5", nu + antal))
        // En annan IP påverkas inte.
        assertNotNull(grans.reservera("198.51.100.7", nu))
    }

    @Test
    fun angradReservationGerPlatsenTillbaka() {
        val grans = IpGrans(tempDir)
        val nu = System.currentTimeMillis()
        val reservationer = (0 until MAX_JOBB_PER_IP_TIMME).map { grans.reservera("203.0.113.5", nu + it)!! }
        assertNull(grans.reservera("203.0.113.5", nu + 10))
        grans.angra(reservationer.last())
        assertNotNull(grans.reservera("203.0.113.5", nu + 11))
        assertNull(grans.reservera("203.0.113.5", nu + 12))
    }
}
