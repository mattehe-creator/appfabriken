package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningsplanPerGruppTest {
    private fun k(namn: String, grupp: Komponentgrupp, v: Long, ar: Int, n: Int) =
        Komponent(namn, grupp, v, ar, n, 0, "test", Sakerhet.MEDEL)

    @Test
    fun perGrupp() {
        val lista = listOf(
            k("Tak", Komponentgrupp.TAK, 1_200_000, 2010, 40),
            k("Fönster", Komponentgrupp.FONSTER_DORRAR, 300_000, 2020, 10),
            k("Takavvattning", Komponentgrupp.TAK, 100_000, 2015, 20),
        )
        assertEquals(
            listOf(
                GruppPlan(Komponentgrupp.TAK, listOf(35_000L, 35_000L)),
                GruppPlan(Komponentgrupp.FONSTER_DORRAR, listOf(30_000L, 30_000L)),
            ),
            avskrivningsplanPerGrupp(lista, 2026, 2),
        )
        assertEquals(emptyList<GruppPlan>(), avskrivningsplanPerGrupp(emptyList(), 2026, 2))
        assertThrows(IllegalArgumentException::class.java) { avskrivningsplanPerGrupp(lista, 2026, 101) }
    }
}
