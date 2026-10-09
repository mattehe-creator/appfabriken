package se.tmconnect.garantivalvet.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.flow.first
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class KopDaoTest {
    private lateinit var databas: GarantiDatabas
    private lateinit var dao: KopDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas = Room.inMemoryDatabaseBuilder(context, GarantiDatabas::class.java)
            .allowMainThreadQueries()
            .build()
        dao = databas.kopDao()
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun infogaHamtaUppdateraTaBort() = runTest {
        val kopdatum = LocalDate.of(2024, 6, 15)
        val id = dao.infoga(
            Kop(
                vad = "Kaffebryggare",
                varKopt = "Elgiganten",
                kopdatum = kopdatum,
                garantiManader = 24,
                prisOre = 129_950L,
                anteckning = "Kvitto i lådan",
            ),
        )

        val hamtad = dao.hamta(id)
        assertNotNull(hamtad)
        assertEquals("Kaffebryggare", hamtad!!.vad)
        assertEquals(kopdatum, hamtad.kopdatum)

        dao.uppdatera(hamtad.copy(vad = "Espressomaskin"))
        assertEquals("Espressomaskin", dao.hamta(id)!!.vad)

        dao.taBort(hamtad.copy(vad = "Espressomaskin"))
        assertNull(dao.hamta(id))
        assertEquals(0, dao.antal())
    }

    @Test
    fun allaFlowInnehallerPoster() = runTest {
        dao.infoga(
            Kop(
                vad = "A",
                kopdatum = LocalDate.of(2025, 1, 1),
                garantiManader = 12,
            ),
        )
        dao.infoga(
            Kop(
                vad = "B",
                kopdatum = LocalDate.of(2025, 2, 1),
                garantiManader = 6,
            ),
        )

        assertEquals(2, dao.allaFlow().first().size)
    }

    @Test
    fun kopdatumOverleverKonvertering() = runTest {
        val datum = LocalDate.of(2020, 2, 29)
        val id = dao.infoga(
            Kop(
                vad = "Test",
                kopdatum = datum,
                garantiManader = 0,
            ),
        )

        assertEquals(datum, dao.hamta(id)!!.kopdatum)
    }
}
