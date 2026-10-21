package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.YearMonth
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Installning

class ManadsRapportNormaltidTest {
    private val texter =
        ManadsRapportTexter(
            manadArRubrik = "",
            ingaRegistreradeDagar = "",
            summaArbetadEtikett = "",
            summaUtfallEtikett = "",
            saldoManadsslutEtikett = "",
            kolumnRubriker = emptyList(),
            typEtiketter = DagTyp.entries.associateWith { it.name },
            veckodagEtiketter = DayOfWeek.entries.associateWith { it.name },
            saknasTecken = "-",
        )
    private val inst = Installning(normaltidMinPerDag = 480)

    @Test
    fun april2026() {
        val r = byggManadsRapport(emptyList(), inst, YearMonth.of(2026, 4), texter, Locale.ROOT)
        assertEquals(20, r.arbetsdagarManad)
        assertEquals(formateraMinuterSomTid(9600, Locale.ROOT), r.normaltidManad)
    }

    @Test
    fun decemberMedOchUtanAftnar() {
        assertEquals(20, byggManadsRapport(emptyList(), inst, YearMonth.of(2026, 12), texter, Locale.ROOT).arbetsdagarManad)
        assertEquals(
            22,
            byggManadsRapport(emptyList(), inst, YearMonth.of(2026, 12), texter, Locale.ROOT, aftnarLediga = false)
                .arbetsdagarManad,
        )
    }
}
