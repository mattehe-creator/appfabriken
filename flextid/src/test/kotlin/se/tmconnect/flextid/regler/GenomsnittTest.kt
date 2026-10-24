package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.DagTyp
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GenomsnittTest {
    
    @Test
    fun testGenomsnittArbetadMin() {
        // Testa med tre arbetsdagar
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(12, 30), 30, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0), LocalTime.of(13, 0), 45, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 27), LocalTime.of(8, 30), LocalTime.of(12, 0), 30, DagTyp.ARBETE)
        )
        
        val resultat = genomsnittArbetadMin(dagar, LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        // 450 + 480 + 495 = 1425
        // (1425 * 2 + 3) / (2 * 3) = (2850 + 3) / 6 = 2853 / 6 = 475.5 → avrundat uppåt = 476
        assertEquals(476, resultat)
    }
    
    @Test
    fun testGenomsnittArbetadMinMedTvåDagar() {
        // Testa med två arbetsdagar
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(12, 30), 30, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 26), LocalTime.of(9, 0), LocalTime.of(13, 0), 45, DagTyp.ARBETE)
        )
        
        val resultat = genomsnittArbetadMin(dagar, LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        // 450 + 480 = 930
        // (930 * 2 + 2) / (2 * 2) = (1860 + 2) / 4 = 1862 / 4 = 465.5 → avrundat uppåt = 466
        assertEquals(466, resultat)
    }
    
    @Test
    fun testGenomsnittArbetadMinIngenMatch() {
        // Testa med arbetsdagar utanför perioden
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 24), LocalTime.of(8, 0), LocalTime.of(12, 30), 30, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 28), LocalTime.of(9, 0), LocalTime.of(13, 0), 45, DagTyp.ARBETE)
        )
        
        val resultat = genomsnittArbetadMin(dagar, LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertNull(resultat)
    }
    
    @Test
    fun testGenomsnittArbetadMinMedSemester() {
        // Testa med semesterdag som inte ska räknas
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 25), LocalTime.of(8, 0), LocalTime.of(12, 30), 30, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 26), null, null, 0, DagTyp.SEMESTER)
        )
        
        val resultat = genomsnittArbetadMin(dagar, LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 26))
        assertEquals(475, resultat) // Endast första dagen
    }
    
    @Test
    fun testGenomsnittArbetadMinTomLista() {
        val resultat = genomsnittArbetadMin(emptyList(), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertNull(resultat)
    }
    
    @Test
    fun testGenomsnittStart() {
        // Testa med tre arbetsdagar
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 25), LocalTime.of(7, 50), null, 0, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 26), LocalTime.of(8, 10), null, 0, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 27), LocalTime.of(8, 30), null, 0, DagTyp.ARBETE)
        )
        
        val resultat = genomsnittStart(dagar, LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        // 7:50 + 8:10 + 8:30 = 470 + 490 + 510 = 1470 minuter efter midnatt
        // (1470 * 2 + 3) / (2 * 3) = (2940 + 3) / 6 = 2943 / 6 = 490.5 → avrundat uppåt = 491
        // 491 minuter = 8 timmar och 11 minuter
        assertEquals(LocalTime.of(8, 11), resultat)
    }
    
    @Test
    fun testGenomsnittStartIngenMatch() {
        // Testa med arbetsdagar utanför perioden
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 24), LocalTime.of(7, 50), null, 0, DagTyp.ARBETE),
            Dag(LocalDate.of(2026, 10, 28), LocalTime.of(8, 30), null, 0, DagTyp.ARBETE)
        )
        
        val resultat = genomsnittStart(dagar, LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertNull(resultat)
    }
    
    @Test
    fun testGenomsnittStartTomLista() {
        val resultat = genomsnittStart(emptyList(), LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 27))
        assertNull(resultat)
    }
}
