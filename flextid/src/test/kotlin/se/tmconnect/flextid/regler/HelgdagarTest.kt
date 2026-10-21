package se.tmconnect.flextid.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.assertThrows
import java.time.LocalDate

class HelgdagarTest {
    @Test
    fun testPaskdagen() {
        assertEquals(LocalDate.of(2008, 3, 23), paskdagen(2008))
        assertEquals(LocalDate.of(2019, 4, 21), paskdagen(2019))
        assertEquals(LocalDate.of(2024, 3, 31), paskdagen(2024))
        assertEquals(LocalDate.of(2025, 4, 20), paskdagen(2025))
        assertEquals(LocalDate.of(2026, 4, 5), paskdagen(2026))
        assertEquals(LocalDate.of(2027, 3, 28), paskdagen(2027))
        assertEquals(LocalDate.of(2038, 4, 25), paskdagen(2038))
    }

    @Test
    fun testPaskdagenIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            paskdagen(1899)
        }
        assertThrows(IllegalArgumentException::class.java) {
            paskdagen(2201)
        }
    }
}
