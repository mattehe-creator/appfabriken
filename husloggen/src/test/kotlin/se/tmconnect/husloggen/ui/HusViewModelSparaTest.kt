package se.tmconnect.husloggen.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.SparaPostResult
import se.tmconnect.husloggen.data.skapaHusRepository
import se.tmconnect.husloggen.data.skapaTestHusDatabas
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.husloggen.regler.GRATIS_MAX_POSTER
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.pro.FakeProKalla

/** Filväljare i test: filerna ligger i cache, typen styrs av filändelsen. */
class TestDokumentProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    private fun fil(uri: Uri): File = File(context!!.cacheDir, uri.lastPathSegment!!)

    override fun getType(uri: Uri): String =
        if (uri.lastPathSegment!!.endsWith(".pdf")) "application/pdf" else "text/plain"

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor =
        ParcelFileDescriptor.open(fil(uri), ParcelFileDescriptor.MODE_READ_ONLY)

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor =
        AssetFileDescriptor(openFile(uri, mode), 0, fil(uri).length())

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusViewModelSparaTest {
    private lateinit var context: Context
    private lateinit var databas: HusDatabas
    private lateinit var repository: HusRepository
    private lateinit var viewModel: HusViewModel
    private val idag = LocalDate.of(2026, 10, 16)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = ApplicationProvider.getApplicationContext()
        databas = skapaTestHusDatabas(context)
        repository = skapaHusRepository(context, databas)
        viewModel = HusViewModel(
            repository,
            PaminnelseInstallningarLager(context, HusPaminnelseKalla.DATASTORE_NAMN + "_test_spara"),
            InstallningarLager(context),
            context,
            FakeProKalla(),
            { idag },
        )
    }

    @After
    fun tearDown() {
        databas.close()
        Dispatchers.resetMain()
    }

    private suspend fun vantaPa(timeoutMs: Long = 10_000, block: suspend () -> Boolean) {
        withTimeout(timeoutMs) {
            while (!block()) {
                ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
                delay(25)
            }
        }
    }

    private suspend fun fyllTillGransen() {
        repeat(GRATIS_MAX_POSTER) { i ->
            val resultat = repository.sparaPost(
                Post(datum = idag, vad = "Post $i", typ = PostTyp.UNDERHALL),
                arPro = false,
            )
            assertTrue(resultat is SparaPostResult.Sparad)
        }
    }

    @Test
    fun nyPostSparPaGransenNarGratisgransenArNadd() = runBlocking {
        fyllTillGransen()
        // Utan insamling stod antalet kvar på 0 och "Ny post" öppnade formuläret ändå.
        vantaPa { !viewModel.kanLaggaTillNyPost() }
    }

    @Test
    fun sparaPostVidGransenVisarProDialog() = runBlocking {
        fyllTillGransen()
        viewModel.visaNyPost()
        viewModel.uppdateraPostVad("Sextonde")
        assertTrue(viewModel.sparaPostFormular())
        vantaPa { viewModel.proDialogSynlig.value }
        assertNotNull(viewModel.postFormular.value)
        assertEquals(GRATIS_MAX_POSTER, repository.antalPoster())
    }

    @Test
    fun dubbeltryckPaSparaGerEnPost() = runBlocking {
        viewModel.visaNyPost()
        viewModel.uppdateraPostVad("Rensa rännor")
        viewModel.sparaPostFormular()
        viewModel.sparaPostFormular()
        vantaPa { viewModel.postFormular.value == null }
        delay(500)
        assertEquals(1, repository.antalPoster())
    }

    @Test
    fun misslyckatFilbyteBehallerTidigareDokumentfil() = runBlocking {
        Robolectric.setupContentProvider(TestDokumentProvider::class.java, "test.dokument")
        File(context.cacheDir, "kvitto.pdf").writeBytes("%PDF-1.4 test".toByteArray())
        File(context.cacheDir, "anteckning.txt").writeText("inte tillåten")

        viewModel.visaNyttDokument()
        viewModel.valjDokumentFil(Uri.parse("content://test.dokument/kvitto.pdf"))
        vantaPa { viewModel.dokumentFormular.value?.filnamn != null }
        val forsta = viewModel.dokumentFormular.value!!.filnamn!!
        assertTrue(repository.dokumentfilFinns(forsta))

        val meddelande = CompletableDeferred<HusUiMeddelande>()
        val lyssnare = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            meddelande.complete(viewModel.meddelanden.first())
        }
        viewModel.valjDokumentFil(Uri.parse("content://test.dokument/anteckning.txt"))
        assertEquals(HusUiMeddelande.DokumentOgiltigTyp, withTimeout(10_000) { meddelande.await() })
        lyssnare.cancel()

        assertEquals(forsta, viewModel.dokumentFormular.value?.filnamn)
        assertTrue(repository.dokumentfilFinns(forsta))
        assertFalse(repository.allaDokument().any())
    }
}
