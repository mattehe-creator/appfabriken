package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test

class HittaByggnadensVardeTest {
    
    @Test
    fun `hittaByggnadensVardeTkrMnkr`() {
        // Testa tkr
        val result1 = hittaByggnadensVarde("Byggnader och mark 12 345 tkr")
        assertEquals(Belopp(12345000, iTusentals = true), result1)
        
        // Testa mnkr
        val result2 = hittaByggnadensVarde("Byggnader 12,3 mnkr")
        assertEquals(Belopp(12300000, iTusentals = false), result2)
        
        // Testa att befintlig logik fortfarande fungerar
        val result3 = hittaByggnadensVarde("Byggnader och mark 45 000 tusen kronor")
        assertEquals(Belopp(45000000, iTusentals = true), result3)
        
        // Testa med mellanslag i tal
        val result4 = hittaByggnadensVarde("Byggnader 123 mnkr")
        assertEquals(Belopp(123000000, iTusentals = false), result4)
    }
}
