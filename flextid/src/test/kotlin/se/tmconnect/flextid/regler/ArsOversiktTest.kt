package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

class ArsOversiktTest {
    private val installning =
        Installning(normaltidMinPerDag = 480, standardRastMin = 30, ingangssaldoMin = 60)

    @Test
    fun exempelDagar2026() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2025, 12, 1),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(17, 0),
                    rastMin = 0,
                ),
                Dag(
                    datum = LocalDate.of(2026, 1, 5),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(17, 0),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 3, 2),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 0),
                    rastMin = 60,
                ),
            )
        val rader = arsOversikt(dagar, 2026, installning)
        assertEquals(12, rader.size)
        assertEquals(ManadsRad(1, 510, 30, 150), rader[0])
        assertEquals(ManadsRad(2, 0, 0, 150), rader[1])
        assertEquals(ManadsRad(3, 420, -60, 90), rader[2])
        assertEquals(ManadsRad(4, 0, 0, 90), rader[3])
        for (manad in 5..12) {
            assertEquals(ManadsRad(manad, 0, 0, 90), rader[manad - 1])
        }
    }

    @Test
    fun tomDagarLista() {
        val rader = arsOversikt(emptyList(), 2026, installning)
        assertEquals(12, rader.size)
        for (manad in 1..12) {
            assertEquals(ManadsRad(manad, 0, 0, 60), rader[manad - 1])
        }
    }
}
