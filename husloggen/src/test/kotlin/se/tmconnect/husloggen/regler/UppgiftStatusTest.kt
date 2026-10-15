package se.tmconnect.husloggen.regler

import org.junit.Test
import org.junit.Assert.assertEquals
import java.time.LocalDate

class UppgiftStatusTest {
    
    @Test
    fun testForsenad() {
        val idag = LocalDate.of(2026, 10, 16)
        val nasta = LocalDate.of(2026, 10, 15)
        val resultat = uppgiftStatus(nasta, idag)
        assertEquals(UppgiftStatus.FORSENAD, resultat)
    }
    
    @Test
    fun testSnartExact() {
        val idag = LocalDate.of(2026, 10, 16)
        val nasta = LocalDate.of(2026, 10, 16)
        val resultat = uppgiftStatus(nasta, idag)
        assertEquals(UppgiftStatus.SNART, resultat)
    }
    
    @Test
    fun testSnartWithinLimit() {
        val idag = LocalDate.of(2026, 10, 16)
        val nasta = LocalDate.of(2026, 11, 15)
        val resultat = uppgiftStatus(nasta, idag)
        assertEquals(UppgiftStatus.SNART, resultat)
    }
    
    @Test
    fun testSenare() {
        val idag = LocalDate.of(2026, 10, 16)
        val nasta = LocalDate.of(2026, 11, 16)
        val resultat = uppgiftStatus(nasta, idag)
        assertEquals(UppgiftStatus.SENARE, resultat)
    }
}
