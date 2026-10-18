package se.tmconnect.prov.aviseringar.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AviseringDaoTest {
    private lateinit var databas: AviseringDatabas
    private lateinit var dao: AviseringDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas = skapaTestAviseringDatabas(context)
        dao = databas.aviseringDao()
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun paketStatistikOkaAntal() = runTest {
        dao.registreraPaketAvisering("se.test.a", "App A")
        dao.registreraPaketAvisering("se.test.a", "App A")
        val lista = dao.allaPaketStatistik().first()
        assertEquals(1, lista.size)
        assertEquals(2, lista[0].antal)
    }

    @Test
    fun sparadAviseringHamtasISenaste() = runTest {
        val rad = exempelRad(nyckel = "k1", text = "Hej")
        assertTrue(dao.sparaAviseringOmNy(rad))
        val senaste = dao.senasteFemtio().first()
        assertEquals(1, senaste.size)
        assertEquals("Hej", senaste[0].text)
    }

    @Test
    fun dubblettSammaNyckelOchInnehallSparasInte() = runTest {
        val rad1 = exempelRad(nyckel = "k1", text = "Samma")
        val rad2 = exempelRad(nyckel = "k1", text = "Samma", loggadTid = 2L)
        assertTrue(dao.sparaAviseringOmNy(rad1))
        assertFalse(dao.sparaAviseringOmNy(rad2))
        assertEquals(1, dao.antalSparade())
    }

    @Test
    fun sammaNyckelNyttInnehallSparas() = runTest {
        val rad1 = exempelRad(nyckel = "k1", text = "Första")
        val rad2 = exempelRad(nyckel = "k1", text = "Andra", loggadTid = 2L)
        assertTrue(dao.sparaAviseringOmNy(rad1))
        assertTrue(dao.sparaAviseringOmNy(rad2))
        assertEquals(2, dao.antalSparade())
    }

    private fun exempelRad(nyckel: String, text: String, loggadTid: Long = 1L): AviseringEntitet {
        val hash = beraknaInnehallHash(text, null, null, null, null, null, null)
        return AviseringEntitet(
            loggadTid = loggadTid,
            postTime = 100L,
            nyckel = nyckel,
            paketNamn = "se.test",
            appNamn = "Test",
            kanalId = "kanal",
            title = "Titel",
            text = text,
            bigText = null,
            subText = null,
            textLines = null,
            conversationTitle = null,
            messages = null,
            innehallHash = hash,
        )
    }
}
