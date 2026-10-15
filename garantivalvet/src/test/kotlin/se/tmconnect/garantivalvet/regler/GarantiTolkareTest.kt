package se.tmconnect.garantivalvet.regler

import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class GarantiTolkareTest {
    
    @Test
    fun testExempel() {
        assertEquals(36, tolkaGarantiManader("3 års garanti"))
        assertEquals(24, tolkaGarantiManader("Garanti: 24 månader"))
        assertEquals(12, tolkaGarantiManader("Garantitid 12 mån."))
        assertEquals(24, tolkaGarantiManader("2 år garanti"))
        assertEquals(12, tolkaGarantiManader("Ett års garanti"))
        assertEquals(24, tolkaGarantiManader("GARANTI 2 AR"))
        assertEquals(24, tolkaGarantiManader("Warranty: 2 years"))
        assertEquals(36, tolkaGarantiManader("Köp 24 månader räntefritt, 3 års garanti"))
        assertEquals(36, tolkaGarantiManader("Elgiganten\n2026-08-01\nSumma 1 299,00\n3 års garanti"))
        assertEquals(6, tolkaGarantiManader("6 månaders garanti"))
        assertEquals(null, tolkaGarantiManader("Garanti 30 dagar"))
        assertEquals(null, tolkaGarantiManader("3 års reklamationsrätt"))
        assertEquals(null, tolkaGarantiManader("Ingen garanti"))
        assertEquals(null, tolkaGarantiManader("Garanti: 0 månader"))
        assertEquals(null, tolkaGarantiManader("Garanti 200 år"))
        assertEquals(null, tolkaGarantiManader("Garanti\n3 år"))
        assertEquals(null, tolkaGarantiManader("24 månader"))
        assertEquals(null, tolkaGarantiManader(""))
    }
}
