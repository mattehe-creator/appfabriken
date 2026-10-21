package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class BokfortVardeGransfallArsskifteTest {

    @Test
    fun exempel90kVidArsskifte2024Till2025() {
        val k = komponent(90_000, 2025, 3, 0)
        assertEquals(90_000L, bokfortVarde(k, 2024))
        assertEquals(60_000L, bokfortVarde(k, 2025))
        assertEquals(30_000L, bokfortVarde(k, 2024) - bokfortVarde(k, 2025))
        assertEquals(30_000L, avskrivningFor(k, 2025))
    }

    @Test
    fun arsskifteVidAnskaffningsarForeOchEfterForstaAvskrivning() {
        val k = komponent(90_000, 2025, 3, 0)
        val fore = bokfortVarde(k, k.anskaffningsar - 1)
        val efter = bokfortVarde(k, k.anskaffningsar)
        assertEquals(90_000L, fore)
        assertTrue(efter <= fore)
        assertEquals(avskrivningFor(k, k.anskaffningsar), fore - efter)
    }

    @Test
    fun bokfortVardeMinskarMedAvskrivningInomNyttiandeperiod() {
        val k = komponent(90_000, 2025, 3, 0)
        val sistaAvskrivningsar = k.anskaffningsar + k.nyttjandeperiodAr - 1
        for (ar in k.anskaffningsar - 1 until sistaAvskrivningsar) {
            val nuvarande = bokfortVarde(k, ar)
            val nasta = bokfortVarde(k, ar + 1)
            assertTrue("bokfört värde ska minska eller stanna vid restvärde", nasta <= nuvarande)
            if (ar + 1 in k.anskaffningsar..sistaAvskrivningsar) {
                assertEquals(
                    avskrivningFor(k, ar + 1),
                    nuvarande - nasta,
                )
            }
        }
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
