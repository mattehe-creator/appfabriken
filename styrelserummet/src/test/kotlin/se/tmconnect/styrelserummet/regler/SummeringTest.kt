package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class SummeringTest {

    @Test
    fun summeraPerGruppStommeOchEl2025() {
        val komponenter = listOf(
            komponent(Komponentgrupp.STOMME, 1_200_000, 2010, 40),
            komponent(Komponentgrupp.STOMME, 100_000, 2020, 3),
            komponent(Komponentgrupp.EL, 500_000, 2015, 25),
        )
        val resultat = summeraPerGrupp(komponenter, 2025)
        assertEquals(2, resultat.size)
        assertEquals(Komponentgrupp.STOMME, resultat[0].grupp)
        assertEquals(1_300_000L, resultat[0].anskaffning)
        assertEquals(30_000L, resultat[0].avskrivningAret)
        assertEquals(720_000L, resultat[0].bokfort)
        assertEquals(Komponentgrupp.EL, resultat[1].grupp)
        assertEquals(500_000L, resultat[1].anskaffning)
        assertEquals(20_000L, resultat[1].avskrivningAret)
        assertEquals(280_000L, resultat[1].bokfort)
    }

    @Test
    fun summeraPerGruppTomLista() {
        assertTrue(summeraPerGrupp(emptyList(), 2025).isEmpty())
    }

    private fun komponent(grupp: Komponentgrupp, varde: Long, anskaffningsar: Int, period: Int) =
        Komponent(
            namn = "Test",
            grupp = grupp,
            anskaffningsvarde = varde,
            anskaffningsar = anskaffningsar,
            nyttjandeperiodAr = period,
            restvarde = 0,
            kalla = "test",
            sakerhet = Sakerhet.HOG,
        )
}
