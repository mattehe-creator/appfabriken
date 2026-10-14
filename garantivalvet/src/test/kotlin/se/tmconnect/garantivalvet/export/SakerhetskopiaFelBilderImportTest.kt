package se.tmconnect.garantivalvet.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
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
import se.tmconnect.garantivalvet.regler.lasFelBilder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SakerhetskopiaFelBilderImportTest {
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

    private fun skapaJpegBytes(bredd: Int = 40, hojd: Int = 30): ByteArray {
        val bitmap = Bitmap.createBitmap(bredd, hojd, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLUE)
        return ByteArrayOutputStream().use { ut ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ut)
            ut.toByteArray()
        }
    }

    private fun sparaFelBildPaDisk(): Pair<String, ByteArray> {
        val bytes = skapaJpegBytes()
        val filnamn = kvittoLager.sparaFranBytes(bytes, "fel.jpg")
        return filnamn to bytes
    }

    @Test
    fun rundturMedFelBilderOchReklamationsUtfall() = runTest {
        val (felFil, felBytes) = sparaFelBildPaDisk()
        val kop =
            Kop(
                vad = "Tvättmaskin",
                kopdatum = LocalDate.of(2024, 3, 1),
                garantiManader = 24,
                reklamerad = LocalDate.of(2026, 9, 1),
                reklamationUtfall = "AVSLAG",
                reklamationUtfallDatum = LocalDate.of(2026, 9, 20),
                felBilder = felFil,
            )
        val zipBytes = ByteArrayOutputStream().use { ut ->
            Sakerhetskopia.skriv(ut, listOf(kop), kvittoLager)
            ut.toByteArray()
        }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) } as SakerhetskopiaLasResult.Ok
        assertEquals(listOf(felFil), las.innehall.kop.single().felBilder)
        assertArrayEquals(felBytes, las.kvittoFiler[felFil])

        val resultat = repository.importeraSakerhetskopia(las.innehall, las.kvittoFiler)
        assertEquals(1, resultat.tillagda)
        val sparat = repository.allaKop().single()
        assertEquals("AVSLAG", sparat.reklamationUtfall)
        assertEquals(LocalDate.of(2026, 9, 20), sparat.reklamationUtfallDatum)
        val importeradeFel = lasFelBilder(sparat.felBilder)
        assertEquals(1, importeradeFel.size)
        assertNotEquals(felFil, importeradeFel.single())
        assertTrue(File(context.filesDir, "kvitton/${importeradeFel.single()}").isFile)
        assertArrayEquals(felBytes, kvittoLager.lasRåBytes(importeradeFel.single())!!)
    }

    @Test
    fun importeraHopparDubblett() = runTest {
        repository.spara(
            Kop(
                vad = "Samma",
                kopdatum = LocalDate.of(2024, 1, 1),
                garantiManader = 12,
                reklamationUtfall = "LOST",
                reklamationUtfallDatum = LocalDate.of(2025, 1, 2),
            ),
        )
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-13",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Samma",
                            kopdatum = LocalDate.of(2024, 1, 1),
                            garantiManader = 12,
                            reklamationUtfall = "LOST",
                            reklamationUtfallDatum = LocalDate.of(2025, 1, 2),
                        ),
                    ),
            )
        val resultat = repository.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(0, resultat.tillagda)
        assertEquals(1, resultat.hoppadeOver)
    }

    @Test
    fun saknadFelBildGerFelUtanImport() = runTest {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-13",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Ny",
                            kopdatum = LocalDate.of(2025, 1, 1),
                            garantiManader = 12,
                            felBilder = listOf("saknas.jpg"),
                        ),
                    ),
            )
        var krasch = false
        try {
            repository.importeraSakerhetskopia(innehall, emptyMap())
        } catch (_: IllegalStateException) {
            krasch = true
        }
        assertTrue(krasch)
        assertEquals(0, repository.antal())
    }

    @Test
    fun gammalVersion1ZipUtanNyaFaltGarAttImportera() = runTest {
        val json = """{"version":1,"skapad":"x","kop":[{"vad":"A","kopdatum":"2025-01-01","garantiManader":12}]}"""
        val zipBytes = ByteArrayOutputStream().use { ut ->
            java.util.zip.ZipOutputStream(ut).use { zip ->
                zip.putNextEntry(java.util.zip.ZipEntry(SAKERHETSKOPIA_KOP_JSON))
                zip.write(json.toByteArray())
                zip.closeEntry()
            }
            ut.toByteArray()
        }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) } as SakerhetskopiaLasResult.Ok
        assertEquals(emptyList<String>(), las.innehall.kop.single().felBilder)
        assertEquals(null, las.innehall.kop.single().reklamationUtfall)
        val resultat = repository.importeraSakerhetskopia(las.innehall, las.kvittoFiler)
        assertEquals(1, resultat.tillagda)
        assertFalse(lasFelBilder(repository.allaKop().single().felBilder).isNotEmpty())
    }

    @Test
    fun galleriFelBildRundturViaKvittoLager() = runTest {
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.RED)
        val kalla = File(context.cacheDir, "galleri-fel.jpg")
        FileOutputStream(kalla).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        val filnamn = kvittoLager.sparaFelBild(Uri.fromFile(kalla))!!
        val kop =
            Kop(
                vad = "Med felbild",
                kopdatum = LocalDate.of(2024, 6, 1),
                garantiManader = 12,
                felBilder = filnamn,
            )
        val zipBytes = ByteArrayOutputStream().use { ut ->
            Sakerhetskopia.skriv(ut, listOf(kop), kvittoLager)
            ut.toByteArray()
        }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) } as SakerhetskopiaLasResult.Ok
        assertEquals(1, las.kvittoFiler.size)
        repository.importeraSakerhetskopia(las.innehall, las.kvittoFiler)
        assertEquals(1, lasFelBilder(repository.allaKop().single().felBilder).size)
    }
}
