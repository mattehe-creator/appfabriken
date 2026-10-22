package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.fail

class StammaDataLasTest {

    @Test
    fun testGiltigData() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2020;47300;kr;SCB prisbasbelopp 2020",
            "prisbasbelopp;2021;47600;kr;SCB prisbasbelopp 2021"
        )
        
        val resultat = lasStammaData(rader)
        
        assertEquals(2, resultat.size)
        assertEquals(47300L, resultat["prisbasbelopp_2020"]!!.varde)
        assertEquals(47600L, resultat["prisbasbelopp_2021"]!!.varde)
    }

    @Test
    fun testIgnoreraTomRad() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "",
            "prisbasbelopp;2020;47300;kr;SCB prisbasbelopp 2020"
        )
        
        val resultat = lasStammaData(rader)
        
        assertEquals(1, resultat.size)
        assertEquals(47300L, resultat["prisbasbelopp_2020"]!!.varde)
    }

    @Test
    fun testIgnoreraRubrikRad() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2020;47300;kr;SCB prisbasbelopp 2020"
        )
        
        val resultat = lasStammaData(rader)
        
        assertEquals(1, resultat.size)
        assertEquals(47300L, resultat["prisbasbelopp_2020"]!!.varde)
    }

    @Test
    fun testTomKalla() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2020;47300;kr;"
        )
        
        try {
            lasStammaData(rader)
            fail("Förväntade IllegalArgumentException för tom källa")
        } catch (e: IllegalArgumentException) {
            assertEquals("Tom källa. Rad: prisbasbelopp;2020;47300;kr;", e.message)
        }
    }

    @Test
    fun testNegativtVarde() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2020;-1;kr;SCB prisbasbelopp 2020"
        )
        
        try {
            lasStammaData(rader)
            fail("Förväntade IllegalArgumentException för negativt värde")
        } catch (e: IllegalArgumentException) {
            assertEquals("Negativt värde: -1. Rad: prisbasbelopp;2020;-1;kr;SCB prisbasbelopp 2020", e.message)
        }
    }

    @Test
    fun testOgiltigtAr() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;abc;47300;kr;SCB prisbasbelopp 2020"
        )
        
        try {
            lasStammaData(rader)
            fail("Förväntade IllegalArgumentException för ogiltigt år")
        } catch (e: IllegalArgumentException) {
            assertEquals("Ogiltigt år: abc. Rad: prisbasbelopp;abc;47300;kr;SCB prisbasbelopp 2020", e.message)
        }
    }

    @Test
    fun testArUtanforIntervall() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;10000;47300;kr;SCB prisbasbelopp 2020"
        )
        
        try {
            lasStammaData(rader)
            fail("Förväntade IllegalArgumentException för år utanför intervall")
        } catch (e: IllegalArgumentException) {
            assertEquals("År utanför giltigt intervall: 10000. Rad: prisbasbelopp;10000;47300;kr;SCB prisbasbelopp 2020", e.message)
        }
    }

    @Test
    fun testFelAntalFalt() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2020;47300;kr"
        )
        
        try {
            lasStammaData(rader)
            fail("Förväntade IllegalArgumentException för fel antal fält")
        } catch (e: IllegalArgumentException) {
            assertEquals("Ogiltig rad: fel antal fält. Rad: prisbasbelopp;2020;47300;kr", e.message)
        }
    }

    @Test
    fun testGiltigtExempel() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2026;59200;kr;SCB prisbasbelopp 2026",
            "referens_overlatelse;0;25;promille_av_pbb;STAMMA.md Osäkert — typisk stadgarnivå 2,5 %, inte lagtak",
            "referens_pantsattning;0;10;promille_av_pbb;STAMMA.md Osäkert — typisk stadgarnivå 1 %, inte lagtak"
        )
        
        val resultat = lasStammaData(rader)
        
        assertEquals(3, resultat.size)
        assertEquals(59200L, resultat["prisbasbelopp_2026"]!!.varde)
        assertEquals(25L, resultat["referens_overlatelse_0"]!!.varde)
        assertEquals(10L, resultat["referens_pantsattning_0"]!!.varde)
    }
}
