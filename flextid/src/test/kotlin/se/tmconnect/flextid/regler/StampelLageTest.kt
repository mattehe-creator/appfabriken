package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class StampelLageTest {
    private val d = LocalDate.of(2026, 10, 22)

    @Test
    fun lagen() {
        assertEquals(StampelLage.STAMPLA_IN, stampelLage(null))
        assertEquals(StampelLage.STAMPLA_IN, stampelLage(Dag(datum = d, typ = DagTyp.SEMESTER)))
        assertEquals(StampelLage.STAMPLA_UT, stampelLage(Dag(datum = d, start = LocalTime.of(8, 0))))
        assertEquals(StampelLage.KLAR, stampelLage(Dag(datum = d, start = LocalTime.of(8, 0), slut = LocalTime.of(16, 0))))
    }
}
