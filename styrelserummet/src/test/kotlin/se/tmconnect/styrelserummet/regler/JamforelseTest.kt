package se.tmconnect.styrelserummet.regler

import org.junit.Test
import org.junit.Assert.*
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class JamforelseTest {

    @Test
    fun forandring() {
        // Testa tomma listor
        assertTrue(forandring(emptyList(), emptyList()).isEmpty())
        
        // Testa ny komponent
        val nu = listOf(
            Komponent("Tak", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val foreg = emptyList<Komponent>()
        val resultat = forandring(nu, foreg)
        assertEquals(1, resultat.size)
        assertEquals("Tak", resultat[0].namn)
        assertEquals(ForandringTyp.NY, resultat[0].typ)
        
        // Testa borttagen komponent
        val nu2 = emptyList<Komponent>()
        val foreg2 = listOf(
            Komponent("Hiss", Komponentgrupp.STOMME, 50000L, 2015, 30, 0L, "test", Sakerhet.HOG)
        )
        val resultat2 = forandring(nu2, foreg2)
        assertEquals(1, resultat2.size)
        assertEquals("Hiss", resultat2[0].namn)
        assertEquals(ForandringTyp.BORTTAGEN, resultat2[0].typ)
        
        // Testa ändrad komponent
        val nu3 = listOf(
            Komponent("Vägg", Komponentgrupp.STOMME, 150000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val foreg3 = listOf(
            Komponent("Vägg", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val resultat3 = forandring(nu3, foreg3)
        assertEquals(1, resultat3.size)
        assertEquals("Vägg", resultat3[0].namn)
        assertEquals(ForandringTyp.ANDRAD, resultat3[0].typ)
        
        // Testa oförändrad komponent
        val nu4 = listOf(
            Komponent("Golv", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val foreg4 = listOf(
            Komponent("Golv", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val resultat4 = forandring(nu4, foreg4)
        assertTrue(resultat4.isEmpty())
        
        // Testa blandade förändringar
        val nu5 = listOf(
            Komponent("Tak", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG),
            Komponent("Vägg", Komponentgrupp.STOMME, 150000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val foreg5 = listOf(
            Komponent("Hiss", Komponentgrupp.STOMME, 50000L, 2015, 30, 0L, "test", Sakerhet.HOG),
            Komponent("Vägg", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val resultat5 = forandring(nu5, foreg5)
        assertEquals(3, resultat5.size)
        assertEquals("Hiss", resultat5[0].namn)
        assertEquals(ForandringTyp.BORTTAGEN, resultat5[0].typ)
        assertEquals("Tak", resultat5[1].namn)
        assertEquals(ForandringTyp.NY, resultat5[1].typ)
        assertEquals("Vägg", resultat5[2].namn)
        assertEquals(ForandringTyp.ANDRAD, resultat5[2].typ)
        
        // Testa normalisering av namn (blanksteg och gemener)
        val nu6 = listOf(
            Komponent("  Tak  ", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val foreg6 = listOf(
            Komponent("tak", Komponentgrupp.STOMME, 100000L, 2020, 40, 0L, "test", Sakerhet.HOG)
        )
        val resultat6 = forandring(nu6, foreg6)
        assertTrue(resultat6.isEmpty())
    }
}
