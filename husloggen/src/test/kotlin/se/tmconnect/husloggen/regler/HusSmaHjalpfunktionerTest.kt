package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.export.kommandeUnderhallCsvFilnamn
import java.time.LocalDate

class HusSmaHjalpfunktionerTest {
    @Test
    fun arSverigeForSkatteInfo_exempel() {
        assertTrue(arSverigeForSkatteInfo("SE"))
        assertTrue(arSverigeForSkatteInfo("se"))
        assertFalse(arSverigeForSkatteInfo("NO"))
        assertFalse(arSverigeForSkatteInfo(""))
    }

    @Test
    fun standardForsaljningsar_exempel() {
        assertEquals(2026, standardForsaljningsar(LocalDate.of(2026, 10, 25)))
        assertEquals(2027, standardForsaljningsar(LocalDate.of(2027, 1, 1)))
    }

    @Test
    fun formateraMatareVarde_exempel() {
        assertEquals("12345,6", formateraMatareVarde(12_345_600L))
        assertEquals("1,005", formateraMatareVarde(1_005L))
        assertEquals("1,05", formateraMatareVarde(1_050L))
        assertEquals("0,999", formateraMatareVarde(999L))
        assertEquals("0", formateraMatareVarde(0L))
    }

    @Test
    fun kommandeUnderhallCsvFilnamn_exempel() {
        assertEquals(
            "husloggen-kommande-underhall-2026-10-25.csv",
            kommandeUnderhallCsvFilnamn(LocalDate.of(2026, 10, 25)),
        )
        assertEquals(
            "husloggen-kommande-underhall-2027-01-05.csv",
            kommandeUnderhallCsvFilnamn(LocalDate.of(2027, 1, 5)),
        )
    }
}
