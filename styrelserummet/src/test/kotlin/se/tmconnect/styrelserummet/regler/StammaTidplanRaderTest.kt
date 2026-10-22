package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StammaTidplanRaderTest {

    @Test
    fun tidplanRaderSjuRaderOchSenasteKallelsedag() {
        val plan = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 5, 15),
            kallelsefristVeckor = 4..6,
        )
        val rader = tidplanRader(plan, kallelseVeckor = 4)

        assertEquals(7, rader.size)
        assertEquals("2026-04-17 (fredag)", rader[3].datum)
        assertEquals("", rader[0].anmarkning)
        assertEquals("", rader[1].anmarkning)
        assertEquals(
            "Kallelsen ska utfärdas senast 4 veckor före stämman",
            rader[3].anmarkning,
        )
    }

    @Test
    fun tidplanRaderEfterLagfristGerAnmarkningarPaRadEttOchTva() {
        val plan = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 8, 1),
            kallelsefristVeckor = 2..4,
        )
        val rader = tidplanRader(plan, kallelseVeckor = 2)

        assertEquals("Stämman ligger efter lagfrist", rader[0].anmarkning)
        assertEquals("Efter lagens senaste datum", rader[1].anmarkning)
    }
}
