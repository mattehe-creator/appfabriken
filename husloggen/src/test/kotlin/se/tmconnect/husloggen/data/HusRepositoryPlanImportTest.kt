package se.tmconnect.husloggen.data

import android.content.Context
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryPlanImportTest {
    private lateinit var repository: HusRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val databas = skapaTestHusDatabas(context)
        repository = skapaHusRepository(context, databas)
    }

    @Test
    fun exportImportPlaneradeKostnader() = runTest {
        val bostadId = repository.hamtaForvaldBostadId()
        repository.sparaPlaneradKostnad(
            PlaneradKostnad(bostadId = bostadId, titel = "Tak", beloppOre = 4_000_000L, planeratAr = 2028),
        )
        val plan = repository.allaPlaneradeKostnader()
        val zipBytes =
            ByteArrayOutputStream().use { ut ->
                Sakerhetskopia.skriv(
                    ut,
                    repository.allaBostader(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    plan,
                    skapaHusBildLager(context),
                    skapaHusDokumentLager(context),
                )
                ut.toByteArray()
            }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
        val databas2 = skapaTestHusDatabas(context)
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
        val bostadId = repository.hamtaForvaldBostadId()
        repeat(15) { i ->
            repository.sparaPost(
                Post(
                    bostadId = bostadId,
                    datum = java.time.LocalDate.of(2026, 1, 1),
                    vad = "P$i",
                    typ = PostTyp.UNDERHALL,
                ),
                arPro = false,
            )
        }
        val planId =
            repository.sparaPlaneradKostnad(
                PlaneradKostnad(bostadId = bostadId, titel = "Tak", beloppOre = 5_000_000L, planeratAr = 2028),
            )
        val nyPost =
            Post(
                bostadId = bostadId,
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
