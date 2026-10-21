package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AvskrivningsplanTest {

    private fun komponent(varde: Long, anskaffningsar: Int, period: Int, restvarde: Long) =
        Komponent(
            namn = "Tak",
            grupp = Komponentgrupp.TAK,
            anskaffningsvarde = varde,
            anskaffningsar = anskaffningsar,
            nyttjandeperiodAr = period,
            restvarde = restvarde,
            kalla = "test",
            sakerhet = Sakerhet.MEDEL,
        )

    @Test
    fun planForKomponent_sistaArINyttjandeperiod() {
        val k = komponent(300_000, 2020, 10, 0)

        val result = planForKomponent(k, 2028, 3)

        assertEquals(3, result.size)
        assertEquals(PlanAr(2028, 30_000L, 30_000L), result[0])
        assertEquals(PlanAr(2029, 30_000L, 0L), result[1])
        assertEquals(PlanAr(2030, 0L, 0L), result[2])
    }

    @Test
    fun planForKomponent_foreNyttjandeperiod() {
        val k = komponent(300_000, 2020, 10, 0)

        val result = planForKomponent(k, 2018, 2)

        assertEquals(2, result.size)
        assertEquals(PlanAr(2018, 0L, 300_000L), result[0])
        assertEquals(PlanAr(2019, 0L, 300_000L), result[1])
    }

    @Test
    fun planForKomponent_nollAr() {
        val k = komponent(300_000, 2020, 10, 0)

        assertEquals(emptyList<PlanAr>(), planForKomponent(k, 2020, 0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun planForKomponent_flerAn100Ar() {
        val k = komponent(300_000, 2020, 10, 0)

        planForKomponent(k, 2020, 101)
    }
}
