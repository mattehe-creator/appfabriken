package se.tmconnect.husloggen.paminnelse

import org.junit.Test
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.karna.paminnelse.PaminnelseFrist
import java.time.LocalDate
import kotlin.test.assertEquals

class PaminnelseFristerFranUppgifterTest {

    @Test
    fun testTomListaGerTomLista() {
        val uppgifter = emptyList<Uppgift>()
        val idag = LocalDate.of(2026, 10, 26)
        val resultat = paminnelseFristerFranUppgifter(uppgifter, idag)
        assertEquals(0, resultat.size)
    }

    @Test
    fun testUppgiftMedNullSenastGjord() {
        val uppgifter = listOf(
            Uppgift(
                id = 7,
                namn = "Testuppgift",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 3, 1),
                senastGjord = null,
                paminnelse = true
            )
        )
        val idag = LocalDate.of(2026, 10, 26)
        val resultat = paminnelseFristerFranUppgifter(uppgifter, idag)
        assertEquals(1, resultat.size)
        assertEquals("7:2026-03-01", resultat[0].nyckel)
        assertEquals(LocalDate.of(2026, 3, 1), resultat[0].slutdatum)
    }

    @Test
    fun testUppgiftMedSenastGjord() {
        val uppgifter = listOf(
            Uppgift(
                id = 8,
                namn = "Testuppgift",
                intervallManader = 1,
                startDatum = LocalDate.of(2025, 1, 1),
                senastGjord = LocalDate.of(2026, 1, 31),
                paminnelse = true
            )
        )
        val idag = LocalDate.of(2026, 10, 26)
        val resultat = paminnelseFristerFranUppgifter(uppgifter, idag)
        assertEquals(1, resultat.size)
        assertEquals("8:2026-02-28", resultat[0].nyckel)
        assertEquals(LocalDate.of(2026, 2, 28), resultat[0].slutdatum)
    }

    @Test
    fun testUppgiftUtanPaminnelse() {
        val uppgifter = listOf(
            Uppgift(
                id = 9,
                namn = "Testuppgift",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 3, 1),
                senastGjord = null,
                paminnelse = false
            )
        )
        val idag = LocalDate.of(2026, 10, 26)
        val resultat = paminnelseFristerFranUppgifter(uppgifter, idag)
        assertEquals(0, resultat.size)
    }

    @Test
    fun testListaMedFleraUppgifter() {
        val uppgifter = listOf(
            Uppgift(
                id = 7,
                namn = "Testuppgift1",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 3, 1),
                senastGjord = null,
                paminnelse = true
            ),
            Uppgift(
                id = 9,
                namn = "Testuppgift2",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 3, 1),
                senastGjord = null,
                paminnelse = false
            )
        )
        val idag = LocalDate.of(2026, 10, 26)
        val resultat = paminnelseFristerFranUppgifter(uppgifter, idag)
        assertEquals(1, resultat.size)
        assertEquals("7:2026-03-01", resultat[0].nyckel)
        assertEquals(LocalDate.of(2026, 3, 1), resultat[0].slutdatum)
    }
}
