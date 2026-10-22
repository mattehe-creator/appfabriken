package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SaldoPrognosTest {
    @Test
    fun exempel() {
        assertEquals(239, saldoVidArssluten(120, 120, LocalDate.of(2026, 7, 2)))
        assertEquals(-275, saldoVidArssluten(0, -90, LocalDate.of(2026, 3, 31)))
        assertEquals(50, saldoVidArssluten(50, 0, LocalDate.of(2026, 12, 31)))
        assertNull(saldoVidArssluten(10, 3, LocalDate.of(2026, 1, 29)))
        assertEquals(25, saldoVidArssluten(10, 3, LocalDate.of(2024, 2, 29)))
    }
}
