package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class SaldoKurvaTest {
    private val arbetstid =
        Arbetstid(
            normaltidMinPerDag = 480,
            arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG,
        )

    private val dagar =
        listOf(
            Dag(LocalDate.of(2026, 10, 16), LocalTime.of(8, 0), LocalTime.of(17, 30), 30),
            Dag(LocalDate.of(2026, 10, 19), LocalTime.of(8, 0), LocalTime.of(17, 0), 30),
            Dag(LocalDate.of(2026, 10, 21), LocalTime.of(8, 0), LocalTime.of(16, 0), 30),
        )

    @Test
    fun exempelFran19Till21() {
        val resultat =
            saldoKurva(
                dagar,
                ingangssaldoMin = 100,
                arbetstid = arbetstid,
                fran = LocalDate.of(2026, 10, 19),
                till = LocalDate.of(2026, 10, 21),
            )
        assertEquals(
            listOf(
                SaldoPunkt(LocalDate.of(2026, 10, 19), 190),
                SaldoPunkt(LocalDate.of(2026, 10, 20), 190),
                SaldoPunkt(LocalDate.of(2026, 10, 21), 160),
            ),
            resultat,
        )
    }

    @Test
    fun exempelEnDagEfterPerioden() {
        val resultat =
            saldoKurva(
                dagar,
                ingangssaldoMin = 100,
                arbetstid = arbetstid,
                fran = LocalDate.of(2026, 10, 22),
                till = LocalDate.of(2026, 10, 22),
            )
        assertEquals(listOf(SaldoPunkt(LocalDate.of(2026, 10, 22), 160)), resultat)
    }

    @Test
    fun exempelFranEfterTill() {
        val resultat =
            saldoKurva(
                dagar,
                ingangssaldoMin = 100,
                arbetstid = arbetstid,
                fran = LocalDate.of(2026, 10, 22),
                till = LocalDate.of(2026, 10, 21),
            )
        assertEquals(emptyList<SaldoPunkt>(), resultat)
    }
}
