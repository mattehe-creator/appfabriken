package se.tmconnect.flextid.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.export.SakerhetskopiaDag
import se.tmconnect.flextid.export.SakerhetskopiaInnehall
import se.tmconnect.flextid.export.SakerhetskopiaInstallning
import se.tmconnect.flextid.export.lasSakerhetskopiaJson
import se.tmconnect.flextid.export.skrivSakerhetskopiaJson
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.beraknaSaldo

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FlextidRepositoryImportTest {
    private lateinit var databas: FlextidDatabas
    private lateinit var repository: FlextidRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas =
            Room.inMemoryDatabaseBuilder(context, FlextidDatabas::class.java)
                .allowMainThreadQueries()
                .build()
        repository = FlextidRepository(databas.flextidDao())
        runTest {
            repository.sparaInstallning(Installning())
        }
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun exportImportGerSammaDagar() = runTest {
        val dag =
            Dag(
                datum = LocalDate.of(2026, 10, 13),
                start = LocalTime.of(7, 30),
                slut = LocalTime.of(17, 0),
                rastMin = 30,
                typ = DagTyp.ARBETE,
                anteckning = "test",
            )
        repository.sparaDag(dag, arPro = true)
        val inst = repository.hamtaInstallning().copy(ingangssaldoMin = -120)
        repository.sparaInstallning(inst)

        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-18",
                installning =
                    SakerhetskopiaInstallning(
                        normaltidMinPerDag = inst.normaltidMinPerDag,
                        standardRastMin = inst.standardRastMin,
                        ingangssaldoMin = inst.ingangssaldoMin,
                        arbetsdagarMask = inst.arbetsdagarMask,
                    ),
                dagar =
                    repository.hamtaAllaDagar().map {
                        SakerhetskopiaDag(
                            datum = it.datum,
                            start = it.start,
                            slut = it.slut,
                            rastMin = it.rastMin,
                            typ = it.typ.name,
                            anteckning = it.anteckning,
                        )
                    },
            )
        val json = skrivSakerhetskopiaJson(innehall)
        val las = lasSakerhetskopiaJson(json) as se.tmconnect.flextid.export.SakerhetskopiaJsonLasResult.Ok

        repository.ersattAllData(emptyList(), Installning())
        repository.ersattAllData(
            las.innehall.dagar.map {
                Dag(
                    datum = it.datum,
                    start = it.start,
                    slut = it.slut,
                    rastMin = it.rastMin,
                    typ = DagTyp.franNamn(it.typ) ?: DagTyp.ARBETE,
                    anteckning = it.anteckning,
                )
            },
            Installning(
                normaltidMinPerDag = las.innehall.installning.normaltidMinPerDag,
                standardRastMin = las.innehall.installning.standardRastMin,
                ingangssaldoMin = las.innehall.installning.ingangssaldoMin,
                arbetsdagarMask = las.innehall.installning.arbetsdagarMask,
            ),
        )

        val efter = repository.hamtaAllaDagar().single()
        assertEquals(dag.datum, efter.datum)
        assertEquals(dag.start, efter.start)
        assertEquals(dag.slut, efter.slut)
        assertEquals(dag.rastMin, efter.rastMin)
        assertEquals(-60, beraknaSaldo(listOf(efter), repository.hamtaInstallning()))
    }
}
