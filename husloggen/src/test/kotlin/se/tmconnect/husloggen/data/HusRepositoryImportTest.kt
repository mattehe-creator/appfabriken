package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaPost
import se.tmconnect.husloggen.export.SakerhetskopiaUppgift

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryImportTest {
    private lateinit var databas: HusDatabas
    private lateinit var repository: HusRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas =
            Room.inMemoryDatabaseBuilder(context, HusDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = HusRepository(databas, databas.husDao(), skapaHusBildLager(context))
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun importeraLaggerTillOchHopparDubblett() = runTest {
        repository.sparaPost(
            Post(
                datum = LocalDate.of(2024, 1, 1),
                vad = "Samma",
                typ = PostTyp.UNDERHALL,
                beloppOre = 50L,
            ),
        )
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-16",
                uppgifter = emptyList(),
                poster =
                    listOf(
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2024, 1, 1),
                            vad = "Samma",
                            typ = PostTyp.UNDERHALL.name,
                            beloppOre = 50L,
                        ),
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2025, 2, 2),
                            vad = "Nytt",
                            typ = PostTyp.REPARATION.name,
                        ),
                    ),
            )
        val resultat = repository.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(1, resultat.tillagda)
        assertEquals(1, resultat.hoppadeOver)
        assertEquals(2, repository.antalPoster())
    }

    @Test
    fun importeraUppgiftOchKoppladPost() = runTest {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-16",
                uppgifter =
                    listOf(
                        SakerhetskopiaUppgift(
                            namn = "Service",
                            intervallManader = 12,
                            startDatum = LocalDate.of(2024, 1, 1),
                        ),
                    ),
                poster =
                    listOf(
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2026, 1, 1),
                            vad = "Utfört",
                            typ = PostTyp.UNDERHALL.name,
                            uppgiftIndex = 0,
                        ),
                    ),
            )
        repository.importeraSakerhetskopia(innehall, emptyMap())
        val poster = repository.allaPoster()
        assertEquals(1, poster.size)
        assertEquals(1, repository.allaUppgifter().size)
        assertEquals(repository.allaUppgifter().single().id, poster.single().uppgiftId)
    }
}
