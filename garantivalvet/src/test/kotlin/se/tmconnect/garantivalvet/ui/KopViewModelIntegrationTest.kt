package se.tmconnect.garantivalvet.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.data.KvittoLager
import se.tmconnect.garantivalvet.export.HemforsakringTexter
import se.tmconnect.garantivalvet.regler.ReklamationsUtfall
import se.tmconnect.garantivalvet.regler.lasFelBilder
import se.tmconnect.garantivalvet.regler.skrivFelBilder
import se.tmconnect.karna.pro.FakeProKalla

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [28])
class KopViewModelIntegrationTest {
    private lateinit var context: Context
    private lateinit var databas: GarantiDatabas
    private lateinit var repository: KopRepository
    private lateinit var kvittoLager: KvittoLager
    private lateinit var installningar: InstallningarLager
    private lateinit var viewModel: KopViewModel
    private val idag = LocalDate.of(2026, 10, 13)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = ApplicationProvider.getApplicationContext()
        val falt = androidx.core.content.FileProvider::class.java.getDeclaredField("sCache")
        falt.isAccessible = true
        (falt.get(null) as MutableMap<*, *>).clear()
        databas =
            Room.inMemoryDatabaseBuilder(context, GarantiDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        kvittoLager = KvittoLager(context)
        repository = KopRepository(databas.kopDao(), kvittoLager)
        installningar = InstallningarLager(context)
        viewModel =
            KopViewModel(
                repository = repository,
                kvittoLager = kvittoLager,
                installningarLager = installningar,
                appContext = context,
                proKalla = FakeProKalla(),
                idag = idag,
            )
    }

    @After
    fun tearDown() {
        databas.close()
        Dispatchers.resetMain()
    }

