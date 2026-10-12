package se.tmconnect.garantivalvet.regler

import org.junit.Test
import org.junit.Assert.*

class GarantitidValTest {
    
    @Test
    fun testForvaldManader() {
        assertEquals(24, GarantitidVal.FORVALD_MANADER)
    }
    
    @Test
    fun testChipManader() {
        assertEquals(listOf(12, 24, 36), GarantitidVal.CHIP_MANADER)
    }
    
    @Test
    fun testManaderFranValMedFastaVarden() {
        assertEquals(12, GarantitidVal.manaderFranVal(0, ""))
        assertEquals(24, GarantitidVal.manaderFranVal(1, ""))
        assertEquals(36, GarantitidVal.manaderFranVal(2, ""))
    }
    
    @Test
    fun testManaderFranValMedAnnat() {
        assertEquals(18, GarantitidVal.manaderFranVal(3, "18"))
        assertEquals(1, GarantitidVal.manaderFranVal(3, "1"))
        assertNull(GarantitidVal.manaderFranVal(3, "0"))
        assertNull(GarantitidVal.manaderFranVal(3, "-5"))
        assertNull(GarantitidVal.manaderFranVal(3, "abc"))
        assertNull(GarantitidVal.manaderFranVal(3, ""))
    }
    
    @Test
    fun testManaderFranValMedOgiltigtIndex() {
        assertNull(GarantitidVal.manaderFranVal(-1, ""))
        assertNull(GarantitidVal.manaderFranVal(4, ""))
    }
}
