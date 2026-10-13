package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FelBilderTest {
    @Test
    fun lasFelBilderDelarPaKommaOchIgnorerarTomt() {
        assertEquals(listOf("a.jpg", "b.jpg"), lasFelBilder("a.jpg, b.jpg,,"))
        assertEquals(emptyList<String>(), lasFelBilder(null))
        assertEquals(emptyList<String>(), lasFelBilder(""))
    }

    @Test
    fun skrivFelBilderGerNullForTomListaOchBegransarAntalet() {
        assertNull(skrivFelBilder(emptyList()))
        assertEquals("a.jpg,b.jpg", skrivFelBilder(listOf("a.jpg", "b.jpg", "a.jpg")))
        val manga = (1..8).map { "$it.jpg" }
        assertEquals(MAX_FEL_BILDER, lasFelBilder(skrivFelBilder(manga)).size)
    }

    @Test
    fun kanLaggaTillTillsGransenNas() {
        assertTrue(kanLaggaTillFelBild(null))
        assertTrue(kanLaggaTillFelBild("1.jpg,2.jpg,3.jpg,4.jpg"))
        assertFalse(kanLaggaTillFelBild("1.jpg,2.jpg,3.jpg,4.jpg,5.jpg"))
    }
}
