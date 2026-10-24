package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

/**
 * Röda dagar (och aftnar när de är lediga) har ingen normaltid. Arbete en sådan dag
 * ska ge plus, så att saldot stämmer med månadens normaltid.
 */
class HelgdagUtfallTest {
    private val kristiHimmelfard = LocalDate.of(2026, 5, 14) // torsdag
    private val midsommarafton = LocalDate.of(2026, 6, 19) // fredag
    private val vanligTorsdag = LocalDate.of(2026, 5, 21)

    private fun attaTimmar(datum: LocalDate) =
        Dag(datum = datum, start = LocalTime.of(8, 0), slut = LocalTime.of(16, 30), rastMin = 30)

    @Test
    fun arbeteRodDagGerPlus() {
        assertEquals(480, dagensUtfall(attaTimmar(kristiHimmelfard), 480, ARBETSDAGAR_MANDAG_FREDAG))
    }

    @Test
    fun arbeteVanligDagGerNoll() {
        assertEquals(0, dagensUtfall(attaTimmar(vanligTorsdag), 480, ARBETSDAGAR_MANDAG_FREDAG))
    }

    @Test
    fun aftonFoljerInstallningen() {
        val dag = attaTimmar(midsommarafton)
        assertEquals(480, dagensUtfall(dag, 480, ARBETSDAGAR_MANDAG_FREDAG, aftnarLediga = true))
        assertEquals(0, dagensUtfall(dag, 480, ARBETSDAGAR_MANDAG_FREDAG, aftnarLediga = false))
    }

    @Test
    fun saldoRaknarRodDagSomPlus() {
        val inst = Installning(normaltidMinPerDag = 480, ingangssaldoMin = 0)
        val dagar = listOf(attaTimmar(kristiHimmelfard), attaTimmar(vanligTorsdag))
        assertEquals(480, beraknaSaldo(dagar, inst))
    }

    @Test
    fun manadensUtfallStammerMedNormaltid() {
        // Maj 2026: arbeta 8 timmar alla vardagar, även Kristi himmelfärd.
        val maj = YearMonth.of(2026, 5)
        val inst = Installning(normaltidMinPerDag = 480, ingangssaldoMin = 0)
        val dagar =
            (1..maj.lengthOfMonth())
                .map { maj.atDay(it) }
                .filter { arArbetsdag(it, ARBETSDAGAR_MANDAG_FREDAG) }
                .map { attaTimmar(it) }
        val sammanfattning = periodSammanfattning(dagar, maj.atDay(1), maj.atEndOfMonth(), inst, aftnarLediga = true)
        assertEquals(sammanfattning.arbetadMin - sammanfattning.normaltidMin, sammanfattning.utfallMin)
    }
}
