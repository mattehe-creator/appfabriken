package se.tmconnect.styrelserummet.regler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FondIndexeringTest {
    
    @Test
    fun testIndexeratBelopp() {
        assertEquals(104040, indexeratBelopp(100000, 2028, 2026, 20))
        assertEquals(649460, indexeratBelopp(600000, 2030, 2026, 20))
        assertEquals(100000, indexeratBelopp(100000, 2026, 2026, 20))
        assertEquals(128010, indexeratBelopp(100000, 2036, 2026, 25))
        assertEquals(100000, indexeratBelopp(100000, 2025, 2026, 20))
    }
    
    @Test
    fun testIndexeratBeloppMedFelaktigOkning() {
        assertFailsWith<IllegalArgumentException> {
            indexeratBelopp(100000, 2026, 2026, 201)
        }
    }
    
    @Test
    fun testIndexeradeAtgarder() {
        val atgarder = listOf(
            PlaneradAtgard(2026, 100000, "Atgard 1"),
            PlaneradAtgard(2027, 200000, "Atgard 2"),
            PlaneradAtgard(2028, 300000, "Atgard 3")
        )
        
        val indexerade = indexeradeAtgarder(atgarder, 2026, 20)
        
        assertEquals(100000, indexerade[0].belopp)
        assertEquals(200000, indexerade[1].belopp)
        assertEquals(300000, indexerade[2].belopp)
    }
    
    @Test
    fun testIndexeradeAtgarderMedPrisokning() {
        val atgarder = listOf(
            PlaneradAtgard(2026, 100000, "Atgard 1"),
            PlaneradAtgard(2027, 100000, "Atgard 2"),
            PlaneradAtgard(2028, 100000, "Atgard 3")
        )
        
        val indexerade = indexeradeAtgarder(atgarder, 2026, 20)
        
        assertEquals(100000, indexerade[0].belopp) // 2026
        assertEquals(120000, indexerade[1].belopp) // 2027
        assertEquals(144000, indexerade[2].belopp) // 2028
    }
}
