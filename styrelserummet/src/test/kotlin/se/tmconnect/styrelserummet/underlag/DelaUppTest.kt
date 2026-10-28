package se.tmconnect.styrelserummet.underlag

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import java.lang.IllegalArgumentException

class DelaUppTest {

    @Test
    fun testNormalFall() {
        val sidor = listOf("a", "b", "c")
        val resultat = delaUpp(sidor, 10)
        assertEquals(1, resultat.size)
        assertEquals(listOf(1, 2, 3), resultat[0].sidnummer)
        assertEquals("a\n\nb\n\nc", resultat[0].text)
    }

    @Test
    fun testVarjeSidaSeparat() {
        val sidor = listOf("aa", "bb", "cc")
        val resultat = delaUpp(sidor, 4)
        assertEquals(3, resultat.size)
        assertEquals(listOf(1), resultat[0].sidnummer)
        assertEquals("aa", resultat[0].text)
        assertEquals(listOf(2), resultat[1].sidnummer)
        assertEquals("bb", resultat[1].text)
        assertEquals(listOf(3), resultat[2].sidnummer)
        assertEquals("cc", resultat[2].text)
    }

    @Test
    fun testSidaPrecisInomGrans() {
        val sidor = listOf("aa", "bb")
        val resultat = delaUpp(sidor, 6)
        assertEquals(1, resultat.size)
        assertEquals(listOf(1, 2), resultat[0].sidnummer)
        assertEquals("aa\n\nbb", resultat[0].text)
    }

    @Test
    fun testForLangSida() {
        val sidor = listOf("a", "xxxxxx", "b")
        val resultat = delaUpp(sidor, 5)
        assertEquals(3, resultat.size)
        assertEquals(listOf(1), resultat[0].sidnummer)
        assertEquals("a", resultat[0].text)
        assertEquals(listOf(2), resultat[1].sidnummer)
        assertEquals("xxxxxx", resultat[1].text)
        assertEquals(listOf(3), resultat[2].sidnummer)
        assertEquals("b", resultat[2].text)
    }

    @Test
    fun testTomLista() {
        val resultat = delaUpp(emptyList(), 5)
        assertEquals(0, resultat.size)
    }

    @Test
    fun testMaxTeckenNoll() {
        assertThrows(IllegalArgumentException::class.java) {
            delaUpp(listOf("a"), 0)
        }
    }

    @Test
    fun testMaxTeckenNegativt() {
        assertThrows(IllegalArgumentException::class.java) {
            delaUpp(listOf("a"), -1)
        }
    }
}
