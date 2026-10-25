package se.tmconnect.flextid.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.regler.ARBETSDAGAR_MANDAG_FREDAG
import se.tmconnect.flextid.regler.Arbetstid
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.dagarForLedighetsperiod

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FlextidRepositoryLedighetTest {
    private lateinit var databas: FlextidDatabas
    private lateinit var repository: FlextidRepository
    private val at = Arbetstid(480, ARBETSDAGAR_MANDAG_FREDAG)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas = Room.inMemoryDatabaseBuilder(context, FlextidDatabas::class.java).allowMainThreadQueries().build()
        repository = FlextidRepository(databas.flextidDao())
        runTest { repository.sparaInstallning(Installning()) }
    }

    @After
    fun tearDown() {
        databas.close()
    }

    private fun tvaVeckor() =
        dagarForLedighetsperiod(LocalDate.of(2026, 7, 6), LocalDate.of(2026, 7, 17), DagTyp.SEMESTER, at, emptySet())

    @Test
    fun medProSparasAllaDagar() = runTest {
        assertEquals(FlextidRepository.LedighetsPeriodResult.Sparat(10), repository.sparaLedighetsperiod(tvaVeckor(), arPro = true))
        assertEquals(10, repository.antalDagar())
    }

    @Test
    fun utanProOchForMangaSparasInget() = runTest {
        repeat(8) { repository.sparaDag(Dag(datum = LocalDate.of(2026, 1, 5).plusDays(it.toLong())), arPro = false) }
        assertEquals(FlextidRepository.LedighetsPeriodResult.GransUtanPro(7), repository.sparaLedighetsperiod(tvaVeckor(), arPro = false))
        assertEquals(8, repository.antalDagar())
    }

    @Test
    fun tomListaGerIngaDagar() = runTest {
        assertEquals(FlextidRepository.LedighetsPeriodResult.IngaDagar, repository.sparaLedighetsperiod(emptyList(), arPro = true))
    }
}
