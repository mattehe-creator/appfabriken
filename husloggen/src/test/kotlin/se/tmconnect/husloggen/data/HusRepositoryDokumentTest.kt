package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaDokument
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult
import se.tmconnect.husloggen.regler.grupperaDokumentPerKategori

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryDokumentTest {
    private lateinit var context: Context
    private lateinit var databas: HusDatabas
    private lateinit var repository: HusRepository
    private lateinit var dokumentLager: DokumentLager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        databas = skapaTestHusDatabas(context)
        repository = skapaHusRepository(context, databas)
        dokumentLager = skapaHusDokumentLager(context)
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun sparaManualDiskmaskinVisasUnderManual() = runTest {
        val filnamn = dokumentLager.sparaFranBytes(byteArrayOf(1, 2, 3), "manual.pdf")
        repository.sparaDokument(
            Dokument(
                titel = "Manual diskmaskin",
                kategori = DokumentKategori.MANUAL,
                filnamn = filnamn,
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2026, 10, 1),
            ),
        )
        val grupper = grupperaDokumentPerKategori(repository.allaDokument())
        assertEquals(DokumentKategori.MANUAL, grupper.single().kategori)
        assertEquals("Manual diskmaskin", grupper.single().dokument.single().titel)
    }

    @Test
    fun taBortTarBortFil() = runTest {
        val filnamn = dokumentLager.sparaFranBytes(byteArrayOf(9), "x.pdf")
        val id =
            repository.sparaDokument(
                Dokument(
                    titel = "Temp",
                    kategori = DokumentKategori.OVRIGT,
                    filnamn = filnamn,
                    mimeTyp = "application/pdf",
                    tillagd = LocalDate.of(2026, 1, 1),
                ),
            )
        val rad = repository.hamtaDokument(id)!!
        repository.taBortDokument(rad)
        assertEquals(0, repository.allaDokument().size)
        assertFalse(dokumentLager.filFinns(filnamn))
    }

    @Test
    fun importUtanDokumentFungerar() = runTest {
        val innehall =
            SakerhetskopiaInnehall(
                version = 2,
                skapad = "2026-10-17",
                uppgifter = emptyList(),
                poster = emptyList(),
            )
        repository.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(0, repository.allaDokument().size)
    }

    @Test
    fun exportImportBevararMetadata() = runTest {
        val filBytes = ByteArray(1024) { it.toByte() }
        val filnamn = dokumentLager.sparaFranBytes(filBytes, "manual.pdf")
        val dokument =
            Dokument(
                titel = "Manual diskmaskin",
                kategori = DokumentKategori.MANUAL,
                anteckning = "Kök",
                filnamn = filnamn,
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2026, 9, 15),
            )
        repository.sparaDokument(dokument)
        val sparade = repository.allaDokument().single()
        val zipBytes =
            ByteArrayOutputStream().use { ut ->
                Sakerhetskopia.skriv(
                    ut,
                    repository.allaBostader(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    listOf(sparade),
                    skapaHusBildLager(context),
                    dokumentLager,
                )
                ut.toByteArray()
            }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) } as SakerhetskopiaLasResult.Ok
        val export = las.innehall.dokument.single()
        assertEquals("Manual diskmaskin", export.titel)
        assertEquals(DokumentKategori.MANUAL.name, export.kategori)
        databas.close()
        val databas2 = skapaTestHusDatabas(context)
        val repo2 = skapaHusRepository(context, databas2)
        repo2.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)
        val importerad = repo2.allaDokument().single()
        assertEquals("Manual diskmaskin", importerad.titel)
        assertEquals(DokumentKategori.MANUAL, importerad.kategori)
        assertEquals("Kök", importerad.anteckning)
        assertEquals(LocalDate.of(2026, 9, 15), importerad.tillagd)
        assertTrue(repo2.dokumentfilFinns(importerad.filnamn))
        databas2.close()
    }
}
