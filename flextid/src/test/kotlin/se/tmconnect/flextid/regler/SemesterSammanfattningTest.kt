package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class SemesterSammanfattningTest {
    private val mask = ARBETSDAGAR_MANDAG_FREDAG
    private fun sem(s: String) = Dag(datum = LocalDate.parse(s), typ = DagTyp.SEMESTER)
    private val dagar =
        listOf("2026-07-06", "2026-07-07", "2026-07-08", "2026-07-09", "2026-07-10", "2026-12-28", "2026-12-29", "2026-03-30")
            .map(::sem)

    @Test
    fun raknaTyp() {
        val fran = LocalDate.of(2026, 4, 1)
        val till = LocalDate.of(2027, 3, 31)
        val d = listOf(sem("2026-07-06"), sem("2026-07-07"), sem("2026-07-11"))
        assertEquals(2, raknaDagarAvTyp(d, DagTyp.SEMESTER, fran, till, mask))
        assertEquals(0, raknaDagarAvTyp(d, DagTyp.SJUK, fran, till, mask))
        assertEquals(1, raknaDagarAvTyp(listOf(sem("2027-03-31")), DagTyp.SEMESTER, fran, till, mask))
        assertEquals(0, raknaDagarAvTyp(d, DagTyp.SEMESTER, till, fran, mask))
    }

    @Test
    fun sammanfattning() {
        val s = semesterSammanfattning(dagar, LocalDate.of(2026, 10, 21), mask)
        assertEquals(SemesterAr(LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31)), s.ar)
        assertEquals(5, s.uttagna)
        assertEquals(2, s.planerade)
        assertEquals(18, s.kvar)
        assertEquals(-1, semesterSammanfattning(dagar, LocalDate.of(2026, 10, 21), mask, dagarPerAr = 6).kvar)
        val mars = semesterSammanfattning(dagar, LocalDate.of(2026, 3, 31), mask)
        assertEquals(1, mars.uttagna)
        assertEquals(0, mars.planerade)
        assertEquals(24, mars.kvar)
    }
}
