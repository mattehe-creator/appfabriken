package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LedighetsPeriodTest {
    private val at = Arbetstid(normaltidMinPerDag = 480, arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG)

    @Test
    fun barArbetsdagarUtanHelgOchRodDag() {
        // Måndag 1 juni till söndag 14 juni 2026: tio vardagar, men lördag 6 juni (nationaldagen) är redan helg.
        val dagar = dagarForLedighetsperiod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 14), DagTyp.SEMESTER, at, emptySet())
        assertEquals(10, dagar.size)
        assertTrue(dagar.all { it.typ == DagTyp.SEMESTER && it.start == null && it.slut == null })
    }

    @Test
    fun midsommarveckanUtanAfton() {
        // 15-21 juni 2026: midsommarafton fredag 19 juni är ledig (standard), så fyra dagar.
        val dagar = dagarForLedighetsperiod(LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 21), DagTyp.SEMESTER, at, emptySet())
        assertEquals(listOf(15, 16, 17, 18), dagar.map { it.datum.dayOfMonth })
        val medAfton =
            dagarForLedighetsperiod(LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 21), DagTyp.SEMESTER, at.copy(aftnarLediga = false), emptySet())
        assertEquals(5, medAfton.size)
    }

    @Test
    fun befintligaDagarHoppasOver() {
        val dagar =
            dagarForLedighetsperiod(
                LocalDate.of(2026, 10, 19),
                LocalDate.of(2026, 10, 23),
                DagTyp.FLEX,
                at,
                setOf(LocalDate.of(2026, 10, 20)),
            )
        assertEquals(listOf(19, 21, 22, 23), dagar.map { it.datum.dayOfMonth })
    }

    @Test
    fun ogiltigaPerioder() {
        val d = LocalDate.of(2026, 10, 19)
        assertTrue(dagarForLedighetsperiod(d, d.minusDays(1), DagTyp.SEMESTER, at, emptySet()).isEmpty())
        assertTrue(dagarForLedighetsperiod(d, d.plusDays(400), DagTyp.SEMESTER, at, emptySet()).isEmpty())
        assertTrue(dagarForLedighetsperiod(d, d, DagTyp.ARBETE, at, emptySet()).isEmpty())
        assertEquals(1, dagarForLedighetsperiod(d, d, DagTyp.SJUK, at, emptySet()).size)
    }

    @Test
    fun semesterVeckaRaknasSomUttagen() {
        val dagar = dagarForLedighetsperiod(LocalDate.of(2026, 7, 6), LocalDate.of(2026, 7, 10), DagTyp.SEMESTER, at, emptySet())
        val sammanfattning = semesterSammanfattning(dagar, LocalDate.of(2026, 7, 31), ARBETSDAGAR_MANDAG_FREDAG)
        assertEquals(5, sammanfattning.uttagna)
        assertEquals(20, sammanfattning.kvar)
    }
}
