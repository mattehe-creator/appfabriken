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
class FlextidRepositoryGenvagTest {
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
    fun genvagStamplaInSpararStart() = runTest {
        val resultat =
            repository.genvagStamplaIn(
                idag,
                LocalTime.of(8, 15, 20),
                standardRastMin = 30,
                arPro = false,
            )
        assertEquals(FlextidRepository.GenvagStamplaInResult.Sparat, resultat)
        assertEquals(LocalTime.of(8, 15), repository.hamtaDag(idag)?.start)
    }

    @Test
    fun genvagStamplaInRedanInstamplad() = runTest {
        repository.stamplaIn(idag, LocalTime.of(7, 30), 30, arPro = false)
        val resultat =
            repository.genvagStamplaIn(idag, LocalTime.of(9, 0), 30, arPro = false)
        assertTrue(resultat is FlextidRepository.GenvagStamplaInResult.RedanInstamplad)
        assertEquals(LocalTime.of(7, 30), (resultat as FlextidRepository.GenvagStamplaInResult.RedanInstamplad).start)
        assertEquals(LocalTime.of(7, 30), repository.hamtaDag(idag)?.start)
    }

    @Test
    fun genvagStamplaUtUtanStart() = runTest {
        val resultat = repository.genvagStamplaUt(idag, LocalTime.of(17, 0), arPro = false)
        assertEquals(FlextidRepository.GenvagStamplaUtResult.IngenInstempling, resultat)
        assertEquals(null, repository.hamtaDag(idag))
    }

    @Test
    fun genvagStamplaUtSlutForeStart() = runTest {
        repository.stamplaIn(idag, LocalTime.of(17, 0), 30, arPro = false)
        val resultat = repository.genvagStamplaUt(idag, LocalTime.of(8, 0), arPro = false)
        assertEquals(FlextidRepository.GenvagStamplaUtResult.SlutForeStart, resultat)
        assertEquals(null, repository.hamtaDag(idag)?.slut)
    }

    @Test
    fun genvagStamplaInGransUtanPro() = runTest {
        val bas = LocalDate.of(2026, 1, 1)
        repeat(15) { offset ->
            repository.sparaDag(
                Dag(datum = bas.plusDays(offset.toLong()), start = LocalTime.of(8, 0)),
                arPro = false,
            )
        }
        val resultat =
            repository.genvagStamplaIn(idag, LocalTime.of(8, 0), 30, arPro = false)
        assertEquals(FlextidRepository.GenvagStamplaInResult.GransUtanPro, resultat)
        assertEquals(null, repository.hamtaDag(idag))
    }
}
