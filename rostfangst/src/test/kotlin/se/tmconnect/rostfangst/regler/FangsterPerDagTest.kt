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
        
        val tider = listOf(
            Instant.parse("2026-10-19T10:00:00Z"),
            Instant.parse("2026-10-18T08:00:00Z")
        )
        
        val resultat = fangsterPerDag(tider, idag, zon)
        
        assertEquals(30, resultat.size)
        
        // Kontrollera att rätt dagar har rätt antal
        assertEquals(LocalDate.of(2026, 10, 19) to 1, resultat[0])
        assertEquals(LocalDate.of(2026, 10, 18) to 1, resultat[1])
        
        // Kontrollera att övriga dagar har 0
        for (i in 2..29) {
            assertEquals(0, resultat[i].second)
        }
    }
    
    @Test
    fun testFangsterPerDagTomLista() {
        val idag = LocalDate.of(2026, 10, 19)
        val zon = ZoneId.of("Europe/Stockholm")
        
        val resultat = fangsterPerDag(emptyList(), idag, zon)
        
        assertEquals(30, resultat.size)
        for (i in 0..29) {
            assertEquals(0, resultat[i].second)
        }
    }
}
