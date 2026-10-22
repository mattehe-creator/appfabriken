package se.tmconnect.styrelserummet.regler

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import java.time.LocalDate

class HelgdagarPaskTest {

    @Test
    fun `paskdagen 2026`() {
        assertEquals(LocalDate.of(2026, 4, 5), paskdagen(2026))
    }

    @Test
    fun `paskdagen 2025`() {
        assertEquals(LocalDate.of(2025, 4, 20), paskdagen(2025))
    }

    @Test
    fun `paskdagen 2000`() {
        assertEquals(LocalDate.of(2000, 4, 23), paskdagen(2000))
    }

    @Test
    fun `paskdagen utanför giltigt intervall`() {
        assertThrows<IllegalArgumentException> { paskdagen(1899) }
        assertThrows<IllegalArgumentException> { paskdagen(2201) }
    }
}
