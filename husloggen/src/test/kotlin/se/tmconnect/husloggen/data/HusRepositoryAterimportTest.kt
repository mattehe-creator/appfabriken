package se.tmconnect.husloggen.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
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
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult

/** Samma säkerhetskopia importerad två gånger, fält som ska följa med, och filer vid radering. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryAterimportTest {
    private lateinit var context: Context
    private lateinit var repository: HusRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = skapaHusRepository(context, skapaTestHusDatabas(context))
    }

    private suspend fun exportera(repo: HusRepository): SakerhetskopiaLasResult.Ok =
        ByteArrayOutputStream().use { ut ->
            Sakerhetskopia.skriv(
                ut,
                repo.allaBostader(),
                repo.allaUppgifter(),
                repo.allaPoster(),
                repo.allaMatare(),
                repo.allaAvlasningar(),
                repo.allaDokument(),
                repo.allaPlaneradeKostnader(),
                skapaHusBildLager(context),
                skapaHusDokumentLager(context),
            )
            ByteArrayInputStream(ut.toByteArray()).use { Sakerhetskopia.lasIn(it) as SakerhetskopiaLasResult.Ok }
        }

    private suspend fun fyllMedData() {
        val bostad = repository.hamtaForvaldBostadId()
        val matare = repository.sparaMatare(Matare(bostadId = bostad, namn = "El", enhet = "kWh", prisOrePerEnhet = 250))
        repository.sparaAvlasning(Avlasning(matareId = matare, datum = LocalDate.of(2026, 1, 1), varde = 1_000_000))
        repository.sparaUppgift(
            Uppgift(
                bostadId = bostad,
                namn = "Rensa hängrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 4, 1),
                paminnelse = false,
            ),
        )
        repository.sparaPlaneradKostnad(
            PlaneradKostnad(bostadId = bostad, titel = "Nytt tak", beloppOre = 20_000_000, planeratAr = 2030),
        )
    }

    @Test
    fun prisOchPaminnelseFoljerMed() = runTest {
        fyllMedData()
        val las = exportera(repository)
        val ny = skapaHusRepository(context, skapaTestHusDatabas(context))
        ny.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)
        assertEquals(250L, ny.allaMatare().single().prisOrePerEnhet)
        assertFalse(ny.allaUppgifter().single().paminnelse)
    }

    @Test
    fun sammaFilTvaGangerGerInteDubbletter() = runTest {
        fyllMedData()
        val las = exportera(repository)
        val ny = skapaHusRepository(context, skapaTestHusDatabas(context))
        ny.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)
        ny.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)
        assertEquals(1, ny.antalBostader())
        assertEquals(1, ny.allaMatare().size)
        assertEquals(1, ny.allaAvlasningar().size)
        assertEquals(1, ny.allaUppgifter().size)
        assertEquals(1, ny.allaPlaneradeKostnader().size)
    }

    @Test
    fun tomStandardbostadTarOverForstaBostadenIFilen() = runTest {
        val bostad = repository.hamtaBostad(repository.hamtaForvaldBostadId())!!
        repository.sparaBostad(bostad.copy(namn = "Villan"), arPro = false)
        fyllMedData()
        val las = exportera(repository)
        val ny = skapaHusRepository(context, skapaTestHusDatabas(context))
        ny.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)
        assertEquals(listOf("Villan"), ny.allaBostader().map { it.namn })
        assertEquals(ny.allaBostader().single().id, ny.allaMatare().single().bostadId)
    }

    @Test
    fun overtagenBostadSlasInteIhopMedSenareBostadMedDetGamlaNamnet() = runTest {
        // Filen: "Villan" först och en bostad som heter som den nya telefonens tomma standardbostad.
        val standardnamn = context.getString(se.tmconnect.husloggen.R.string.bostad_forvald_namn)
        val forsta = repository.hamtaBostad(repository.hamtaForvaldBostadId())!!
        repository.sparaBostad(forsta.copy(namn = "Villan"), arPro = true)
        val andra = (repository.sparaBostad(Bostad(namn = standardnamn), arPro = true) as SparaBostadResult.Sparad).id
        repository.sparaMatare(Matare(bostadId = forsta.id, namn = "El", enhet = "kWh"))
        repository.sparaMatare(Matare(bostadId = andra, namn = "Vatten", enhet = "m3"))
        val las = exportera(repository)

        val ny = skapaHusRepository(context, skapaTestHusDatabas(context))
        ny.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)

        assertEquals(setOf("Villan", standardnamn), ny.allaBostader().map { it.namn }.toSet())
        val bostadPerMatare = ny.allaMatare().associate { it.namn to it.bostadId }
        val namnPerBostad = ny.allaBostader().associate { it.id to it.namn }
        assertEquals("Villan", namnPerBostad[bostadPerMatare["El"]])
        assertEquals(standardnamn, namnPerBostad[bostadPerMatare["Vatten"]])
    }

    @Test
    fun bostadMedDataTasInteOver() = runTest {
        fyllMedData()
        val annan = skapaHusRepository(context, skapaTestHusDatabas(context))
        val bostad = annan.hamtaBostad(annan.hamtaForvaldBostadId())!!
        annan.sparaBostad(bostad.copy(namn = "Stugan"), arPro = false)
        annan.sparaMatare(Matare(bostadId = bostad.id, namn = "Vatten", enhet = "m3"))
        val las = exportera(repository)
        annan.importeraSakerhetskopia(las.innehall, las.bildFiler, las.dokumentFiler)
        assertEquals(2, annan.antalBostader())
    }

    @Test
    fun raderadBostadTarBortBilderna() = runTest {
        val forsta = repository.hamtaForvaldBostadId()
        val andra = (repository.sparaBostad(Bostad(namn = "Stuga"), arPro = true) as SparaBostadResult.Sparad).id
        val bildLager = skapaHusBildLager(context)
        val fil = bildLager.sparaFranBytes(byteArrayOf(1, 2, 3), "bild.jpg")
        repository.sparaPost(
            Post(bostadId = andra, datum = LocalDate.of(2026, 5, 1), vad = "Altan", typ = PostTyp.OMBYGGNAD, bilder = fil),
            arPro = true,
        )
        assertTrue(bildLager.filFinns(fil))
        assertEquals(TaBortBostadResult.Borttagen, repository.taBortBostad(repository.hamtaBostad(andra)!!))
        assertFalse(bildLager.filFinns(fil))
        assertEquals(forsta, repository.allaBostader().single().id)
    }
}
