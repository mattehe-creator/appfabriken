package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StammaDataPrisbasbeloppTest {

    @Test
    fun prisbasbeloppFor2026Returnerar59200() {
        val data = lasStammaData(
            listOf(
                "typ;ar;varde;enhet;kalla",
                "prisbasbelopp;2026;59200;kr;SCB prisbasbelopp 2026",
            ),
        )

        assertEquals(59200L, prisbasbeloppFor(2026, data))
    }

    @Test
    fun prisbasbeloppForSaknatArReturnerarNull() {
        val data = lasStammaData(
            listOf(
                "typ;ar;varde;enhet;kalla",
                "prisbasbelopp;2026;59200;kr;SCB prisbasbelopp 2026",
            ),
        )

        assertNull(prisbasbeloppFor(2019, data))
    }

    @Test
    fun prisbasbeloppForFelEnhetReturnerarNull() {
        val data = lasStammaData(
            listOf(
                "typ;ar;varde;enhet;kalla",
                "prisbasbelopp;2026;59200;promille_av_pbb;STAMMA.md",
            ),
        )

        assertNull(prisbasbeloppFor(2026, data))
    }

    @Test
    fun lasStammaDataFranResursGer59200For2026() {
        val data = lasStammaDataFranResurs()

        assertEquals(59200L, prisbasbeloppFor(2026, data))
    }
}
