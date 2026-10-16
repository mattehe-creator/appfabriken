package se.tmconnect.husloggen.regler

import org.junit.Test
import org.junit.Assert.*

class BildTolkningTest {

    @Test
    fun testTolkaBilder_med_giltiga_indata() {
        // Testa normal fall
        val result = tolkaBilder("a.jpg,b.jpg")
        assertEquals(listOf("a.jpg", "b.jpg"), result)
        
        // Testa med mellanslag
        val result2 = tolkaBilder(" a.jpg , ,b.jpg")
        assertEquals(listOf("a.jpg", "b.jpg"), result2)
        
        // Testa med null
        val result3 = tolkaBilder(null)
        assertEquals(emptyList<String>(), result3)
        
        // Testa med tom sträng
        val result4 = tolkaBilder("")
        assertEquals(emptyList<String>(), result4)
    }

    @Test
    fun testFormateraBilder_med_giltiga_indata() {
        // Testa normal fall
        val result = formateraBilder(listOf("a.jpg", "b.jpg"))
        assertEquals("a.jpg,b.jpg", result)
        
        // Testa med tom lista
        val result2 = formateraBilder(emptyList())
        assertNull(result2)
        
        // Testa med en bild
        val result3 = formateraBilder(listOf("a.jpg"))
        assertEquals("a.jpg", result3)
    }
}
