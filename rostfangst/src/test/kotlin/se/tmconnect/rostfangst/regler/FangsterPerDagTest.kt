package se.tmconnect.rostfangst.regler

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals

class FangsterPerDagTest {
    
    @Test
    fun testFangsterPerDagMedFangster() {
        val idag = LocalDate.of(2026, 10, 19)
        val zon = ZoneId.of("Europe/Stockholm")
        
        // En fångst 2026-10-19 10:00 och en 2026-10-18 08:00
        val tider = listOf(
            Instant.parse("2026-10-19T10:00:00Z"),
            Instant.parse("2026-10-18T08:00:00Z")
        )
        
        val resultat = fangsterPerDag(tider, idag, zon)
        
        // Förväntat: 2026-10-19 → 1, 2026-10-18 → 1, övriga dagar i fönstret → 0
        assertEquals(30, resultat.size)
        
        // Kontrollera att rätt dagar har rätt antal
        assertEquals(LocalDate.of(2026, 10, 19) to 1, resultat[29]) // Sista dagen
        assertEquals(LocalDate.of(2026, 10, 18) to 1, resultat[28]) // Näst sista dagen
        for (i in 0..27) {
            assertEquals(LocalDate.of(2026, 10, 19).minusDays(29 - i) to 0, resultat[i])
        }
    }
    
    @Test
    fun testFangsterPerDagTomLista() {
        val idag = LocalDate.of(2026, 10, 19)
        val zon = ZoneId.of("Europe/Stockholm")
        
        val tider = emptyList<Instant>()
        
        val resultat = fangsterPerDag(tider, idag, zon)
        
        // Förväntat: 30 par med 0
        assertEquals(30, resultat.size)
        for (i in 0..29) {
            assertEquals(LocalDate.of(2026, 10, 19).minusDays(29 - i) to 0, resultat[i])
        }
    }
}