    private fun skapaBildUri(): Uri {
        val bitmap = Bitmap.createBitmap(60, 40, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.MAGENTA)
        val fil = File(context.cacheDir, "vm-fel-${System.nanoTime()}.jpg")
        FileOutputStream(fil).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
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

    private suspend fun visaDetaljOchVanta(kopId: Long) {
        viewModel.visaDetalj(kopId)
        vantaPa { viewModel.valtKop.value?.id == kopId }
    }

    private suspend fun sparaKop(vad: String = "Test"): Kop {
        val id = repository.spara(
            Kop(vad = vad, kopdatum = LocalDate.of(2024, 1, 1), garantiManader = 24),
        )
        return repository.hamta(id)!!
    }

    @Test
    fun laggTillFelBildMaxFemOchSjätteAvvisas() = runBlocking {
        val kop = sparaKop()
        val filnamn = mutableListOf<String>()
        var aktuell = kop
        repeat(5) {
            filnamn += kvittoLager.sparaFelBild(skapaBildUri())!!
            aktuell = aktuell.copy(felBilder = skrivFelBilder(filnamn))
            repository.spara(aktuell)
        }
        visaDetaljOchVanta(kop.id)
        var avvisad = false
        viewModel.laggTillFelBild(kop.id, skapaBildUri()) { avvisad = true }
        vantaPa { avvisad }
        val efter = repository.hamta(kop.id)!!
        assertEquals(5, lasFelBilder(efter.felBilder).size)
        assertEquals(filnamn.toSet(), lasFelBilder(efter.felBilder).toSet())
        val kvitton = File(context.filesDir, "kvitton").listFiles()?.map { it.name }?.toSet() ?: emptySet()
        assertEquals(filnamn.toSet(), kvitton)
    }

    @Test
    fun laggTillFelBildHamnarPaRattKopOavsettValtKop() = runBlocking {
        val kopA = sparaKop("A")
        val kopB = sparaKop("B")
        visaDetaljOchVanta(kopB.id)
        viewModel.laggTillFelBild(kopA.id, skapaBildUri())
        vantaPa { lasFelBilder(repository.hamta(kopA.id)!!.felBilder).size == 1 }
        assertTrue(lasFelBilder(repository.hamta(kopB.id)!!.felBilder).isEmpty())
    }

    @Test
    fun laggTillFelBildPaBorttagetKopRaderarFilen() = runBlocking {
        val kop = sparaKop()
        visaDetaljOchVanta(kop.id)
        val uri = skapaBildUri()
        viewModel.laggTillFelBild(kop.id, uri)
        repository.taBort(kop)
        vantaPa {
            val filer = File(context.filesDir, "kvitton").listFiles()?.filter { it.isFile } ?: emptyList()
            filer.isEmpty() || filer.none { lasFelBilder(kop.felBilder).contains(it.name) }
        }
        val kvar = File(context.filesDir, "kvitton").listFiles()?.filter { it.isFile } ?: emptyList()
        assertTrue(kvar.isEmpty())
    }

    @Test
    fun taBortFelBildTvaSnabbaGerKonsekventResultat() = runBlocking {
        val kop = sparaKop()
        val f1 = kvittoLager.sparaFelBild(skapaBildUri())!!
        val f2 = kvittoLager.sparaFelBild(skapaBildUri())!!
        repository.spara(kop.copy(felBilder = "$f1,$f2"))
        visaDetaljOchVanta(kop.id)
        viewModel.taBortFelBild(f1)
        viewModel.taBortFelBild(f2)
        vantaPa { lasFelBilder(repository.hamta(kop.id)!!.felBilder).isEmpty() }
        assertFalse(File(context.filesDir, "kvitton/$f1").isFile)
        assertFalse(File(context.filesDir, "kvitton/$f2").isFile)
    }

    @Test
    fun sattReklamationsUtfall() = runBlocking {
        val kop = sparaKop().copy(reklamerad = idag.minusDays(3))
        repository.spara(kop)
        visaDetaljOchVanta(kop.id)
        viewModel.sattReklamationsUtfall(ReklamationsUtfall.AVSLAG, idag)
        vantaPa { repository.hamta(kop.id)?.reklamationUtfall == "AVSLAG" }
        assertEquals(idag, repository.hamta(kop.id)!!.reklamationUtfallDatum)
    }

    @Test
    fun markeraReklameradNullNollstallerUtfall() = runBlocking {
        val kop =
            sparaKop().copy(
                reklamerad = idag,
                reklamationUtfall = "LOST",
                reklamationUtfallDatum = idag,
            )
        repository.spara(kop)
        visaDetaljOchVanta(kop.id)
        viewModel.markeraReklamerad(null)
        vantaPa { repository.hamta(kop.id)?.reklamerad == null }
        val efter = repository.hamta(kop.id)!!
        assertNull(efter.reklamationUtfall)
        assertNull(efter.reklamationUtfallDatum)
    }

    @Test
    fun sparaHemforsakringCsvHarBomSemikolonOchSumma() = runBlocking {
        repository.spara(
            Kop(
                vad = "TV",
                varKopt = "Butik",
                kopdatum = LocalDate.of(2024, 1, 1),
                garantiManader = 24,
                prisOre = 100_00L,
            ),
        )
        val exportDir = File(context.cacheDir, "export").also { it.mkdirs() }
        val mal = File(exportDir, "hemforsakring.csv")
        mal.delete()
        val uri = Uri.fromFile(mal)
        viewModel.sparaHemforsakringCsv(uri, listOf("Vara", "Butik", "Datum", "Pris"), "Summa")
        vantaPa { mal.isFile && mal.length() > 20 }
        val bytes = mal.readBytes()
        assertTrue(bytes.size >= 3)
        assertArrayEquals(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()), bytes.copyOf(3))
        val text = String(bytes, Charsets.UTF_8)
        assertTrue(text.contains(";"))
        assertTrue(text.trimEnd().lines().last().startsWith("Summa;"))
    }

    @Test(timeout = 30_000)
    fun sparaHemforsakringPdfRensarTempfilOchSpararPdfNarDetGar() = runBlocking {
        repository.spara(
            Kop(vad = "Stol", kopdatum = LocalDate.of(2024, 2, 1), garantiManader = 12, prisOre = 50_00L),
        )
        val exportDir = File(context.cacheDir, "export").also { it.mkdirs() }
        val mal = File(exportDir, "hemforsakring.pdf")
        mal.delete()
        val uri = Uri.fromFile(mal)
        val foreTemp = context.cacheDir.listFiles()?.count { it.name.startsWith("hemforsakring-") && it.name.endsWith(".pdf") } ?: 0
        viewModel.sparaHemforsakringPdf(
            uri,
            HemforsakringTexter("R", "I", "V", "B", "D", "P", "S", "X", "K"),
            java.util.Locale("sv", "SE"),
        )
        vantaPa {
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            viewModel.exportPdfMeddelande.value != null
        }
        val efterTemp = context.cacheDir.listFiles()?.count { it.name.startsWith("hemforsakring-") && it.name.endsWith(".pdf") } ?: 0
        assertEquals(foreTemp, efterTemp)
        if (viewModel.exportPdfMeddelande.value == ExportPdfMeddelande.Sparad) {
            assertTrue(String(mal.readBytes(), 0, 4) == "%PDF")
        }
    }
}
