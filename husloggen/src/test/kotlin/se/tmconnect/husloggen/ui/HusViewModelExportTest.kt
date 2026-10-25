package se.tmconnect.husloggen.ui

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.data.skapaHusRepository
import se.tmconnect.husloggen.data.skapaTestHusDatabas
import se.tmconnect.husloggen.export.AvlasningCsvTexter
import se.tmconnect.husloggen.export.HusHistorikTexter
import se.tmconnect.husloggen.export.KommandeUnderhallCsvTexter
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.pro.FakeProKalla

/**
 * Exporterna läser databasen när filen skrivs. Ingen samlar in ViewModelns flöden här, precis som
 * när Android har startat om appen medan systemets filväljare var öppen.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusViewModelExportTest {
    private lateinit var context: Context
    private lateinit var databas: HusDatabas
    private lateinit var repository: HusRepository
    private lateinit var installningar: InstallningarLager
    private lateinit var viewModel: HusViewModel

    /** Långt före dagens verkliga datum, så att en export som läser systemklockan syns. */
    private val idag = LocalDate.of(2020, 1, 15)
    private var bostadId = 0L
    private val sv = Locale("sv", "SE")

    @Before
    fun setUp() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = ApplicationProvider.getApplicationContext()
        databas = skapaTestHusDatabas(context)
        repository = skapaHusRepository(context, databas)
        installningar = InstallningarLager(context)
        bostadId = repository.allaBostader().first().id
        installningar.sattValdBostadId(bostadId)
        viewModel = HusViewModel(
            repository,
            PaminnelseInstallningarLager(context, HusPaminnelseKalla.DATASTORE_NAMN + "_test_export"),
            installningar,
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

    private suspend fun CoroutineScope.vantaPaMeddelande(starta: () -> Unit): HusUiMeddelande {
        val meddelande = CompletableDeferred<HusUiMeddelande>()
        val lyssnare = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            meddelande.complete(viewModel.meddelanden.first())
        }
        starta()
        try {
            return withTimeout(10_000) {
                while (!meddelande.isCompleted) {
                    ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
                    delay(25)
                }
                meddelande.await()
            }
        } finally {
            lyssnare.cancel()
        }
    }

    private fun registreraFil(namn: String): Pair<Uri, ByteArrayOutputStream> {
        val uri = Uri.parse("content://test.export/$namn")
        val utdata = ByteArrayOutputStream()
        shadowOf(context.contentResolver).registerOutputStream(uri, utdata)
        return uri to utdata
    }

    private fun registreraFelandeFil(namn: String): Uri {
        val uri = Uri.parse("content://test.export/$namn")
        // Skrivningen misslyckas, som när lagringsplatsen är full eller leverantören försvinner.
        val felande = object : OutputStream() {
            override fun write(b: Int): Unit = throw IOException("Ingen skrivrätt")

            override fun write(b: ByteArray, off: Int, len: Int): Unit = throw IOException("Ingen skrivrätt")
        }
        shadowOf(context.contentResolver).registerOutputStream(uri, felande)
        return uri
    }

    private fun historikTexter() = HusHistorikTexter(
        rubrik = "Husets historik",
        kolumnDatum = "Datum",
        kolumnBeskrivning = "Beskrivning",
        kolumnTyp = "Typ",
        kolumnBelopp = "Belopp",
        kolumnForbattring = "Förbättring",
        arSummaRubrik = { "År $it" },
        typSummaRad = { t, b -> "$t: $b" },
        totalt = "Totalt",
        gransJa = "Ja",
        gransNej = "Nej",
        skattRubrik = "Skatt",
        skattText = "Info",
        kallorRubrik = "Källor",
        kallorText = "Skatteverket",
    )

    private fun kommandeTexter() = KommandeUnderhallCsvTexter(
        kolumnAr = "År",
        kolumnUppgift = "Uppgift",
        kolumnDatum = "Datum",
        kolumnKostnad = "Kostnad",
        arSummaRubrik = { "Summa $it" },
        okandKostnad = "Okänd",
    )

    private fun sparaHistorikCsv(uri: Uri) = viewModel.sparaHusHistorikCsv(
        uri = uri,
        texter = historikTexter(),
        locale = sv,
        typEtikett = { it.name },
        forbattringEtikett = { it.name },
        datumEtikett = { it.datum.toString() },
        beloppEtikett = { "${it.beloppOre ?: 0}" },
    )

    @Test
    fun historikCsvHamtarPosterFranDatabasenUtanInsamling() = runBlocking {
        repository.sparaPost(
            Post(bostadId = bostadId, datum = idag, vad = "Byte av takpannor", typ = PostTyp.OMBYGGNAD),
            arPro = false,
        )
        assertTrue(viewModel.poster.value.isEmpty())
        val (uri, utdata) = registreraFil("historik.csv")

        val meddelande = vantaPaMeddelande { sparaHistorikCsv(uri) }

        assertEquals(HusUiMeddelande.HistorikCsvSparad, meddelande)
        assertTrue(utdata.toString("UTF-8").contains("Byte av takpannor"))
    }

    @Test
    fun historikCsvVisarFelNarFilenInteKanOppnas() = runBlocking {
        val uri = registreraFelandeFil("nekad.csv")
        assertEquals(HusUiMeddelande.HistorikCsvMisslyckades, vantaPaMeddelande { sparaHistorikCsv(uri) })
    }

    @Test
    fun historikPdfVisarFelNarFilenInteKanOppnas() = runBlocking {
        val uri = registreraFelandeFil("nekad.pdf")
        val meddelande = vantaPaMeddelande {
            viewModel.sparaHusHistorikPdf(
                uri = uri,
                texter = historikTexter(),
                locale = sv,
                typEtikett = { it.name },
                forbattringEtikett = { it.name },
                datumEtikett = { it.datum.toString() },
                beloppEtikett = { "${it.beloppOre ?: 0}" },
            )
        }
        assertEquals(HusUiMeddelande.HistorikPdfMisslyckades, meddelande)
    }

    @Test
    fun kommandeUnderhallCsvHamtarUppgifterFranDatabasenOchAnvanderKlockan() = runBlocking {
        repository.sparaUppgift(
            Uppgift(
                bostadId = bostadId,
                namn = "Rensa hängrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2020, 3, 1),
            ),
        )
        val (uri, utdata) = registreraFil("kommande.csv")

        val meddelande = vantaPaMeddelande {
            viewModel.sparaKommandeUnderhallCsv(uri, kommandeTexter(), sv)
        }

        assertEquals(HusUiMeddelande.HistorikCsvSparad, meddelande)
        val csv = utdata.toString("UTF-8")
        assertTrue(csv, csv.contains("Rensa hängrännor"))
        // Fönstret räknas från klockans dag (2020), inte från systemets datum.
        assertTrue(csv, csv.contains("2020"))
    }

    @Test
    fun kommandeUnderhallCsvVisarFelNarFilenInteKanOppnas() = runBlocking {
        val uri = registreraFelandeFil("nekad-kommande.csv")
        val meddelande = vantaPaMeddelande {
            viewModel.sparaKommandeUnderhallCsv(uri, kommandeTexter(), sv)
        }
        assertEquals(HusUiMeddelande.HistorikCsvMisslyckades, meddelande)
    }

    @Test
    fun sakerhetskopiaSomInteKanSparasVisarEgetMeddelande() = runBlocking {
        val uri = registreraFelandeFil("nekad.zip")
        val meddelande = vantaPaMeddelande { viewModel.sparaSakerhetskopia(uri) }
        assertEquals(HusUiMeddelande.SakerhetskopiaKundeInteSparas, meddelande)
    }

    @Test
    fun avlasningCsvForSaknadMatareVisarFel() = runBlocking {
        val (uri, _) = registreraFil("matare.csv")
        val meddelande = vantaPaMeddelande {
            viewModel.sparaAvlasningCsv(
                uri = uri,
                matareId = 9_999L,
                texter = AvlasningCsvTexter(
                    kolumnDatum = "Datum",
                    kolumnVarde = "Värde",
                    kolumnEnhet = "Enhet",
                    kolumnForbrukning = "Förbrukning",
                    kolumnEnhetKort = "E",
                ),
                locale = sv,
            )
        }
        assertEquals(HusUiMeddelande.AvlasningCsvMisslyckades, meddelande)
    }
}
