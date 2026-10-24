package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.paminnelse.skaPaminnelseStamplaIn

/** En flexledig dag tar dagens normaltid från saldot; annan ledighet påverkar inte saldot. */
class FlexledigTest {
    private val at = Arbetstid(normaltidMinPerDag = 480, arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG)
    private val onsdag = LocalDate.of(2026, 10, 21)
    private val fredag = LocalDate.of(2026, 10, 23)

    @Test
    fun flexledigDragerNormaltid() {
        assertEquals(-480, dagensUtfall(Dag(datum = onsdag, typ = DagTyp.FLEX), at))
    }

    @Test
    fun flexledigKortFredag() {
        val kortFredag = at.copy(perVeckodag = mapOf(DayOfWeek.FRIDAY to 420))
        assertEquals(-420, dagensUtfall(Dag(datum = fredag, typ = DagTyp.FLEX), kortFredag))
    }

    @Test
    fun flexledigRodDagDragerInget() {
        assertEquals(0, dagensUtfall(Dag(datum = LocalDate.of(2026, 12, 25), typ = DagTyp.FLEX), at))
    }

    @Test
    fun annanLedighetPaverkarInteSaldot() {
        assertEquals(0, dagensUtfall(Dag(datum = onsdag, typ = DagTyp.LEDIG), at))
    }

    @Test
    fun saldoEfterFlexdag() {
        val inst = Installning(normaltidMinPerDag = 480, ingangssaldoMin = 600)
        assertEquals(120, beraknaSaldo(listOf(Dag(datum = onsdag, typ = DagTyp.FLEX)), inst))
    }

    @Test
    fun ingenInstamplingsPaminnelsePaLedigDag() {
        val inst = Installning()
        val nio = LocalTime.of(9, 0)
        assertFalse(skaPaminnelseStamplaIn(Dag(datum = onsdag, typ = DagTyp.FLEX), inst, onsdag, nio, 8 * 60))
        assertFalse(skaPaminnelseStamplaIn(Dag(datum = onsdag, typ = DagTyp.SEMESTER), inst, onsdag, nio, 8 * 60))
        assertTrue(skaPaminnelseStamplaIn(null, inst, onsdag, nio, 8 * 60))
    }
}
