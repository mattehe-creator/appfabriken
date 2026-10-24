package se.tmconnect.husloggen.paminnelse

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager

/** Aviserade nycklar som inte längre är aktuella släpps, så att mängden inte växer för evigt. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AviseradeNycklarTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val lager = PaminnelseInstallningarLager(context, "test_aviserade_" + System.nanoTime())

    @Test
    fun gamlaNycklarSlapps() = runBlocking {
        lager.laggTillAviseradeNycklar(setOf("1:2026-01-01", "2:2026-02-01"))
        lager.uppdateraAviseradeNycklar(nya = setOf("3:2026-03-01"), aktuella = setOf("2:2026-02-01", "3:2026-03-01"))
        assertEquals(setOf("2:2026-02-01", "3:2026-03-01"), lager.hamtaAviseradeNycklar())
    }

    @Test
    fun utanNyaBaraGallring() = runBlocking {
        lager.laggTillAviseradeNycklar(setOf("a", "b"))
        lager.uppdateraAviseradeNycklar(nya = emptySet(), aktuella = setOf("b", "c"))
        assertEquals(setOf("b"), lager.hamtaAviseradeNycklar())
    }
}
