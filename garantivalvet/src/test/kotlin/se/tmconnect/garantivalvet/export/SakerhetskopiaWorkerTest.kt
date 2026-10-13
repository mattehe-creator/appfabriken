package se.tmconnect.garantivalvet.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import java.io.ByteArrayOutputStream
import java.time.LocalDate
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
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.Kop

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SakerhetskopiaWorkerTest {
    private lateinit var context: Context
    private lateinit var installningar: InstallningarLager
    private lateinit var minnesMapp: MinnesMapp

    /** Mapp i minnet (samma idé som [AutoSakerhetskopiaTest]). */
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
        installningar = InstallningarLager(context)
        minnesMapp = MinnesMapp()
        SakerhetskopiaWorker.testMappLagring = minnesMapp
    }

    @After
    fun tearDown() {
        SakerhetskopiaWorker.testMappLagring = null
        WorkManager.getInstance(context).cancelAllWork()
    }

    private suspend fun seedKop() {
        GarantiDatabas.hamta(context).kopDao().infoga(
            Kop(vad = "Hylla", kopdatum = LocalDate.of(2024, 5, 1), garantiManader = 24),
        )
    }

    @Test
    fun ingenKopiaNarMappSaknas() = runBlocking {
        korSakerhetskopiaJobb(context, tvinga = false)
        assertTrue(minnesMapp.poster.isEmpty())
    }

    @Test
    fun intervalletRespekterasUtanTvinga() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        installningar.sattAutoBackupIntervall(7)
        val nu = System.currentTimeMillis()
        installningar.sattAutoBackupResultat(true, nu - TimeUnit.DAYS.toMillis(2))
        korSakerhetskopiaJobb(context, tvinga = false)
        assertTrue(minnesMapp.poster.isEmpty())
    }

    @Test
    fun tvingaKopierarOavsettIntervall() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        installningar.sattAutoBackupIntervall(30)
        installningar.sattAutoBackupResultat(true, System.currentTimeMillis())
        seedKop()
        korSakerhetskopiaJobb(context, tvinga = true)
        assertEquals(1, minnesMapp.poster.size)
        assertTrue(minnesMapp.poster[0].first.namn.startsWith(AUTO_FILPREFIX))
        assertTrue(minnesMapp.poster[0].second.size() > 20)
    }

    @Test
    fun tvaKorningarSammaDagGerInteDubbletter() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        installningar.sattAutoBackupIntervall(7)
        seedKop()
        val idag = LocalDate.of(2026, 10, 13)
        korSakerhetskopiaJobb(context, tvinga = true, idag = idag)
        korSakerhetskopiaJobb(context, tvinga = true, idag = idag)
        assertEquals(1, minnesMapp.poster.size)
    }

    @Test
    fun workManagerKorWorkerTillSlut() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        seedKop()
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        if (testDriver != null) {
            val request =
                OneTimeWorkRequestBuilder<SakerhetskopiaWorker>()
                    .setInputData(workDataOf(SakerhetskopiaWorker.TVINGA to true))
                    .build()
            val wm = WorkManager.getInstance(context)
            wm.enqueue(request).result.get()
            testDriver.setAllConstraintsMet(request.id)
            val info = wm.getWorkInfoById(request.id).get()
            assertEquals(WorkInfo.State.SUCCEEDED, info?.state)
        } else {
            assertEquals(
                androidx.work.ListenableWorker.Result.success(),
                korSakerhetskopiaJobb(context, tvinga = true),
            )
        }
        assertEquals(1, minnesMapp.poster.size)
    }
}
