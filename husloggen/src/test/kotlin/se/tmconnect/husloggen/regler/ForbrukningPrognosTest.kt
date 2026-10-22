package se.tmconnect.husloggen.regler

import org.junit.Test
import se.tmconnect.husloggen.data.Avlasning
import java.time.LocalDate
import kotlin.test.assertNull
import kotlin.test.assertEquals

class ForbrukningPrognosTest {
    
    @Test
    fun testNormalFall() {
        val avlasningar = listOf(
            Avlasning(1, 1, LocalDate.of(2025, 12, 31), 10000000),
            Avlasning(2, 1, LocalDate.of(2026, 7, 1), 15000000)
        )
        
        val resultat = arsprognos(avlasningar, 2026, LocalDate.of(2026, 10, 22))
        assertEquals(10027473, resultat)
    }
    
    @Test
    fun testBaraEnAvlasning() {
        val avlasningar = listOf(
            Avlasning(1, 1, LocalDate.of(2026, 1, 1), 10000000)
        )
        
        val resultat = arsprognos(avlasningar, 2026, LocalDate.of(2026, 10, 22))
        assertNull(resultat)
    }
    
    @Test
    fun testForKortPeriod() {
        val avlasningar = listOf(
            Avlasning(1, 1, LocalDate.of(2026, 1, 1), 0),
            Avlasning(2, 1, LocalDate.of(2026, 1, 10), 100)
        )
        
        val resultat = arsprognos(avlasningar, 2026, LocalDate.of(2026, 10, 22))
        assertNull(resultat)
    }
    
    @Test
    fun testNegativDiff() {
        val avlasningar = listOf(
            Avlasning(1, 1, LocalDate.of(2025, 12, 31), 500),
            Avlasning(2, 1, LocalDate.of(2026, 3, 1), 400)
        )
        
        val resultat = arsprognos(avlasningar, 2026, LocalDate.of(2026, 10, 22))
        assertNull(resultat)
    }
    
    @Test
    fun testForstaAvlasningInomAr() {
        val avlasningar = listOf(
            Avlasning(1, 1, LocalDate.of(2026, 1, 1), 10000000),
            Avlasning(2, 1, LocalDate.of(2026, 10, 22), 15000000)
        )
        
        val resultat = arsprognos(avlasningar, 2026, LocalDate.of(2026, 10, 22))
        assertEquals(10027473, resultat)
    }
}
