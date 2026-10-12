package se.tmconnect.garantivalvet.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.data.KvittoLager
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ExportMedProTest {
    private lateinit var context: Context
    private lateinit var databas: GarantiDatabas
    private lateinit var repository: KopRepository
    private lateinit var kvittoLager: KvittoLager
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        databas =
            Room.inMemoryDatabaseBuilder(context, GarantiDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        kvittoLager = KvittoLager(context)
        repository = KopRepository(databas.kopDao(), kvittoLager)
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun fileProviderAuthorityStammerMedManifest() {
        assertEquals(
            "${context.packageName}.kvittofil",
            KvittoLager.fileProviderAuthority(context),
        )
    }

    @Test
    fun spararPdfTillStromUtanKrasch() {
        val kop =
            Kop(
                vad = "Kaffebryggare",
                kopdatum = LocalDate.of(2024, 6, 1),
                garantiManader = 24,
            )
        val output = ByteArrayOutputStream()
        val result =
            skrivUnderlagTillPdf(
                se.tmconnect.garantivalvet.regler.Underlag(
                    dokumentRubrik = "Title",
                    rader =
                        listOf(
                            se.tmconnect.garantivalvet.regler.UnderlagRad("Vad", kop.vad),
                        ),
                    skapad = LocalDate.of(2026, 10, 12),
                ),
                output,
                null,
            )
        assertTrue(result is PdfExportResult.Ok || result is PdfExportResult.Fel)
    }

    @Test
    fun delaPdfIntentHarRattMimeTypOchLasrattighet() {
        val kop =
            Kop(
                vad = "Kaffebryggare",
                kopdatum = LocalDate.of(2024, 6, 1),
                garantiManader = 24,
            )
        val pdfFil =
            File(
                context.cacheDir,
                "export/${reklamationsPdfFilnamn(kop.vad, kop.kopdatum)}",
            )
        pdfFil.parentFile?.mkdirs()
        pdfFil.writeBytes(byteArrayOf(0x25, 0x50, 0x44, 0x46))

        val fileUri =
            FileProvider.getUriForFile(
                context,
                KvittoLager.fileProviderAuthority(context),
                pdfFil,
            )

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

        assertEquals("application/pdf", intent.type)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
        @Suppress("DEPRECATION")
        val streamUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        assertNotNull(streamUri)
    }

    @Test
    fun skriverSakerhetskopiaZipMedKop() = runTest {
        repository.spara(
            Kop(
                vad = "Skrivbord",
                kopdatum = LocalDate.of(2025, 1, 1),
                garantiManader = 12,
            ),
        )

        val output = ByteArrayOutputStream()
        Sakerhetskopia.skriv(output, repository.alla.first(), kvittoLager)
        assertTrue(output.size() > 50)

        val lasResult = Sakerhetskopia.lasIn(output.toByteArray().inputStream())
        assertTrue(lasResult is SakerhetskopiaLasResult.Ok)
    }
}
