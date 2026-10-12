package se.tmconnect.garantivalvet.regler

import org.junit.Test
import org.junit.Assert.*

class GarantitidValTest {

    @Test
    fun testManaderFranValFastaVarden() {
        assertEquals(12, GarantitidVal.manaderFranVal(0, ""))
        assertEquals(24, GarantitidVal.manaderFranVal(1, ""))
        assertEquals(36, GarantitidVal.manaderFranVal(2, ""))
    }

    @Test
    fun testManaderFranValAnnat() {
        assertEquals(18, GarantitidVal.manaderFranVal(3, "18"))
        assertEquals(1, GarantitidVal.manaderFranVal(3, "1"))
        assertEquals(null, GarantitidVal.manaderFranVal(3, "abc"))
        assertEquals(null, GarantitidVal.manaderFranVal(3, ""))
    }

    @Test
    fun testManaderFranValIndexUtanfor() {
        assertEquals(null, GarantitidVal.manaderFranVal(-1, ""))
        assertEquals(null, GarantitidVal.manaderFranVal(4, ""))
    }

    @Test
    fun testForvaldManader() {
        assertEquals(24, GarantitidVal.FORVALD_MANADER)
    }

    @Test
    fun testChipManader() {
        assertEquals(listOf(12, 24, 36), GarantitidVal.CHIP_MANADER)
    }
}
