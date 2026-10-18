package se.tmconnect.flextid.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.regler.DagTyp

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FlextidRepositoryGransTest {
    private lateinit var databas: FlextidDatabas
    private lateinit var repository: FlextidRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas =
            Room.inMemoryDatabaseBuilder(context, FlextidDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = FlextidRepository(databas.flextidDao())
        runTest { repository.sparaInstallning(Installning()) }
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun femtonPosterUtanProBlockerarSextonde() = runTest {
        val bas = LocalDate.of(2026, 1, 1)
        repeat(15) { offset ->
            repository.sparaDag(
                Dag(datum = bas.plusDays(offset.toLong()), start = LocalTime.of(8, 0)),
                arPro = false,
            )
        }
        val resultat =
            repository.sparaDag(
                Dag(datum = bas.plusDays(15), start = LocalTime.of(8, 0)),
                arPro = false,
            )
        assertEquals(FlextidRepository.SparaDagResult.GransUtanPro, resultat)
        assertEquals(15, repository.antalDagar())
    }

    @Test
    fun proTillaterSextondePost() = runTest {
        val bas = LocalDate.of(2026, 2, 1)
        repeat(15) { offset ->
            repository.sparaDag(
                Dag(datum = bas.plusDays(offset.toLong()), start = LocalTime.of(8, 0)),
                arPro = false,
            )
        }
        val resultat =
            repository.sparaDag(
                Dag(datum = bas.plusDays(15), start = LocalTime.of(8, 0)),
                arPro = true,
            )
        assertTrue(resultat is FlextidRepository.SparaDagResult.Ok)
        assertEquals(16, repository.antalDagar())
    }
}
