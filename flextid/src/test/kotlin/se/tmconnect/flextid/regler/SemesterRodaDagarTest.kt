package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class SemesterRodaDagarTest {
    private val mask = ARBETSDAGAR_MANDAG_FREDAG
    private fun sem(s: String) = Dag(datum = LocalDate.parse(s), typ = DagTyp.SEMESTER)
    
    @Test
    fun testKristiHimmelfardsdag() {
        // Semester 2026-05-13, 2026-05-14 (Kristi himmelfärdsdag), 2026-05-15
        // med aftnarLediga = true (standard) så ska bara 2026-05-13 och 2026-05-15 räknas
        val dagar = listOf(
            sem("2026-05-13"),
            sem("2026-05-14"), // Kristi himmelfärdsdag - röd dag
            sem("2026-05-15")
        )
        
        val result = semesterSammanfattning(dagar, LocalDate.of(2026, 5, 20), mask)
        assertEquals(2, result.uttagna)
        assertEquals(23, result.kvar)
    }
    
    @Test
    fun testMidsommarafton() {
        // Semester 2026-06-19 (midsommarafton) - röd dag
        // med aftnarLediga = true så ska den inte räknas
        val dagar = listOf(
            sem("2026-06-19") // midsommarafton - röd dag
        )
        
        val result = raknaDagarAvTyp(dagar, DagTyp.SEMESTER, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30), mask)
        assertEquals(0, result)
        
        // med aftnarLediga = false så ska den räknas
        val resultWithAftnarLedigaFalse = raknaDagarAvTyp(dagar, DagTyp.SEMESTER, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30), mask, false)
        assertEquals(1, resultWithAftnarLedigaFalse)
    }
    
    @Test
    fun testTorsdag() {
        // Semester 2026-06-18 (torsdag) - inte en röd dag
        val dagar = listOf(
            sem("2026-06-18") // torsdag - inte röd dag
        )
        
        // med aftnarLediga = true
        val resultWithAftnarLedigaTrue = raknaDagarAvTyp(dagar, DagTyp.SEMESTER, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30), mask)
        assertEquals(1, resultWithAftnarLedigaTrue)
        
        // med aftnarLediga = false
        val resultWithAftnarLedigaFalse = raknaDagarAvTyp(dagar, DagTyp.SEMESTER, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30), mask, false)
        assertEquals(1, resultWithAftnarLedigaFalse)
    }
}
