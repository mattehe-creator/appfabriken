package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvstamningGransfallTest {

    @Test
    fun exaktTraffStammerMedDifferensNoll() {
        val komponenter = listOf(
            komponent(6_000_000),
            komponent(4_000_000),
        )
        val avstamning = stamAv(komponenter, 10_000_000)
        assertTrue(avstamning.stammer)
        assertEquals(0L, avstamning.differens)
        assertEquals(10_000_000L, avstamning.summa)
    }

    /** stamAv har ingen tolerans: skillnad 1 kr räknas inte som stämmer (differens måste vara exakt 0). */
    @Test
    fun skillnadEnKronaStammerInte() {
        val komponenter = listOf(komponent(9_999_999))
        val avstamning = stamAv(komponenter, 10_000_000)
        assertFalse(avstamning.stammer)
        assertEquals(1L, avstamning.differens)
        assertEquals(9_999_999L, avstamning.summa)
    }

    @Test
    fun byggnadsvärdeNollMedKomponenterGerNegativDifferens() {
        val komponenter = listOf(komponent(100_000))
        val avstamning = stamAv(komponenter, 0)
        assertFalse(avstamning.stammer)
        assertEquals(-100_000L, avstamning.differens)
        assertEquals(100_000L, avstamning.summa)
    }

    @Test
    fun komponenterHogreAnByggnadGerNegativDifferens() {
        val komponenter = listOf(
            komponent(6_000_000),
            komponent(5_000_000),
        )
        val avstamning = stamAv(komponenter, 10_000_000)
        assertFalse(avstamning.stammer)
        assertTrue(avstamning.differens < 0)
        assertEquals(-1_000_000L, avstamning.differens)
        assertEquals(11_000_000L, avstamning.summa)
    }

    private fun komponent(varde: Long) = Komponent(
        namn = "Test",
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = varde,
        anskaffningsar = 2010,
        nyttjandeperiodAr = 40,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )
}
