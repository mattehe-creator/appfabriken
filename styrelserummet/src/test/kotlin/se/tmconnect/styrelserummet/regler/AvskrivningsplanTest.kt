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

    @Test
    fun avskrivningsplan_med_komponenter() {
        val k1 = komponent(200_000, 2010, 40, 0)
        val k2 = komponent(300_000, 2020, 10, 0)
        
        val result = avskrivningsplan(listOf(k1, k2), 2026, 5)

        assertEquals(5, result.size)
        assertEquals(PlanAr(2026, 60_000L, 780_000L), result[0])
        assertEquals(PlanAr(2027, 60_000L, 720_000L), result[1])
        assertEquals(PlanAr(2028, 60_000L, 660_000L), result[2])
        assertEquals(PlanAr(2029, 60_000L, 600_000L), result[3])
        assertEquals(PlanAr(2030, 30_000L, 570_000L), result[4])
    }

    @Test
    fun avskrivningsplan_tom_list() {
        val result = avskrivningsplan(emptyList(), 2026, 2)

        assertEquals(2, result.size)
        assertEquals(PlanAr(2026, 0L, 0L), result[0])
        assertEquals(PlanAr(2027, 0L, 0L), result[1])
    }
}
