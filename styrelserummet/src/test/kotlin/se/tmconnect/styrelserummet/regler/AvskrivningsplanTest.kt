package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import se.tmconnect.styrelserummet.modell.Komponent
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AvskrivningsplanTest {
    
    @Test
    fun `test planForKomponent with normal case`() {
        val k = Komponent(
            anskaffningsvarde = 300000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 10,
            restvarde = 0L
        )
        
        val result = planForKomponent(k, 2028, 3)
        
        assertEquals(3, result.size)
        assertEquals(PlanAr(2028, 30000L, 60000L), result[0])
        assertEquals(PlanAr(2029, 30000L, 0L), result[1])
        assertEquals(PlanAr(2030, 0L, 0L), result[2])
    }
    
    @Test
    fun `test planForKomponent with years before usage`() {
        val k = Komponent(
            anskaffningsvarde = 300000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 10,
            restvarde = 0L
        )
        
        val result = planForKomponent(k, 2018, 2)
        
        assertEquals(2, result.size)
        assertEquals(PlanAr(2018, 0L, 300000L), result[0])
        assertEquals(PlanAr(2019, 0L, 300000L), result[1])
    }
    
    @Test
    fun `test planForKomponent with zero years`() {
        val k = Komponent(
            anskaffningsvarde = 300000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 10,
            restvarde = 0L
        )
        
        val result = planForKomponent(k, 2020, 0)
        
        assertEquals(0, result.size)
    }
    
    @Test
    fun `test planForKomponent with too many years`() {
        val k = Komponent(
            anskaffningsvarde = 300000L,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 10,
            restvarde = 0L
        )
        
        assertFailsWith<IllegalArgumentException> {
            planForKomponent(k, 2020, 101)
        }
    }
}
