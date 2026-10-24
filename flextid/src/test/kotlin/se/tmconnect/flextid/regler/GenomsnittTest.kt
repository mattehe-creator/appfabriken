package se.tmconnect.flextid.regler

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.regler.DagTyp

class GenomsnittTest {
    
    @Test
    fun testGenomsnittArbetadMin() {
        val dag1 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 0), 30, DagTyp.ARBETE)
        val dag2 = Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 0), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        val dag3 = Dag(LocalDate.of(2026, 10, 27), LocalTime.of(8, 0), LocalTime.of(16, 45), 30, DagTyp.ARBETE)
        
        val result = genomsnittArbetadMin(listOf(dag1, dag2, dag3), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertEquals(475, result)
        
        val dag4 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 0), 30, DagTyp.ARBETE)
        val dag5 = Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 0), LocalTime.of(16, 31), 30, DagTyp.ARBETE)
        
        val result2 = genomsnittArbetadMin(listOf(dag4, dag5), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertEquals(466, result2)
        
        val dag6 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 30), 30, DagTyp.SEMESTER)
        val dag7 = Dag(LocalDate.of(2026, 10, 26), null, null, 0, DagTyp.ARBETE)
        val dagUtanfor = Dag(LocalDate.of(2026, 10, 28), LocalTime.of(8, 0), LocalTime.of(16, 0), 30, DagTyp.ARBETE)

        val result3 = genomsnittArbetadMin(listOf(dag6, dag7), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result3)

        val resultUtanfor = genomsnittArbetadMin(listOf(dagUtanfor), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertNull(resultUtanfor)

        val result4 = genomsnittArbetadMin(emptyList(), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result4)
    }
    
    @Test
    fun testGenomsnittStart() {
        val dag1 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(7, 50), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        val dag2 = Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 10), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        val dag3 = Dag(LocalDate.of(2026, 10, 27), LocalTime.of(8, 30), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        
        val result = genomsnittStart(listOf(dag1, dag2, dag3), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertEquals(LocalTime.of(8, 10), result)
        
        val dag4 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 30), 30, DagTyp.SEMESTER)
        val dag5 = Dag(LocalDate.of(2026, 10, 26), null, LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        
        val result2 = genomsnittStart(listOf(dag4, dag5), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result2)
        
        val result3 = genomsnittStart(emptyList(), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result3)
    }
}
