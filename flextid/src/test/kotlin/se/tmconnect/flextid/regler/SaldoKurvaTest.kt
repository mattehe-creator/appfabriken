package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.regler.DagTyp
import kotlin.test.assertEquals

class SaldoKurvaTest {
    
    @Test
    fun testSaldoKurva() {
        // Skapa en enkel arbetstid (480 min per arbetsdag, mån-fre)
        val arbetstid = Arbetstid(
            normaltidMinPerDag = 480,
            arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG
        )
        
        // Skapa testdagar
        val dagar = listOf(
            Dag(LocalDate.of(2026, 10, 16), LocalTime.of(8, 0), LocalTime.of(17, 30), 30), // +60 min
            Dag(LocalDate.of(2026, 10, 19), LocalTime.of(8, 0), LocalTime.of(17, 0), 30),  // +30 min
            Dag(LocalDate.of(2026, 10, 21), LocalTime.of(8, 0), LocalTime.of(16, 0), 30)   // -30 min
        )
        
        // Testa fall 1: fran = 2026-10-19, till = 2026-10-21
        val resultat1 = saldoKurva(
            dagar,
            100, // ingående saldo
            arbetstid,
            LocalDate.of(2026, 10, 19),
            LocalDate.of(2026, 10, 21)
        )
        
        assertEquals(3, resultat1.size)
        assertEquals(LocalDate.of(2026, 10, 19), resultat1[0].datum)
        assertEquals(190, resultat1[0].saldoMin) // 100 + 0 + 90 = 190 (dagen 16 är före fran, så den räknas inte in)
        assertEquals(LocalDate.of(2026, 10, 20), resultat1[1].datum)
        assertEquals(190, resultat1[1].saldoMin) // 190 + 0 = 190 (dagen 20 är ledig)
        assertEquals(LocalDate.of(2026, 10, 21), resultat1[2].datum)
        assertEquals(160, resultat1[2].saldoMin) // 190 + (-30) = 160
        
        // Testa fall 2: fran = 2026-10-22, till = 2026-10-22
        val resultat2 = saldoKurva(
            dagar,
            100, // ingående saldo
            arbetstid,
            LocalDate.of(2026, 10, 22),
            LocalDate.of(2026, 10, 22)
        )
        
        assertEquals(1, resultat2.size)
        assertEquals(LocalDate.of(2026, 10, 22), resultat2[0].datum)
        assertEquals(160, resultat2[0].saldoMin) // 100 + 0 + 0 + (-30) = 160 (dagen 22 är ledig, så 0, men vi har redan 160 från föregående test)
        
        // Testa fall 3: fran efter till
        val resultat3 = saldoKurva(
            dagar,
            100, // ingående saldo
            arbetstid,
            LocalDate.of(2026, 10, 22),
            LocalDate.of(2026, 10, 21)
        )
        
        assertEquals(0, resultat3.size)
    }
}
