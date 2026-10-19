package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningTest {
    private fun komponent(period: Int, varde: Long = 1_200_000, ar: Int = 2010) = Komponent(
        namn = "Test",
        grupp = Komponentgrupp.STOMME.name,
        anskaffningsvarde = varde,
        anskaffningsar = ar,
        nyttjandeperiodAr = period,
        kalla = "test",
        sakerhet = Sakerhet.HOG.name,
    )

    @Test
    fun linjarAvskrivning() {
        val k = komponent(40)
        assertEquals(30_000, avskrivningFor(k, 2010))
        assertEquals(720_000, bokfortVarde(k, 2025))
        assertEquals(1_200_000, bokfortVarde(k, 2009))
    }

    @Test
    fun sistaArTarResten() {
        val k = komponent(3, 100_000, 2020)
        assertEquals(33_333, avskrivningFor(k, 2020))
        assertEquals(33_333, avskrivningFor(k, 2021))
        assertEquals(33_334, avskrivningFor(k, 2022))
    }
}
