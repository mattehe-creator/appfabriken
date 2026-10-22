package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AndelstalTest {
    
    @Test
    fun `test fordelaEfterAndelstal med exempel 1`() {
        val result = fordelaEfterAndelstal(1000, listOf(1, 1, 1))
        assertEquals(listOf(334, 333, 333), result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med exempel 2`() {
        val result = fordelaEfterAndelstal(100, listOf(50, 30, 20))
        assertEquals(listOf(50, 30, 20), result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med exempel 3`() {
        val result = fordelaEfterAndelstal(10, listOf(1, 2))
        assertEquals(listOf(3, 7), result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med noll total`() {
        val result = fordelaEfterAndelstal(0, listOf(5, 5))
        assertEquals(listOf(0, 0), result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med tom lista`() {
        val result = fordelaEfterAndelstal(100, emptyList())
        assertNull(result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med negativt totalKronor`() {
        val result = fordelaEfterAndelstal(-100, listOf(1, 2))
        assertNull(result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med negativt andelstal`() {
        val result = fordelaEfterAndelstal(100, listOf(1, -1))
        assertNull(result)
    }
    
    @Test
    fun `test fordelaEfterAndelstal med summa noll`() {
        val result = fordelaEfterAndelstal(100, listOf(0, 0))
        assertNull(result)
    }
}
