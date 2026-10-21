package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningGransfallSummatakTest {

    @Test
    fun langPeriodMedAvrundningSummerarTillAnskaffningsvarde() {
        val k = komponent(1_000_000, 2000, 30, 0)
        val sistaAr = k.anskaffningsar + k.nyttjandeperiodAr - 1
        var summa = 0L
        for (ar in k.anskaffningsar..sistaAr) {
            val belopp = avskrivningFor(k, ar)
            assertTrue("Årsbelopp ska inte vara negativt (år $ar)", belopp >= 0L)
            summa += belopp
        }
        assertEquals(1_000_000L, summa)
        assertEquals(
            k.anskaffningsvarde - k.restvarde,
            summa,
        )
    }

    @Test
    fun kortPeriodMedAvrundningUppatSummerarTillAnskaffningsvarde() {
        val k = komponent(100_000, 2020, 3, 0)
        val sistaAr = k.anskaffningsar + k.nyttjandeperiodAr - 1
        var summa = 0L
        for (ar in k.anskaffningsar..sistaAr) {
            val belopp = avskrivningFor(k, ar)
            assertTrue("Årsbelopp ska inte vara negativt (år $ar)", belopp >= 0L)
            summa += belopp
        }
        assertEquals(100_000L, summa)
        assertEquals(33_333L, avskrivningFor(k, 2020))
        assertEquals(33_333L, avskrivningFor(k, 2021))
        assertEquals(33_334L, avskrivningFor(k, 2022))
    }

    private fun komponent(varde: Long, anskaffningsar: Int, period: Int, restvarde: Long) = Komponent(
        namn = "Test",
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = varde,
        anskaffningsar = anskaffningsar,
        nyttjandeperiodAr = period,
        restvarde = restvarde,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )
}
