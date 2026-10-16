package se.tmconnect.husloggen.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.SparaPostResult
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.pro.FakeProKalla

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusViewModelBilderTest {
    private lateinit var context: Context
    private lateinit var databas: HusDatabas
    private lateinit var repository: HusRepository
    private lateinit var viewModel: HusViewModel
    private val idag = LocalDate.of(2026, 10, 16)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = ApplicationProvider.getApplicationContext()
        nollstallFileProviderCache()
        databas =
            Room.inMemoryDatabaseBuilder(context, HusDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = HusRepository(databas.husDao(), skapaHusBildLager(context))
        val paminnelseInstallningar = PaminnelseInstallningarLager(
            context,
            HusPaminnelseKalla.DATASTORE_NAMN + "_test_bilder",
        )
        viewModel = HusViewModel(repository, paminnelseInstallningar, context, FakeProKalla(), idag)
    }

    @After
    fun tearDown() {
        databas.close()
        Dispatchers.resetMain()
    }

    private fun nollstallFileProviderCache() {
        val falt = androidx.core.content.FileProvider::class.java.getDeclaredField("sCache")
        falt.isAccessible = true
        (falt.get(null) as MutableMap<*, *>).clear()
    }

    private fun skapaTestBildUri(namn: String = "vm-bild-${System.nanoTime()}.jpg"): Uri {
        val bitmap = Bitmap.createBitmap(40, 30, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLUE)
        val fil = File(context.cacheDir, namn)
        FileOutputStream(fil).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()
        return Uri.fromFile(fil)
    }

    private suspend fun vantaPa(timeoutMs: Long = 10_000, block: suspend () -> Boolean) {
        withTimeout(timeoutMs) {
            while (!block()) {
                ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
                delay(25)
            }
        }
    }

    @Test
    fun redigeraLaggTillBildOchAvbrytBehallerBadaFilerna() = runBlocking {
        val postId =
            (repository.sparaPost(
                Post(
                    datum = idag,
                    vad = "Befintlig",
                    typ = PostTyp.UNDERHALL,
                ),
                arPro = false,
            ) as SparaPostResult.Sparad).id
        val uri1 = skapaTestBildUri()
        assertTrue(repository.laggTillBild(postId, uri1))
        val befintlig = repository.lasBilder(repository.hamtaPost(postId)!!).single()

        viewModel.visaAndraPost(postId)
        vantaPa { viewModel.postFormular.value?.redigerarId == postId }

        val uri2 = skapaTestBildUri()
        viewModel.laggTillBildUri(uri2)
        vantaPa { viewModel.postFormular.value?.bildFilnamn?.size == 2 }

        viewModel.stangPostFormular()
        assertNull(viewModel.postFormular.value)

        val post = repository.hamtaPost(postId)!!
        val bilder = repository.lasBilder(post)
        assertEquals(2, bilder.size)
        assertTrue(bilder.contains(befintlig))
        bilder.forEach { assertTrue(repository.bildfilFinns(it)) }
    }

    @Test
    fun nyPostLaggTillBildOchAvbrytRaderarFilen() = runBlocking {
        viewModel.visaNyPost()
        val uri = skapaTestBildUri()
        viewModel.laggTillBildUri(uri)
        vantaPa { viewModel.postFormular.value?.bildFilnamn?.size == 1 }

        val filnamn = viewModel.postFormular.value!!.bildFilnamn.single()
        assertTrue(repository.bildfilFinns(filnamn))

        viewModel.stangPostFormular()
        assertNull(viewModel.postFormular.value)
        assertFalse(repository.bildfilFinns(filnamn))
    }

    @Test
    fun nyPostLaggTillBildOchSparaBehallerFilen() = runBlocking {
        viewModel.visaNyPost()
        viewModel.uppdateraPostVad("Ny med bild")
        val uri = skapaTestBildUri()
        viewModel.laggTillBildUri(uri)
        vantaPa { viewModel.postFormular.value?.bildFilnamn?.size == 1 }

        val filnamn = viewModel.postFormular.value!!.bildFilnamn.single()
        assertTrue(viewModel.sparaPostFormular())
        vantaPa { viewModel.postFormular.value == null }

        val post = repository.poster.first().first()
        val bilder = repository.lasBilder(post)
        assertEquals(listOf(filnamn), bilder)
        assertTrue(repository.bildfilFinns(filnamn))
    }
}
