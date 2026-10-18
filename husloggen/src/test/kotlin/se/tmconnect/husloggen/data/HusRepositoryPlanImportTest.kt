package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult
import se.tmconnect.husloggen.export.SakerhetskopiaPlaneradKostnad

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryPlanImportTest {
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
    fun exportImportPlaneradeKostnader() = runTest {
        repository.sparaPlaneradKostnad(
            PlaneradKostnad(titel = "Tak", beloppOre = 4_000_000L, planeratAr = 2028),
        )
        val plan = repository.allaPlaneradeKostnader()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val zipBytes =
            ByteArrayOutputStream().use { ut ->
                Sakerhetskopia.skriv(
                    ut,
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    skapaHusBildLager(context),
                    skapaHusDokumentLager(context),
                    plan,
                )
                ut.toByteArray()
            }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
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
        val efter = repo2.allaPlaneradeKostnader()
        assertEquals(1, efter.size)
        assertEquals("Tak", efter.single().titel)
        assertEquals(4_000_000L, efter.single().beloppOre)
        assertEquals(2028, efter.single().planeratAr)
    }

    @Test
    fun jsonUtanPlaneradeKostnaderKanImporteras() = runTest {
        val innehall =
            SakerhetskopiaInnehall(
                version = 3,
                skapad = "2026-10-18",
                uppgifter = emptyList(),
                poster = emptyList(),
            )
        repository.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(0, repository.allaPlaneradeKostnader().size)
    }

    @Test
    fun genomfordBehallerPlanVidGrans() = runTest {
        repeat(15) { i ->
            repository.sparaPost(
                Post(datum = java.time.LocalDate.of(2026, 1, 1), vad = "P$i", typ = PostTyp.UNDERHALL),
                arPro = false,
            )
        }
        val planId =
            repository.sparaPlaneradKostnad(
                PlaneradKostnad(titel = "Tak", beloppOre = 5_000_000L, planeratAr = 2028),
            )
        val nyPost =
            Post(
                datum = java.time.LocalDate.of(2026, 6, 1),
                vad = "Tak",
                typ = PostTyp.REPARATION,
                beloppOre = 5_000_000L,
            )
        assertEquals(SparaPostResult.GransNadd, repository.sparaPost(nyPost, arPro = false))
        assertEquals(1, repository.allaPlaneradeKostnader().size)

        val sparad = repository.sparaPost(nyPost, arPro = true) as SparaPostResult.Sparad
        repository.hamtaPlaneradKostnad(planId)?.let { repository.taBortPlaneradKostnad(it) }
        assertEquals(0, repository.allaPlaneradeKostnader().size)
        assertEquals(16, repository.antalPoster())
        assertEquals(sparad.id > 0L, true)
    }
}
