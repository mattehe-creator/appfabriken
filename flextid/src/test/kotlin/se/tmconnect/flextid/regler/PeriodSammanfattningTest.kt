package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

class PeriodSammanfattningTest {
    private val installning = Installning(normaltidMinPerDag = 480, standardRastMin = 30)
    private val fran = LocalDate.of(2026, 10, 12)
    private val till = LocalDate.of(2026, 10, 18)

    @Test
    fun vecka20261012Till20261018() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 12),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(7, 45),
                    slut = LocalTime.of(16, 45),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 14),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                Dag(datum = LocalDate.of(2026, 10, 15), typ = DagTyp.SEMESTER),
                Dag(
                    datum = LocalDate.of(2026, 10, 16),
                    start = LocalTime.of(8, 15),
                    slut = LocalTime.of(15, 0),
                    rastMin = 15,
                ),
            )
        val s = periodSammanfattning(dagar, fran, till, installning, aftnarLediga = false)
        assertEquals(PeriodSammanfattning(arbetadMin = 1860, normaltidMin = 2400, utfallMin = -60), s)
    }

    @Test
    fun tomDagarListaSammaIntervall() {
        val s = periodSammanfattning(emptyList(), fran, till, installning, aftnarLediga = false)
        assertEquals(PeriodSammanfattning(arbetadMin = 0, normaltidMin = 2400, utfallMin = 0), s)
    }
}
