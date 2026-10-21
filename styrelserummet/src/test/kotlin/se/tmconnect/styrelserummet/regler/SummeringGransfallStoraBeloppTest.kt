package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class SummeringGransfallStoraBeloppTest {

    @Test
    fun summeraPerGruppStoraBeloppSammaGrupp() {
        val komponenter = listOf(
            komponent(Komponentgrupp.STOMME, 900_000_000_000L, 2010, 40),
            komponent(Komponentgrupp.STOMME, 900_000_000_000L, 2020, 40),
        )
        val arsslut = 2025
        val grupper = summeraPerGrupp(komponenter, arsslut)
        assertEquals(1, grupper.size)
        assertEquals(Komponentgrupp.STOMME, grupper[0].grupp)
        assertEquals(1_800_000_000_000L, grupper[0].anskaffning)
    }

    @Test
    fun summeraPerGruppTotalArSummaAvGrupper() {
        val arsslut = 2025
        val komponenter = listOf(
            komponent(Komponentgrupp.STOMME, 100L, 2010, 10),
            komponent(Komponentgrupp.EL, 200L, 2015, 20),
            komponent(Komponentgrupp.TAK, 300L, 2018, 15),
        )
        val grupper = summeraPerGrupp(komponenter, arsslut)
        assertEquals(3, grupper.size)
        assertEquals(
            komponenter.sumOf { it.anskaffningsvarde },
            grupper.sumOf { it.anskaffning },
        )
        assertEquals(
            komponenter.sumOf { avskrivningFor(it, arsslut) },
            grupper.sumOf { it.avskrivningAret },
        )
        assertEquals(
            komponenter.sumOf { bokfortVarde(it, arsslut) },
            grupper.sumOf { it.bokfort },
        )
    }

    private fun komponent(grupp: Komponentgrupp, varde: Long, anskaffningsar: Int, period: Int) =
        Komponent(
            namn = "Test",
            grupp = grupp,
            anskaffningsvarde = varde,
            anskaffningsar = anskaffningsar,
            nyttjandeperiodAr = period,
            restvarde = 0,
            kalla = "test",
            sakerhet = Sakerhet.HOG,
        )
}
