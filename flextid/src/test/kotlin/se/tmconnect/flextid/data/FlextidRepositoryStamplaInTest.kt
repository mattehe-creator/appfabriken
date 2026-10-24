package se.tmconnect.flextid.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.regler.TidValideringFel

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FlextidRepositoryStamplaInTest {
    private lateinit var databas: FlextidDatabas
    private lateinit var repository: FlextidRepository
    private val datum = LocalDate.of(2026, 10, 21)

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
    fun stamplaInEfterUtstamplingAvvisasOchDagenBehalls() = runTest {
        repository.sparaDag(
            Dag(datum = datum, start = LocalTime.of(8, 0), slut = LocalTime.of(16, 0), rastMin = 30),
            arPro = false,
        )

        val resultat = repository.stamplaIn(datum, LocalTime.of(16, 5), 30, arPro = false)

        assertEquals(FlextidRepository.SparaDagResult.Fel(TidValideringFel.SlutForeStart), resultat)
        val dag = repository.hamtaDag(datum)!!
        assertEquals(LocalTime.of(8, 0), dag.start)
        assertEquals(LocalTime.of(16, 0), dag.slut)
    }

    @Test
    fun stamplaInForeSluttidRattarStarten() = runTest {
        repository.sparaDag(
            Dag(datum = datum, start = LocalTime.of(8, 0), slut = LocalTime.of(16, 0), rastMin = 30),
            arPro = false,
        )

        val resultat = repository.stamplaIn(datum, LocalTime.of(7, 30), 30, arPro = false)

        assertEquals(FlextidRepository.SparaDagResult.Ok, resultat)
        assertEquals(LocalTime.of(7, 30), repository.hamtaDag(datum)!!.start)
    }

    @Test
    fun stamplaInUtanDagSparas() = runTest {
        val resultat = repository.stamplaIn(datum, LocalTime.of(8, 0), 30, arPro = false)

        assertEquals(FlextidRepository.SparaDagResult.Ok, resultat)
        assertEquals(LocalTime.of(8, 0), repository.hamtaDag(datum)!!.start)
    }
}
