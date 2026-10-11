package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PaminnelseTest {
    private val idag = LocalDate.of(2026, 6, 1)
    private val kopId = 42L

    private fun frist(slut: LocalDate) = Frist(kopId, FristTyp.GARANTI, slut)

    @Test
    fun slutOm30DagarAviseras() {
        val slut = idag.plusDays(30)
        val resultat = fristerSomSkaAviseras(listOf(frist(slut)), idag, emptySet())
        assertEquals(listOf(frist(slut)), resultat)
    }

    @Test
    fun slutOm31DagarAviserasInte() {
        val slut = idag.plusDays(31)
        val resultat = fristerSomSkaAviseras(listOf(frist(slut)), idag, emptySet())
        assertTrue(resultat.isEmpty())
    }

    @Test
    fun slutIdagAviseras() {
        val resultat = fristerSomSkaAviseras(listOf(frist(idag)), idag, emptySet())
        assertEquals(listOf(frist(idag)), resultat)
    }

    @Test
    fun slutIgårAviserasInte() {
        val slut = idag.minusDays(1)
        val resultat = fristerSomSkaAviseras(listOf(frist(slut)), idag, emptySet())
        assertTrue(resultat.isEmpty())
    }

    @Test
    fun redanAviseradAviserasInte() {
        val slut = idag.plusDays(10)
        val f = frist(slut)
        val nyckel = fristNyckel(f)
        val resultat = fristerSomSkaAviseras(listOf(f), idag, setOf(nyckel))
        assertTrue(resultat.isEmpty())
    }

    @Test
    fun nyckelInkluderarSlutdatum() {
        val f1 = frist(idag.plusDays(5))
        val f2 = f1.copy(slutdatum = idag.plusDays(6))
        assertTrue(fristNyckel(f1) != fristNyckel(f2))
    }

    @Test
    fun garantiOchReklamationInom30DagarAviserasBada() {
        val garantiSlut = idag.plusDays(20)
        val reklamationSlut = idag.plusDays(25)
        val frister = listOf(
            Frist(kopId, FristTyp.GARANTI, garantiSlut),
            Frist(kopId, FristTyp.REKLAMATION, reklamationSlut),
        )
        val resultat = fristerSomSkaAviseras(frister, idag, emptySet())
        assertEquals(frister, resultat)
    }

    @Test
    fun nyckelSkiljerPaTyp() {
        val slut = idag.plusDays(10)
        val garanti = Frist(kopId, FristTyp.GARANTI, slut)
        val reklamation = Frist(kopId, FristTyp.REKLAMATION, slut)
        assertTrue(fristNyckel(garanti) != fristNyckel(reklamation))
    }
}
