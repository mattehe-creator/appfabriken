package se.tmconnect.flextid.paminnelse

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PaminnelseWorkerTest {
    private lateinit var context: Context
    private lateinit var installningar: FlextidPaminnelseInstallningarLager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        installningar = FlextidPaminnelseInstallningarLager(context)
        runBlocking {
            installningar.sattStamplaUtPa(false)
            installningar.sattStamplaInPa(false)
        }
    }

    @Test
    fun ingenAviseringNarPaminnelserAv() = runBlocking {
        val worker = TestListenableWorkerBuilder<PaminnelseWorker>(context).build()
        val result = worker.startWork().get()

        assertEquals(ListenableWorker.Result.success(), result)
        assertTrue(installningar.karnaInstallningar.hamtaAviseradeNycklar().isEmpty())
    }
}
