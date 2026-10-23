package se.tmconnect.flextid.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.regler.ARBETSDAGAR_MANDAG_FREDAG

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FlextidRepositoryVanligDagTest {
    private lateinit var databas: FlextidDatabas
    private lateinit var repository: FlextidRepository

    private val vanligStart = LocalTime.of(8, 0)
    private val vanligSlut = LocalTime.of(16, 30)
    private val vanligRast = 30

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas =
            Room.inMemoryDatabaseBuilder(context, FlextidDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = FlextidRepository(databas.flextidDao())
        runTest {
            repository.sparaInstallning(Installning(arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG))
        }
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun spararNyDagMedVanligaTider() = runTest {
        val onsdag = LocalDate.of(2026, 10, 21)
        val resultat =
            repository.fyllVanligDag(
                onsdag,
                ARBETSDAGAR_MANDAG_FREDAG,
                vanligStart,
                vanligSlut,
                vanligRast,
                arPro = true,
            )
        assertEquals(FlextidRepository.FyllVanligDagResult.Sparat, resultat)
        val sparad = repository.hamtaDag(onsdag)
        requireNotNull(sparad)
        assertEquals(vanligStart, sparad.start)
        assertEquals(vanligSlut, sparad.slut)
        assertEquals(vanligRast, sparad.rastMin)
    }

    @Test
    fun befintligDagAndrasInte() = runTest {
        val onsdag = LocalDate.of(2026, 10, 21)
        repository.sparaDag(
            Dag(
                datum = onsdag,
                start = LocalTime.of(7, 0),
                slut = LocalTime.of(15, 0),
                rastMin = 0,
            ),
            arPro = true,
        )
        val resultat =
            repository.fyllVanligDag(
                onsdag,
                ARBETSDAGAR_MANDAG_FREDAG,
                vanligStart,
                vanligSlut,
                vanligRast,
                arPro = true,
            )
        assertEquals(FlextidRepository.FyllVanligDagResult.FinnsRedan, resultat)
        assertEquals(LocalTime.of(7, 0), repository.hamtaDag(onsdag)?.start)
    }

    @Test
    fun gratisgransRespekteras() = runTest {
        val bas = LocalDate.of(2026, 1, 1)
        repeat(15) { offset ->
            repository.sparaDag(
                Dag(datum = bas.plusDays(offset.toLong()), start = LocalTime.of(8, 0)),
                arPro = false,
            )
        }
        val nyDag = bas.plusDays(20)
        val resultat =
            repository.fyllVanligDag(
                nyDag,
                ARBETSDAGAR_MANDAG_FREDAG,
                vanligStart,
                vanligSlut,
                vanligRast,
                arPro = false,
            )
        assertEquals(FlextidRepository.FyllVanligDagResult.GransUtanPro, resultat)
        assertNull(repository.hamtaDag(nyDag))
    }
}
