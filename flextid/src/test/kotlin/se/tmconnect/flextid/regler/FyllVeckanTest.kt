package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FyllVeckanTest {
    private val at = Arbetstid(normaltidMinPerDag = 480, arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG)
    private val mandag = LocalDate.of(2026, 10, 19)
    private val start = LocalTime.of(8, 0)
    private val slut = LocalTime.of(16, 30)

    @Test
    fun barTommaDagarTillOchMedIdag() {
        // Onsdag 21 oktober; tisdag är redan registrerad.
        val dagar = dagarAttFyllaMedVanligaTider(mandag, LocalDate.of(2026, 10, 21), at, setOf(LocalDate.of(2026, 10, 20)), start, slut, 30)
        assertEquals(listOf(19, 21), dagar.map { it.datum.dayOfMonth })
        assertTrue(dagar.all { it.start == start && it.slut == slut && it.rastMin == 30 && it.typ == DagTyp.ARBETE })
        assertEquals(0, dagensUtfall(dagar.first(), at))
    }

    @Test
    fun helaVeckanUtanHelg() {
        val dagar = dagarAttFyllaMedVanligaTider(mandag, LocalDate.of(2026, 10, 30), at, emptySet(), start, slut, 30)
        assertEquals(5, dagar.size)
    }

    @Test
    fun rodDagFyllsInte() {
        // Vecka 14 2026: långfredag 3 april och annandag påsk 6 april (nästa vecka).
        val dagar = dagarAttFyllaMedVanligaTider(LocalDate.of(2026, 3, 30), LocalDate.of(2026, 4, 5), at, emptySet(), start, slut, 30)
        assertEquals(listOf(30, 31, 1, 2), dagar.map { it.datum.dayOfMonth })
    }

    @Test
    fun framtidaVeckaOchOgiltigaTider() {
        assertTrue(dagarAttFyllaMedVanligaTider(mandag.plusWeeks(1), mandag, at, emptySet(), start, slut, 30).isEmpty())
        assertTrue(dagarAttFyllaMedVanligaTider(mandag, mandag.plusDays(6), at, emptySet(), slut, start, 30).isEmpty())
    }
}
