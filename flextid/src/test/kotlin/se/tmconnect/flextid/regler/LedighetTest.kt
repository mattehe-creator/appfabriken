package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.paminnelse.skaPaminnelseStamplaIn

class LedighetTest {
    private val mask = ARBETSDAGAR_MANDAG_FREDAG

    @Test
    fun vanligVardagArArbetsdag() {
        assertTrue(arArbetsdagMedHelger(LocalDate.of(2026, 10, 21), mask, aftnarLediga = true))
    }

    @Test
    fun rodVardagArInteArbetsdag() {
        assertFalse(arArbetsdagMedHelger(LocalDate.of(2026, 4, 3), mask, aftnarLediga = false))
        assertFalse(arArbetsdagMedHelger(LocalDate.of(2026, 5, 14), mask, aftnarLediga = false))
    }

    @Test
    fun aftonStyrsAvInstallning() {
        val midsommarafton = LocalDate.of(2026, 6, 19)
        assertFalse(arArbetsdagMedHelger(midsommarafton, mask, aftnarLediga = true))
        assertTrue(arArbetsdagMedHelger(midsommarafton, mask, aftnarLediga = false))
    }

    @Test
    fun ingenPaminnelseOmStamplaInPaRodDag() {
        val installning = Installning(arbetsdagarMask = mask)
        val langfredag = LocalDate.of(2026, 4, 3)
        assertFalse(skaPaminnelseStamplaIn(null, installning, langfredag, LocalTime.of(9, 0), 8 * 60))
        val vanligFredag = LocalDate.of(2026, 4, 10)
        assertTrue(skaPaminnelseStamplaIn(null, installning, vanligFredag, LocalTime.of(9, 0), 8 * 60))
    }

    @Test
    fun paminnelsePaAftonNarAftnarInteArLediga() {
        val installning = Installning(arbetsdagarMask = mask)
        val julafton = LocalDate.of(2026, 12, 24)
        assertFalse(skaPaminnelseStamplaIn(null, installning, julafton, LocalTime.of(9, 0), 8 * 60))
        assertTrue(
            skaPaminnelseStamplaIn(null, installning, julafton, LocalTime.of(9, 0), 8 * 60, aftnarLediga = false),
        )
    }
}
