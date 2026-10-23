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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FlextidRepositoryStamplaTest {
    private lateinit var databas: FlextidDatabas
    private lateinit var repository: FlextidRepository
    private val idag = LocalDate.of(2026, 10, 23)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas =
            Room.inMemoryDatabaseBuilder(context, FlextidDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = FlextidRepository(databas.flextidDao())
        runTest { repository.sparaInstallning(Installning(standardRastMin = 30)) }
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun andraStamplaInGerRedanInstamplad() = runTest {
        val forsta =
            repository.stamplaIn(idag, LocalTime.of(8, 0), standardRastMin = 30, arPro = false)
        assertTrue(forsta is FlextidRepository.SparaDagResult.Ok)
        val andra =
            repository.stamplaIn(idag, LocalTime.of(9, 0), standardRastMin = 30, arPro = false)
        assertEquals(FlextidRepository.SparaDagResult.RedanInstamplad(LocalTime.of(8, 0)), andra)
        assertEquals(LocalTime.of(8, 0), repository.hamtaDag(idag)?.start)
    }

    @Test
    fun stamplaUtUtanInGerIngenInstampling() = runTest {
        val resultat = repository.stamplaUt(idag, LocalTime.of(17, 0), arPro = false)
        assertEquals(FlextidRepository.SparaDagResult.IngenInstampling, resultat)
        assertEquals(0, repository.antalDagar())
    }

    @Test
    fun stamplaInOchUtSpararSlut() = runTest {
        repository.stamplaIn(idag, LocalTime.of(8, 0), standardRastMin = 30, arPro = false)
        val ut = repository.stamplaUt(idag, LocalTime.of(16, 30), arPro = false)
        assertTrue(ut is FlextidRepository.SparaDagResult.Ok)
        assertEquals(LocalTime.of(16, 30), repository.hamtaDag(idag)?.slut)
    }
}
