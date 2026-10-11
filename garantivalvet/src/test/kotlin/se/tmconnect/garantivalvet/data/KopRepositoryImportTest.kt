package se.tmconnect.garantivalvet.data

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
import se.tmconnect.garantivalvet.export.SakerhetskopiaInnehall
import se.tmconnect.garantivalvet.export.SakerhetskopiaKop

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class KopRepositoryImportTest {
    private lateinit var databas: GarantiDatabas
    private lateinit var repository: KopRepository
    private lateinit var kvittoLager: KvittoLager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
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
    fun importeraLaggerTillOchHopparDubblett() = runTest {
        repository.spara(
            Kop(
                vad = "Samma",
                varKopt = "Butik",
                kopdatum = LocalDate.of(2024, 1, 1),
                garantiManader = 12,
                prisOre = 50L,
            ),
        )
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-11",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Samma",
                            varKopt = "Butik",
                            kopdatum = LocalDate.of(2024, 1, 1),
                            garantiManader = 12,
                            prisOre = 50L,
                        ),
                        SakerhetskopiaKop(
                            vad = "Nytt",
                            kopdatum = LocalDate.of(2025, 2, 2),
                            garantiManader = 6,
                        ),
                    ),
            )
        val resultat = repository.importeraSakerhetskopia(innehall, emptyMap())
        assertEquals(1, resultat.tillagda)
        assertEquals(1, resultat.hoppadeOver)
        assertEquals(2, repository.antal())
    }

    @Test
    fun importeraKopierarKvitto() = runTest {
        val bytes = byteArrayOf(9, 8, 7)
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-11",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Med bild",
                            kopdatum = LocalDate.of(2024, 3, 3),
                            garantiManader = 12,
                            kvittoFil = "kvitto.jpg",
                        ),
                    ),
            )
        val resultat =
            repository.importeraSakerhetskopia(
                innehall,
                mapOf("kvitto.jpg" to bytes),
            )
        assertEquals(1, resultat.tillagda)
        val sparat = repository.allaKop().single()
        assertEquals("Med bild", sparat.vad)
        assertEquals(bytes.toList(), kvittoLager.lasRåBytes(sparat.kvittoFil!!)!!.toList())
    }
}
