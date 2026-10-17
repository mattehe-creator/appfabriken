package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaAvlasning
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult
import se.tmconnect.husloggen.export.SakerhetskopiaMatare
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryMatareImportTest {
    private lateinit var repository: HusRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databas =
            Room.inMemoryDatabaseBuilder(context, HusDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = skapaHusRepository(context, databas)
    }

    @Test
    fun exportImportMatareOchAvlasningar() = runTest {
        val matareId = repository.sparaMatare(Matare(namn = "El", enhet = "kWh"))
        repository.sparaAvlasning(
            Avlasning(
                matareId = matareId,
                datum = LocalDate.of(2026, 1, 1),
                varde = 1_000_000,
            ),
        )
        val matare = repository.allaMatare()
        val avlasningar = repository.allaAvlasningar()
        val zipBytes =
            ByteArrayOutputStream().use { ut ->
                Sakerhetskopia.skriv(
                    ut,
                    emptyList(),
                    emptyList(),
                    matare,
                    avlasningar,
                    emptyList(),
                    skapaHusBildLager(ApplicationProvider.getApplicationContext()),
                    skapaHusDokumentLager(ApplicationProvider.getApplicationContext()),
                )
                ut.toByteArray()
            }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
        assertEquals(true, las is SakerhetskopiaLasResult.Ok)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databas2 =
            Room.inMemoryDatabaseBuilder(context, HusDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        val repo2 = skapaHusRepository(context, databas2)
        repo2.importeraSakerhetskopia(
            (las as SakerhetskopiaLasResult.Ok).innehall,
            las.bildFiler,
            las.dokumentFiler,
        )
        val efterMatare = repo2.allaMatare()
        val efterAvlasningar = repo2.allaAvlasningar()
        assertEquals(1, efterMatare.size)
        assertEquals("El", efterMatare.single().namn)
        assertEquals(1, efterAvlasningar.size)
        assertEquals(1_000_000L, efterAvlasningar.single().varde)
    }

    @Test
    fun jsonUtanMatareKanImporteras() = runTest {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-17",
                uppgifter = emptyList(),
                poster = emptyList(),
            )
        repository.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(0, repository.allaMatare().size)
    }
}
