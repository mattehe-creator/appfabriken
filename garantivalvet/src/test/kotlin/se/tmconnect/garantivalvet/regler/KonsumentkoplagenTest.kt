package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class KonsumentkoplagenTest {

    @Test
    fun reklamationSlutTreArFranKop() {
        val kop = LocalDate.of(2024, 3, 15)
        assertEquals(LocalDate.of(2027, 3, 15), reklamationSlut(kop))
        assertEquals(LocalDate.of(2026, 3, 15), presumtionSlut(kop))
    }

    @Test
    fun reklamationSlutSkottarsdag() {
        assertEquals(LocalDate.of(2027, 2, 28), reklamationSlut(LocalDate.of(2024, 2, 29)))
    }

    @Test
    fun sistaDagenRaknasSomGiltig() {
        val kop = LocalDate.of(2024, 3, 15)
        val slut = reklamationSlut(kop)!!
        assertEquals(ReklamationStatus.GALLER, reklamationStatus(kop, slut))
        assertEquals(ReklamationStatus.UTGANGEN, reklamationStatus(kop, slut.plusDays(1)))
    }

    @Test
    fun reklamationStatusOverGangar() {
        val kop = LocalDate.of(2024, 3, 15)
        assertEquals(ReklamationStatus.FEL_ANTAS_FUNNITS, reklamationStatus(kop, LocalDate.of(2025, 6, 1)))
        assertEquals(ReklamationStatus.GALLER, reklamationStatus(kop, LocalDate.of(2026, 6, 1)))
        assertEquals(ReklamationStatus.UTGANGEN, reklamationStatus(kop, LocalDate.of(2027, 3, 16)))
    }

    @Test
    fun kopForeIkraftArEjTillamplig() {
        val fore = LocalDate.of(2022, 4, 30)
        assertNull(reklamationSlut(fore))
        assertNull(presumtionSlut(fore))
        assertEquals(ReklamationStatus.EJ_TILLAMPLIG, reklamationStatus(fore, LocalDate.of(2025, 1, 1)))
    }

    @Test
    fun kopFranIkraftArTillamplig() {
        val fran = LocalDate.of(2022, 5, 1)
        assertEquals(LocalDate.of(2025, 5, 1), reklamationSlut(fran))
        assertEquals(ReklamationStatus.FEL_ANTAS_FUNNITS, reklamationStatus(fran, LocalDate.of(2023, 1, 1)))
    }
}
