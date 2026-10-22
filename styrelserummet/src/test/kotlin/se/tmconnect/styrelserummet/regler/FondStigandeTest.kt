package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class FondStigandeTest {

    @Test
    fun `fondUtvecklingStigande enligt uppgiftens exempel`() {
        val atgarder = listOf(PlaneradAtgard(2028, 900_000L, "Renovering"))
        val resultat = fondUtvecklingStigande(
            atgarder,
            franAr = 2026,
            antalAr = 3,
            forstaAvsattning = 300_000L,
            okningTiondelsProcent = 20,
            ingaendeFond = 0L,
        )
        assertEquals(
            listOf(
                FondAr(2026, 300_000L, 0L, 300_000L),
                FondAr(2027, 306_000L, 0L, 606_000L),
                FondAr(2028, 312_120L, 900_000L, 18_120L),
            ),
            resultat,
        )
    }

    @Test
    fun `okning noll ger samma resultat som fondUtveckling`() {
        val atgarder = listOf(PlaneradAtgard(2028, 900_000L, "Renovering"))
        val franAr = 2026
        val antalAr = 3
        val forstaAvsattning = 300_000L
        val ingaendeFond = 0L
        val stigande = fondUtvecklingStigande(
            atgarder,
            franAr,
            antalAr,
            forstaAvsattning,
            okningTiondelsProcent = 0,
            ingaendeFond,
        )
        val jamn = fondUtveckling(atgarder, franAr, antalAr, forstaAvsattning, ingaendeFond)
        assertEquals(jamn, stigande)
    }
}
