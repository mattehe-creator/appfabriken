package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class NastaDatumTest {
    
    @Test
    fun `nastaDatum med null senastGjord returnerar startDatum`() {
        val result = nastaDatum(null, LocalDate.of(2026, 11, 1), 12)
        assertEquals(LocalDate.of(2026, 11, 1), result)
    }
    
    @Test
    fun `nastaDatum med senastGjord och intervall`() {
        val result = nastaDatum(LocalDate.of(2026, 3, 15), LocalDate.of(2025, 1, 1), 6)
        assertEquals(LocalDate.of(2026, 9, 15), result)
    }
    
    @Test
    fun `nastaDatum med månadsslut`() {
        val result = nastaDatum(LocalDate.of(2026, 1, 31), LocalDate.of(2025, 1, 1), 1)
        assertEquals(LocalDate.of(2026, 2, 28), result)
    }
    
    @Test
    fun `nastaDatum med schackår`() {
        val result = nastaDatum(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 1, 1), 12)
        assertEquals(LocalDate.of(2025, 2, 28), result)
    }
}
