package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StegvisHojningTest {
    
    @Test
    fun `test normal fall`() {
        assertEquals(listOf(4456667, 4713333, 4970000), stegvisHojning(4000000, 4970000, 3))
    }
    
    @Test
    fun `test enkelt fall`() {
        assertEquals(listOf(200), stegvisHojning(100, 200, 1))
    }
    
    @Test
    fun `test avrundning upp`() {
        assertEquals(listOf(101, 101), stegvisHojning(100, 101, 2))
    }
    
    @Test
    fun `test sankning`() {
        assertEquals(listOf(175, 150, 125, 100), stegvisHojning(200, 100, 4))
    }
    
    @Test
    fun `test sankning med avrundning`() {
        assertEquals(listOf(99, 99), stegvisHojning(100, 99, 2))
    }
    
    @Test
    fun `test felaktigt antal ar`() {
        assertNull(stegvisHojning(100, 200, 0))
        assertNull(stegvisHojning(100, 200, 11))
    }
    
    @Test
    fun `test negativa varden`() {
        assertNull(stegvisHojning(-1, 200, 2))
        assertNull(stegvisHojning(100, -1, 2))
    }
}
