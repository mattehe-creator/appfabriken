package se.tmconnect.flextid.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.data.AutoBackupInstallningarLager
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.FlextidDatabas
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.karna.sakerhetskopia.MappLagring
import se.tmconnect.karna.sakerhetskopia.MappPost

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AutoSakerhetskopiaWorkerTest {
    private lateinit var context: Context
    private lateinit var installningar: AutoBackupInstallningarLager
    private lateinit var minnesMapp: MinnesMapp

    class MinnesMapp(
        val poster: MutableList<Pair<MappPost, ByteArrayOutputStream>> = mutableListOf(),
        private var nr: Int = 0,
    ) : MappLagring {
        override fun lista(): List<MappPost> = poster.map { it.first }

        override fun skapa(namn: String): java.io.OutputStream? {
            val ut = ByteArrayOutputStream()
            poster += MappPost("id${nr++}", namn, System.currentTimeMillis()) to ut
            return ut
        }

        override fun radera(post: MappPost): Boolean = poster.removeAll { it.first.id == post.id }
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        installningar = AutoBackupInstallningarLager(context)
        minnesMapp = MinnesMapp()
        AutoSakerhetskopiaWorker.testMappLagring = minnesMapp
    }

    @After
    fun tearDown() {
        AutoSakerhetskopiaWorker.testMappLagring = null
        WorkManager.getInstance(context).cancelAllWork()
    }

    private suspend fun seedDag() {
        val dao = FlextidDatabas.hamta(context).flextidDao()
        dao.sparaInstallning(Installning())
        dao.sparaDag(
            Dag(
                datum = LocalDate.of(2026, 10, 1),
                start = LocalTime.of(8, 0),
                slut = LocalTime.of(17, 0),
                rastMin = 30,
                typ = DagTyp.ARBETE,
            ),
        )
    }

    @Test
    fun tvingaKopierarJson() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        seedDag()
        val idag = LocalDate.of(2026, 10, 23)
        korAutoSakerhetskopiaJobb(context, tvinga = true, idag = idag)
        assertEquals(1, minnesMapp.poster.size)
        assertEquals("flextid-auto-2026-10-23.json", minnesMapp.poster[0].first.namn)
        val json = minnesMapp.poster[0].second.toString(Charsets.UTF_8.name())
        val las = lasSakerhetskopiaJson(json)
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        assertEquals(1, (las as SakerhetskopiaJsonLasResult.Ok).innehall.dagar.size)
    }

    @Test
    fun intervalletRespekterasUtanTvinga() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        installningar.sattAutoBackupResultat(true, System.currentTimeMillis())
        korAutoSakerhetskopiaJobb(context, tvinga = false)
        assertTrue(minnesMapp.poster.isEmpty())
    }

    @Test
    fun kopieraNarSenasteArGammal() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        seedDag()
        val dagMs = TimeUnit.DAYS.toMillis(1)
        val nu = 20 * dagMs
        installningar.sattAutoBackupResultat(true, 10 * dagMs)
        installningar.sattAutoBackupIntervall(7)
        korAutoSakerhetskopiaJobb(context, tvinga = false, nu = nu, idag = LocalDate.of(2026, 10, 20))
        assertEquals(1, minnesMapp.poster.size)
    }
}
