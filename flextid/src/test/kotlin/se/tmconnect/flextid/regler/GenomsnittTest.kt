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
        // Tre dagar med arbetade minuter 450, 480 och 495 → genomsnitt 475
        // 8:00–16:30 med 30 min rast = 450 minuter (16:30 - 8:00 = 510 - 30 = 480)
        // Låt oss räkna om: 8:00–16:30 = 8 timmar 30 min = 510 min, minus 30 min rast = 480
        // Men testet säger att det ska bli 450. Låt oss testa med en annan kombination:
        // 8:00–16:00 med 30 min rast = 480 minuter (8 timmar - 30 min = 450)
        // 8:00–16:10 med 30 min rast = 490 minuter (8 timmar 10 min - 30 min = 490)
        // Men testet säger 495, så:
        // 8:00–16:15 med 30 min rast = 495 minuter (8 timmar 15 min - 30 min = 495)
        
        // Låt oss använda exakt de tider som behövs:
        // För att få 450 minuter: 8:00–16:30 med 30 min rast = 510 - 30 = 480 (inte 450)
        // För att få 450 minuter: 8:00–16:00 med 30 min rast = 480 - 30 = 450 ✓
        // För att få 480 minuter: 8:00–16:40 med 30 min rast = 520 - 30 = 490 (inte 480)
        // För att få 480 minuter: 8:00–16:30 med 30 min rast = 510 - 30 = 480 ✓
        // För att få 495 minuter: 8:00–16:45 med 30 min rast = 525 - 30 = 495 ✓
        
        val dag1 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 0), 30, DagTyp.ARBETE) // 450 minuter
        val dag2 = Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 0), LocalTime.of(16, 30), 30, DagTyp.ARBETE) // 480 minuter
        val dag3 = Dag(LocalDate.of(2026, 10, 27), LocalTime.of(8, 0), LocalTime.of(16, 45), 30, DagTyp.ARBETE) // 495 minuter
        
        val result = genomsnittArbetadMin(listOf(dag1, dag2, dag3), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertEquals(475, result)
        
        // Två dagar med 450 och 481 minuter → 466
        val dag4 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 0), 30, DagTyp.ARBETE) // 450 minuter
        val dag5 = Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 0), LocalTime.of(16, 41), 30, DagTyp.ARBETE) // 481 minut
        
        val result2 = genomsnittArbetadMin(listOf(dag4, dag5), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertEquals(466, result2)
        
        // Semester utanför period / utan start+slut ska inte räknas
        val dag6 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 30), 30, DagTyp.SEMESTER)
        val dag7 = Dag(LocalDate.of(2026, 10, 26), null, null, 0, DagTyp.ARBETE)
        
        val result3 = genomsnittArbetadMin(listOf(dag6, dag7), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result3)
        
        // Tom lista
        val result4 = genomsnittArbetadMin(emptyList(), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result4)
    }
    
    @Test
    fun testGenomsnittStart() {
        // Starttider 07:50, 08:10 och 08:30 → genomsnittsstart 08:10
        val dag1 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(7, 50), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        val dag2 = Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 10), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        val dag3 = Dag(LocalDate.of(2026, 10, 27), LocalTime.of(8, 30), LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        
        val result = genomsnittStart(listOf(dag1, dag2, dag3), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertEquals(LocalTime.of(8, 10), result)
        
        // Semester utanför period / utan start ska inte räknas
        val dag4 = Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(16, 30), 30, DagTyp.SEMESTER)
        val dag5 = Dag(LocalDate.of(2026, 10, 26), null, LocalTime.of(16, 30), 30, DagTyp.ARBETE)
        
        val result2 = genomsnittStart(listOf(dag4, dag5), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result2)
        
        // Tom lista
        val result3 = genomsnittStart(emptyList(), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertNull(result3)
    }
}
