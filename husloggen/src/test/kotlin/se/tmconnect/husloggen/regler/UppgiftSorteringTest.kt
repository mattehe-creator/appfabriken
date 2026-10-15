package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.Uppgift
import java.time.LocalDate

class UppgiftSorteringTest {

    @Test
    fun testSorteraUppgifterMedExempel() {
        val idag = LocalDate.of(2026, 10, 16)
        
        val uppgiftA = Uppgift(
            namn = "Tak",
            startDatum = LocalDate.of(2026, 1, 1),
            intervallManader = 12,
            senastGjord = null
        )
        
        val uppgiftB = Uppgift(
            namn = "Fönster",
            startDatum = LocalDate.of(2026, 6, 1),
            intervallManader = 6,
            senastGjord = LocalDate.of(2026, 6, 1)
        )
        
        val uppgiftC = Uppgift(
            namn = "altan",
            startDatum = LocalDate.of(2026, 11, 1),
            intervallManader = 12,
            senastGjord = null
        )
        
        val lista = listOf(uppgiftA, uppgiftB, uppgiftC)
        val resultat = sorteraUppgifter(lista, idag)
        
        assertEquals(listOf(uppgiftA, uppgiftC, uppgiftB), resultat)
    }

    @Test
    fun testSorteraUppgifterSammaNastaDatum() {
        val idag = LocalDate.of(2026, 10, 16)
        
        val uppgiftA = Uppgift(
            namn = "B",
            startDatum = LocalDate.of(2026, 1, 1),
            intervallManader = 12,
            senastGjord = null
        )
        
        val uppgiftB = Uppgift(
            namn = "a",
            startDatum = LocalDate.of(2026, 1, 1),
            intervallManader = 12,
            senastGjord = null
        )
        
        val lista = listOf(uppgiftA, uppgiftB)
        val resultat = sorteraUppgifter(lista, idag)
        
        assertEquals(listOf(uppgiftB, uppgiftA), resultat)
    }
}
