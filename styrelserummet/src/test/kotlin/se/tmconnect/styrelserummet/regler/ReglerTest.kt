package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class ReglerTest {
    @Test
    fun stamAvStammerNarSummanMatchar() {
        val komponenter = listOf(
            komponent("A", 6_000_000),
            komponent("B", 4_000_000),
        )
        val avstamning = stamAv(komponenter, 10_000_000)
        assertTrue(avstamning.stammer)
        assertEquals(0L, avstamning.differens)
    }

    @Test
    fun fordelaLaggerRestPaStorstaPosten() {
        val map = fordela(
            100,
            listOf(Komponentgrupp.STOMME to 34, Komponentgrupp.FASAD to 33, Komponentgrupp.TAK to 33),
        )
        assertEquals(100L, map.values.sum())
    }

    @Test
    fun valideraFangerOgiltigPeriod() {
        val fel = validera(
            komponent("X", 100).copy(nyttjandeperiodAr = 0),
            2025,
        )
        assertTrue(fel.any { it.kod == "NYTTJANDEPERIOD" })
    }

    private fun komponent(namn: String, varde: Long) = Komponent(
        namn = namn,
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = varde,
        anskaffningsar = 2010,
        nyttjandeperiodAr = 40,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )
}
