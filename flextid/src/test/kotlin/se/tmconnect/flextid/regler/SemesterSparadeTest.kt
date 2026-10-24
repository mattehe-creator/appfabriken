package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class SemesterSparadeTest {
    private val mask = ARBETSDAGAR_MANDAG_FREDAG
    private fun sem(s: String) = Dag(datum = LocalDate.parse(s), typ = DagTyp.SEMESTER)
    
    @Test
    fun testSparadeDagarNoll() {
        val dagar = listOf("2026-07-06", "2026-07-07", "2026-07-08", "2026-07-09", "2026-07-10")
            .map(::sem)
        val result = semesterSammanfattning(dagar, LocalDate.of(2026, 10, 21), mask, sparadeDagar = 0)
        assertEquals(20, result.kvar)
        assertEquals(0, result.sparade)
    }
    
    @Test
    fun testSparadeDagarSju() {
        val dagar = listOf("2026-07-06", "2026-07-07", "2026-07-08", "2026-07-09", "2026-07-10")
            .map(::sem)
        val result = semesterSammanfattning(dagar, LocalDate.of(2026, 10, 21), mask, sparadeDagar = 7)
        assertEquals(27, result.kvar)
        assertEquals(7, result.sparade)
    }
    
    @Test
    fun testSparadeDagarMinusTre() {
        val dagar = listOf("2026-07-06", "2026-07-07", "2026-07-08", "2026-07-09", "2026-07-10")
            .map(::sem)
        val result = semesterSammanfattning(dagar, LocalDate.of(2026, 10, 21), mask, sparadeDagar = -3)
        assertEquals(20, result.kvar)
        assertEquals(0, result.sparade)
    }
}
