package se.tmconnect.husloggen.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
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
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.karna.sakerhetskopia.MappLagring
import se.tmconnect.karna.sakerhetskopia.MappPost

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SakerhetskopiaWorkerTest {
    private lateinit var context: Context
    private lateinit var installningar: InstallningarLager
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
        installningar = InstallningarLager(context)
        minnesMapp = MinnesMapp()
        SakerhetskopiaWorker.testMappLagring = minnesMapp
    }

    @After
    fun tearDown() {
        SakerhetskopiaWorker.testMappLagring = null
        WorkManager.getInstance(context).cancelAllWork()
    }

    private suspend fun seedPost() {
        HusDatabas.hamta(context).husDao().infogaPost(
            Post(
                datum = LocalDate.of(2024, 5, 1),
                vad = "Test",
                typ = PostTyp.UNDERHALL,
            ),
        )
    }

    @Test
    fun tvingaKopierar() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        seedPost()
        korSakerhetskopiaJobb(context, tvinga = true)
        assertEquals(1, minnesMapp.poster.size)
        assertTrue(minnesMapp.poster[0].first.namn.startsWith(HUSLOGGEN_AUTO_FILPREFIX))
    }

    @Test
    fun intervalletRespekterasUtanTvinga() = runBlocking {
        installningar.sattAutoBackupMapp("content://test-mapp", "Test")
        installningar.sattAutoBackupResultat(true, System.currentTimeMillis())
        korSakerhetskopiaJobb(context, tvinga = false)
        assertTrue(minnesMapp.poster.isEmpty())
    }

    @Test
    fun ingenKopiaNarMappSaknas() = runBlocking {
        korSakerhetskopiaJobb(context, tvinga = false)
        assertTrue(minnesMapp.poster.isEmpty())
    }
}
