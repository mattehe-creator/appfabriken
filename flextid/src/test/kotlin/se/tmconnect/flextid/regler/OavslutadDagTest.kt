package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

class OavslutadDagTest {
    private val onsdag = LocalDate.of(2026, 10, 21)

    @Test
    fun instampladUtanUtstamplingRaknasInte() {
        val dag = Dag(datum = onsdag, start = LocalTime.of(8, 0), rastMin = 30)
        assertEquals(0, dagensUtfall(dag, 480, ARBETSDAGAR_MANDAG_FREDAG))
    }

    @Test
    fun saldotRorSigInteUnderDagen() {
        val inst = Installning(normaltidMinPerDag = 480, ingangssaldoMin = 125)
        val dagar = listOf(Dag(datum = onsdag, start = LocalTime.of(8, 0), rastMin = 30))
        assertEquals(125, beraknaSaldo(dagar, inst))
    }

    @Test
    fun avslutadDagRaknasSomForut() {
        val dag = Dag(datum = onsdag, start = LocalTime.of(8, 0), slut = LocalTime.of(17, 0), rastMin = 30)
        assertEquals(30, dagensUtfall(dag, 480, ARBETSDAGAR_MANDAG_FREDAG))
    }
}
