package se.tmconnect.garantivalvet.regler

import org.junit.Test
import kotlin.test.assertEquals

class KvittoSkalningTest {
    
    @Test
    fun testNedskalningStorBild() {
        // Indata: bredd 4000, höjd 3000. Förväntat: 2000 × 1500 (längsta sidan 2000).
        val resultat = beraknaNedskaladStorlek(4000, 3000)
        assertEquals(2000, resultat.first)
        assertEquals(1500, resultat.second)
    }
    
    @Test
    fun testIngenNedskalningLitenBild() {
        // Indata: bredd 800, höjd 600. Förväntat: 800 × 600 (ingen ändring).
        val resultat = beraknaNedskaladStorlek(800, 600)
        assertEquals(800, resultat.first)
        assertEquals(600, resultat.second)
    }
    
    @Test
    fun testKvadratBild() {
        // Indata: bredd 3000, höjd 3000. Förväntat: 2000 × 2000.
        val resultat = beraknaNedskaladStorlek(3000, 3000)
        assertEquals(2000, resultat.first)
        assertEquals(2000, resultat.second)
    }
    
    @Test
    fun testOgiltigaMatt() {
        // Indata: bredd 0, höjd 100. Förväntat: 0 × 100.
        val resultat = beraknaNedskaladStorlek(0, 100)
        assertEquals(0, resultat.first)
        assertEquals(100, resultat.second)
    }
    
    @Test
    fun testMaxLangstaSidaAnpassad() {
        // Testa med anpassad maxLangstaSida
        val resultat = beraknaNedskaladStorlek(4000, 3000, 1000)
        assertEquals(1000, resultat.first)
        assertEquals(750, resultat.second)
    }
    
    @Test
    fun testNegativaMatt() {
        // Testa med negativa värden
        val resultat = beraknaNedskaladStorlek(-100, 200)
        assertEquals(-100, resultat.first)
        assertEquals(200, resultat.second)
    }
}
