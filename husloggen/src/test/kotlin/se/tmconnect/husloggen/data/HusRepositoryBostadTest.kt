package se.tmconnect.husloggen.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.lasSakerhetskopiaJson
import se.tmconnect.husloggen.export.SakerhetskopiaJsonLasResult
import se.tmconnect.husloggen.regler.kanLaggaTillPost
import se.tmconnect.husloggen.regler.summeraPerAr

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryBostadTest {
    private lateinit var context: Context
    private lateinit var repository: HusRepository
    private lateinit var dao: HusDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val databas = skapaTestHusDatabas(context)
        dao = databas.husDao()
        repository = skapaHusRepository(context, databas)
    }

    @Test
    fun tvaBostaderVisarBaraValdBostadsLoggpost() = runTest {
        val a = dao.infogaBostad(Bostad(namn = "A"))
        val b = dao.infogaBostad(Bostad(namn = "B"))
        dao.infogaPost(
            Post(bostadId = a, datum = LocalDate.of(2026, 1, 1), vad = "A-post", typ = PostTyp.UNDERHALL),
        )
        dao.infogaPost(
            Post(bostadId = b, datum = LocalDate.of(2026, 1, 2), vad = "B-post", typ = PostTyp.UNDERHALL),
        )
        assertEquals(1, repository.posterForBostad(a).first().size)
        assertEquals("A-post", repository.posterForBostad(a).first().single().vad)
        assertEquals("B-post", repository.posterForBostad(b).first().single().vad)
    }

    @Test
    fun taBortSistaBostadenAvvisas() = runTest {
        val enda = dao.allaBostader().single()
        assertEquals(TaBortBostadResult.SistaBostaden, repository.taBortBostad(enda))
    }

    @Test
    fun fjardeBostadUtanProAvvisas() = runTest {
        repeat(2) { dao.infogaBostad(Bostad(namn = "Extra $it")) }
        assertEquals(3, repository.antalBostader())
        val result =
            repository.sparaBostad(Bostad(namn = "Fjärde"), arPro = false)
        assertEquals(SparaBostadResult.GransBostaderUtanPro, result)
        assertEquals(3, repository.antalBostader())
    }

    @Test
    fun gratisgrans15PosterOverAllaBostader() = runTest {
        val a = dao.allaBostader().single().id
        val b = dao.infogaBostad(Bostad(namn = "B"))
        repeat(10) { i ->
            dao.infogaPost(
                Post(
                    bostadId = a,
                    datum = LocalDate.of(2026, 1, 1).plusDays(i.toLong()),
                    vad = "A$i",
                    typ = PostTyp.UNDERHALL,
                ),
            )
        }
        repeat(5) { i ->
            dao.infogaPost(
                Post(
                    bostadId = b,
                    datum = LocalDate.of(2026, 2, 1).plusDays(i.toLong()),
                    vad = "B$i",
                    typ = PostTyp.UNDERHALL,
                ),
            )
        }
        assertEquals(15, repository.antalPoster())
        assertFalse(kanLaggaTillPost(repository.antalPoster(), arPro = false))
    }

    @Test
    fun exportOchImportBevararBostader() = runTest {
        val a = dao.allaBostader().single().id
        val b = dao.infogaBostad(Bostad(namn = "Stuga"))
        dao.infogaPost(
            Post(bostadId = b, datum = LocalDate.of(2026, 3, 1), vad = "Stugpost", typ = PostTyp.UNDERHALL),
        )
        val innehall =
            java.io.ByteArrayOutputStream().use { out ->
                Sakerhetskopia.skriv(
                    out,
                    repository.allaBostader(),
                    repository.allaUppgifter(),
                    repository.allaPoster(),
                    repository.allaMatare(),
                    repository.allaAvlasningar(),
                    repository.allaDokument(),
                    repository.allaPlaneradeKostnader(),
                    skapaHusBildLager(context),
                    skapaHusDokumentLager(context),
                )
                java.io.ByteArrayInputStream(out.toByteArray()).use { inp ->
                    (Sakerhetskopia.lasIn(inp) as se.tmconnect.husloggen.export.SakerhetskopiaLasResult.Ok).innehall
                }
            }
        assertEquals(2, innehall.bostader.size)
        val nyDatabas = skapaTestHusDatabas(context)
        val nyRepo = skapaHusRepository(context, nyDatabas)
        nyRepo.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(3, nyRepo.antalBostader())
        assertTrue(nyRepo.allaBostader().any { it.namn == "Stuga" })
    }

    @Test
    fun sammanstallningSummerarBaraValdBostadsPoster() = runTest {
        val a = dao.allaBostader().single().id
        val b = dao.infogaBostad(Bostad(namn = "B"))
        dao.infogaPost(
            Post(
                bostadId = a,
                datum = LocalDate.of(2026, 1, 1),
                vad = "A",
                typ = PostTyp.UNDERHALL,
                beloppOre = 10_000L,
            ),
        )
        dao.infogaPost(
            Post(
                bostadId = b,
                datum = LocalDate.of(2026, 1, 1),
                vad = "B",
                typ = PostTyp.UNDERHALL,
                beloppOre = 5_000L,
            ),
        )
        val posterA = repository.posterForBostad(a).first()
        val summaA = summeraPerAr(posterA)[2026]?.values?.sum() ?: 0L
        assertEquals(10_000L, summaA)
    }

    @Test
    fun tvaBostaderVisarBaraValdBostadsPlaneradeKostnad() = runTest {
        val a = dao.allaBostader().single().id
        val b = dao.infogaBostad(Bostad(namn = "B"))
        dao.infogaPlaneradKostnad(
            PlaneradKostnad(bostadId = a, titel = "Tak A", beloppOre = 1_000_000L, planeratAr = 2028),
        )
        dao.infogaPlaneradKostnad(
            PlaneradKostnad(bostadId = b, titel = "Tak B", beloppOre = 2_000_000L, planeratAr = 2029),
        )
        val planA = repository.planeradeKostnaderForBostad(a).first()
        assertEquals(1, planA.size)
        assertEquals("Tak A", planA.single().titel)
        assertEquals(1_000_000L, planA.single().beloppOre)
        val summaA = planA.sumOf { it.beloppOre }
        assertEquals(1_000_000L, summaA)
    }

    @Test
    fun importUtanBostaderHamnarPaForvald() = runTest {
        val gammal =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-01-01T00:00:00Z",
                uppgifter = emptyList(),
                poster = listOf(
                    se.tmconnect.husloggen.export.SakerhetskopiaPost(
                        datum = LocalDate.of(2026, 4, 1),
                        vad = "Gammal",
                        typ = "UNDERHALL",
                    ),
                ),
            )
        repository.importeraSakerhetskopia(gammal, emptyMap())
        val post = repository.allaPoster().single()
        assertEquals(repository.hamtaForvaldBostadId(), post.bostadId)
    }
}
