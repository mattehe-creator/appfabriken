package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class FondIndexeringTest {
    
    @Test
    fun testIndexeratBelopp() {
        // (100 000, 2028, 2026, 20) → 104 040
        assertEquals(104040, indexeratBelopp(100000L, 2028, 2026, 20))
        
        // (600 000, 2030, 2026, 20) → 649 460
        assertEquals(649460, indexeratBelopp(600000L, 2030, 2026, 20))
        
        // (100 000, 2026, 2026, 20) → 100 000
        assertEquals(100000, indexeratBelopp(100000L, 2026, 2026, 20))
        
        // (100 000, 2036, 2026, 25) → 128 010
        assertEquals(128010, indexeratBelopp(100000L, 2036, 2026, 25))
        
        // (100 000, 2025, 2026, 20) → 100 000
        assertEquals(100000, indexeratBelopp(100000L, 2025, 2026, 20))
    }
    
    @Test
    fun testIndexeratBeloppInvalidOkning() {
        assertThrows<IllegalArgumentException> {
            indexeratBelopp(100000L, 2026, 2026, 201)
        }
        
        assertThrows<IllegalArgumentException> {
            indexeratBelopp(100000L, 2026, 2026, -1)
        }
    }
    
    @Test
    fun testIndexeradeAtgarder() {
        val atgarder = listOf(
            PlaneradAtgard(2026, 100000L, "Atgard 1"),
            PlaneradAtgard(2027, 200000L, "Atgard 2"),
            PlaneradAtgard(2028, 300000L, "Atgard 3")
        )
        
        val indexerade = indexeradeAtgarder(atgarder, 2026, 20)
        
        assertEquals(100000, indexerade[0].belopp) // 2026 är basåret
        assertEquals(120000, indexerade[1].belopp) // 200000 * 1.2
        assertEquals(144000, indexerade[2].belopp) // 300000 * 1.2 * 1.2
    }
}
