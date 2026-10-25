package se.tmconnect.rostfangst.regler

import org.junit.Test
import org.junit.Assert.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class FangsterPerDagTest {
    
    @Test
    fun testFangsterPerDag() {
        val zon = ZoneId.of("Europe/Stockholm")
        val idag = LocalDate.of(2026, 10, 19)
        
        // Testa med två fångster
        val tider = listOf(
            Instant.parse("2026-10-19T10:00:00Z"),
            Instant.parse("2026-10-18T08:00:00Z")
        )
        
        val resultat = fangsterPerDag(tider, idag, zon)
        
        // Kontrollera att vi får 30 par
        assertEquals(30, resultat.size)
        
        // Kontrollera att dagarna är korrekt
        val forventa = (0L..29L).map { idag.minusDays(it) }
        val faktiskaDagar = resultat.map { it.first }
        assertEquals(forventa.reversed(), faktiskaDagar)
        
        // Kontrollera antal fångster
        assertEquals(1, resultat[0].second) // 2026-10-19
        assertEquals(1, resultat[1].second) // 2026-10-18
        for (i in 2..29) {
            assertEquals(0, resultat[i].second)
        }
    }
    
    @Test
    fun testFangsterPerDagTomLista() {
        val zon = ZoneId.of("Europe/Stockholm")
        val idag = LocalDate.of(2026, 10, 19)
        
        val resultat = fangsterPerDag(emptyList(), idag, zon)
        
        // Kontrollera att vi får 30 par med 0
        assertEquals(30, resultat.size)
        for (i in 0..29) {
            assertEquals(0, resultat[i].second)
        }
    }
}
