package se.tmconnect.flextid.widget

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.regler.ARBETSDAGAR_MANDAG_FREDAG
import se.tmconnect.flextid.regler.Arbetstid
import se.tmconnect.flextid.regler.StampelLage

class WidgetTillstandTest {
    private val at = Arbetstid(normaltidMinPerDag = 480, arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG)
    private val onsdag = LocalDate.of(2026, 10, 21)
    private val tisdag = onsdag.minusDays(1)

    @Test
    fun ingenDagIdag() {
        val igar = Dag(datum = tisdag, start = LocalTime.of(8, 0), slut = LocalTime.of(17, 30), rastMin = 30)
        val t = widgetTillstand(listOf(igar), 15, at, onsdag)
        assertEquals(75, t.saldoMin)
        assertEquals(StampelLage.STAMPLA_IN, t.lage)
        assertNull(t.start)
    }

    @Test
    fun instampladIdag() {
        val idag = Dag(datum = onsdag, start = LocalTime.of(7, 45), rastMin = 30)
        val t = widgetTillstand(listOf(idag), 0, at, onsdag)
        assertEquals(0, t.saldoMin)
        assertEquals(StampelLage.STAMPLA_UT, t.lage)
        assertEquals(LocalTime.of(7, 45), t.start)
    }

    @Test
    fun klarIdag() {
        val idag = Dag(datum = onsdag, start = LocalTime.of(8, 0), slut = LocalTime.of(16, 0), rastMin = 30)
        val t = widgetTillstand(listOf(idag), 0, at, onsdag)
        assertEquals(-30, t.saldoMin)
        assertEquals(StampelLage.KLAR, t.lage)
    }
}
