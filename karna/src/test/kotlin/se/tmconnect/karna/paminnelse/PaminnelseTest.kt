package se.tmconnect.karna.paminnelse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PaminnelseTest {
    private val idag = LocalDate.of(2026, 6, 1)
    private val dagarFore = 7

    private fun frist(slut: LocalDate, nyckel: String = "1:$slut") =
        PaminnelseFrist(nyckel = nyckel, slutdatum = slut)

    @Test
    fun slutOm7DagarAviseras() {
        val slut = idag.plusDays(7)
        val resultat = fristerSomSkaAviseras(listOf(frist(slut)), idag, emptySet(), dagarFore)
        assertEquals(listOf(frist(slut)), resultat)
    }

    @Test
    fun slutOm8DagarAviserasInte() {
        val slut = idag.plusDays(8)
        val resultat = fristerSomSkaAviseras(listOf(frist(slut)), idag, emptySet(), dagarFore)
        assertTrue(resultat.isEmpty())
    }

    @Test
    fun slutIdagAviseras() {
        val resultat = fristerSomSkaAviseras(listOf(frist(idag)), idag, emptySet(), dagarFore)
        assertEquals(listOf(frist(idag)), resultat)
    }

    @Test
    fun slutIgårAviseras() {
        val slut = idag.minusDays(1)
        val resultat = fristerSomSkaAviseras(listOf(frist(slut)), idag, emptySet(), dagarFore)
        assertEquals(listOf(frist(slut)), resultat)
    }

    @Test
    fun redanAviseradAviserasInte() {
        val slut = idag.plusDays(3)
        val f = frist(slut)
        val resultat = fristerSomSkaAviseras(listOf(f), idag, setOf(f.nyckel), dagarFore)
        assertTrue(resultat.isEmpty())
    }

    @Test
    fun nyttSlutdatumGerNyNyckel() {
        val f1 = frist(idag.plusDays(5), nyckel = "42:${idag.plusDays(5)}")
        val f2 = frist(idag.plusDays(6), nyckel = "42:${idag.plusDays(6)}")
        assertTrue(f1.nyckel != f2.nyckel)
        val resultat = fristerSomSkaAviseras(listOf(f2), idag, setOf(f1.nyckel), dagarFore)
        assertEquals(listOf(f2), resultat)
    }
}
