package se.tmconnect.rostfangst.regler

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Test
import org.junit.Assert.assertEquals

class FangsterPerDagTest {

    @Test
    fun `tom lista`() {
        val resultat = fangsterPerDag(
            tider = emptyList(),
            idag = LocalDate.of(2026, 10, 19),
            zon = ZoneId.of("Europe/Stockholm")
        )
        assertEquals(30, resultat.size)
        resultat.forEach { (dag, antal) ->
            assertEquals(0, antal, "Dag $dag borde ha 0 fångster")
        }
    }

    @Test
    fun `en fångst idag och en igår`() {
        val idag = LocalDate.of(2026, 10, 19)
        val igår = idag.minusDays(1)
        val resultat = fangsterPerDag(
            tider = listOf(
                Instant.parse("2026-10-19T10:00:00Z"),
                Instant.parse("2026-10-18T08:00:00Z")
            ),
            idag = idag,
            zon = ZoneId.of("Europe/Stockholm")
        )
        assertEquals(30, resultat.size)
        assertEquals(idag, resultat[29].first)
        assertEquals(1, resultat[29].second)
        assertEquals(igår, resultat[28].first)
        assertEquals(1, resultat[28].second)
        for (i in 0..27) {
            assertEquals(0, resultat[i].second, "Dag ${resultat[i].first} borde ha 0 fångster")
        }
    }
}
