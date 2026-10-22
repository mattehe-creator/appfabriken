package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class StammaDataLasTest {
    
    @Test
    fun `test giltig data`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2026;59200;kr;SCB prisbasbelopp 2026",
            "referens_overlatelse;0;25;promille_av_pbb;STAMMA.md Osäkert"
        )
        
        val resultat = lasStammaData(rader)
        
        assertEquals(2, resultat.size)
        assertEquals(StammaRad("prisbasbelopp", 2026, 59200, "kr", "SCB prisbasbelopp 2026"), resultat["prisbasbelopp_2026"])
        assertEquals(StammaRad("referens_overlatelse", 0, 25, "promille_av_pbb", "STAMMA.md Osäkert"), resultat["referens_overlatelse_0"])
    }
    
    @Test
    fun `test tom rad ignoreras`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "",
            "prisbasbelopp;2026;59200;kr;SCB prisbasbelopp 2026"
        )
        
        val resultat = lasStammaData(rader)
        assertEquals(1, resultat.size)
        assertEquals(StammaRad("prisbasbelopp", 2026, 59200, "kr", "SCB prisbasbelopp 2026"), resultat["prisbasbelopp_2026"])
    }
    
    @Test
    fun `test ignorera rubrikrad`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2026;59200;kr;SCB prisbasbelopp 2026"
        )
        
        val resultat = lasStammaData(rader)
        assertEquals(1, resultat.size)
        assertEquals(StammaRad("prisbasbelopp", 2026, 59200, "kr", "SCB prisbasbelopp 2026"), resultat["prisbasbelopp_2026"])
    }
    
    @Test
    fun `test tom kalla kastar exception`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2026;59200;kr;"
        )
        
        assertThrows<IllegalArgumentException> {
            lasStammaData(rader)
        }
    }
    
    @Test
    fun `test negativt värde kastar exception`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2026;-1;kr;SCB prisbasbelopp 2026"
        )
        
        assertThrows<IllegalArgumentException> {
            lasStammaData(rader)
        }
    }
    
    @Test
    fun `test ogiltigt år kastar exception`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;10000;59200;kr;SCB prisbasbelopp 2026"
        )
        
        assertThrows<IllegalArgumentException> {
            lasStammaData(rader)
        }
    }
    
    @Test
    fun `test ogiltigt tal kastar exception`() {
        val rader = listOf(
            "typ;ar;varde;enhet;kalla",
            "prisbasbelopp;2026;abc;kr;SCB prisbasbelopp 2026"
        )
        
        assertThrows<IllegalArgumentException> {
            lasStammaData(rader)
        }
    }
}
