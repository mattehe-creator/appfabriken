package se.tmconnect.styrelserummet.regler

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.assertEquals as kotlinAssertEquals

class FondplanTest {
    
    @Test
    fun `test summaPerAr med exempeldata`() {
        val atgarder = listOf(
            PlaneradAtgard(2026, 100_000L, "Takvård"),
            PlaneradAtgard(2028, 600_000L, "Fönster"),
            PlaneradAtgard(2028, 300_000L, "Balkonger"),
            PlaneradAtgard(2030, 200_000L, "Ventilation"),
            PlaneradAtgard(2040, 1_000_000L, "Stammar")
        )
        
        // franAr 2026, antalAr 5 → [100 000, 0, 900 000, 0, 200 000]
        val resultat1 = summaPerAr(atgarder, 2026, 5)
        assertEquals(listOf(100_000L, 0L, 900_000L, 0L, 200_000L), resultat1)
        
        // franAr 2027, antalAr 2 → [0, 900 000]
        val resultat2 = summaPerAr(atgarder, 2027, 2)
        assertEquals(listOf(0L, 900_000L), resultat2)
        
        // tom lista, franAr 2026, antalAr 3 → [0, 0, 0]
        val resultat3 = summaPerAr(emptyList(), 2026, 3)
        assertEquals(listOf(0L, 0L, 0L), resultat3)
    }
    
    @Test
    fun `test summaPerAr med gränsvärden`() {
        // antalAr 0 → IllegalArgumentException
        assertThrows(IllegalArgumentException::class.java) {
            summaPerAr(emptyList(), 2026, 0)
        }
        
        // antalAr 101 → IllegalArgumentException
        assertThrows(IllegalArgumentException::class.java) {
            summaPerAr(emptyList(), 2026, 101)
        }
        
        // belopp −1 → IllegalArgumentException
        val atgarder = listOf(PlaneradAtgard(2026, -1L, "Test"))
        assertThrows(IllegalArgumentException::class.java) {
            summaPerAr(atgarder, 2026, 1)
        }
    }
}
