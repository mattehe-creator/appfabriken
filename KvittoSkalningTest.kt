package se.tmconnect.garantivalvet.regler

import org.junit.Test
import org.junit.Assert.*

class KvittoSkalningTest {

    @Test
    fun testNedskalningStorBild() {
        // Indata: bredd 4000, höjd 3000. Förväntat: 2000 × 1500
        val resultat = beraknaNedskaladStorlek(4000, 3000)
        assertEquals(2000, resultat.first)
        assertEquals(1500, resultat.second)
    }

    @Test
    fun testBildMindreAnMax() {
        // Indata: bredd 800, höjd 600. Förväntat: 800 × 600 (ingen ändring)
        val resultat = beraknaNedskaladStorlek(800, 600)
        assertEquals(800, resultat.first)
        assertEquals(600, resultat.second)
    }

    @Test
    fun testKvadratiskBild() {
        // Indata: bredd 3000, höjd 3000. Förväntat: 2000 × 2000
        val resultat = beraknaNedskaladStorlek(3000, 3000)
        assertEquals(2000, resultat.first)
        assertEquals(2000, resultat.second)
    }

    @Test
    fun testNollBredd() {
        // Indata: bredd 0, höjd 100. Förväntat: 0 × 100
        val resultat = beraknaNedskaladStorlek(0, 100)
        assertEquals(0, resultat.first)
        assertEquals(100, resultat.second)
    }

    @Test
    fun testNegativBredd() {
        // Indata: bredd -100, höjd 100. Förväntat: -100 × 100
        val resultat = beraknaNedskaladStorlek(-100, 100)
        assertEquals(-100, resultat.first)
        assertEquals(100, resultat.second)
    }

    @Test
    fun testNegativHojd() {
        // Indata: bredd 100, höjd -100. Förväntat: 100 × -100
        val resultat = beraknaNedskaladStorlek(100, -100)
        assertEquals(100, resultat.first)
        assertEquals(-100, resultat.second)
    }

    @Test
    fun testBådaNegativa() {
        // Indata: bredd -100, höjd -100. Förväntat: -100 × -100
        val resultat = beraknaNedskaladStorlek(-100, -100)
        assertEquals(-100, resultat.first)
        assertEquals(-100, resultat.second)
    }

    @Test
    fun testAnpassadMaxLangstaSida() {
        // Testa med anpassad maxLangstaSida
        val resultat = beraknaNedskaladStorlek(4000, 3000, 1000)
        assertEquals(1000, resultat.first)
        assertEquals(750, resultat.second)
    }
}
