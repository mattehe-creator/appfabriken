package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.underlag.tolkaForslag

class ExempelInspelningTest {

    @Test
    fun inspeladJsonTolkasTill14Komponenter() {
        val json = inspeladTolkareJson()
        assertTrue(json.contains("Balkonger"))
        assertTrue(json.contains("LAG"))

        val resultat = tolkaForslag(json, "exempel-underhallsplan.pdf")
        assertEquals(14, resultat.komponenter.size)

        val balkong = resultat.komponenter.single { it.namn == "Balkonger" }
        assertEquals(Sakerhet.LAG, balkong.sakerhet)

        assertTrue(
            resultat.anmarkningar.any {
                it.contains("kostnad", ignoreCase = true) &&
                    it.contains("balkong", ignoreCase = true)
            },
        )

        assertEquals(
            json,
            inspeladTolkare().foreslaKomponenter("", emptyList(), "x").getOrThrow(),
        )
    }
}
