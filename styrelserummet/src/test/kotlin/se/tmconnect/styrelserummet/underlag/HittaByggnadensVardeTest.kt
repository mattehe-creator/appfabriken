package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test

class HittaByggnadensVardeTest {
    
    @Test
    fun parseBeloppAvgransare() {
        // Testa olika typer av tusentalsavgränsare
        assertEquals(12345L, parseBelopp("12\u00A0345", iTusentals = false))
        assertEquals(12345678L, parseBelopp("12.345.678", iTusentals = false))
        assertEquals(1234500L, parseBelopp("1.234,5", iTusentals = true))
        assertEquals(45000000L, parseBelopp("45 000", iTusentals = true))
        
        // Befintliga testfall ska fortfarande fungera
        assertEquals(12345L, parseBelopp("12 345", iTusentals = false))
        assertEquals(12345000L, parseBelopp("12 345", iTusentals = true))
        assertEquals(12L, parseBelopp("12,3", iTusentals = false))
        assertEquals(12L, parseBelopp("12,345", iTusentals = false))
        assertEquals(12345L, parseBelopp("12,345", iTusentals = true))
    }
}
