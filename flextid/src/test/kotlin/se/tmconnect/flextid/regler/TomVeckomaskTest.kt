package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.export.SakerhetskopiaInstallning
import se.tmconnect.flextid.export.sakerhetskopiaInstallningTillInstallning

/** Utan valda veckodagar fanns ingen arbetsdag att hitta, och flera loopar tog aldrig slut. */
class TomVeckomaskTest {
    private val lordag = LocalDate.of(2026, 10, 24)

    @Test(timeout = 2_000)
    fun nastaArbetsdagUtanVeckodagar() {
        assertEquals(lordag.plusDays(1), nastaArbetsdagEfter(lordag, 0))
    }

    @Test(timeout = 2_000)
    fun tidigasteLedigDagUtanVeckodagar() {
        assertNull(tidigasteLedigDag(lordag, 1, 0, helgdagar = true))
    }

    @Test(timeout = 2_000)
    fun paminnelseUtanVeckodagar() {
        val tid = beraknaNastaStamplaInPaminnelse(lordag, LocalTime.of(9, 0), null, 0, 8 * 60)
        assertEquals(lordag, tid.toLocalDate())
    }

    @Test
    fun harArbetsdagar() {
        assertFalse(harArbetsdagar(0))
        assertFalse(harArbetsdagar(0b1000_0000)) // bara bitar utanför veckan
        assertTrue(harArbetsdagar(ARBETSDAGAR_MANDAG_FREDAG))
    }

    @Test
    fun tidigasteLedigDagSomForut() {
        // Fredag 2026-10-23: nästa vardag är måndag 26 oktober.
        assertEquals(LocalDate.of(2026, 10, 26), tidigasteLedigDag(LocalDate.of(2026, 10, 23), 1, ARBETSDAGAR_MANDAG_FREDAG, true))
        // Två dagar i följd från tisdag 22 december: 22 och 23 december.
        assertEquals(LocalDate.of(2026, 12, 22), tidigasteLedigDag(LocalDate.of(2026, 12, 21), 2, ARBETSDAGAR_MANDAG_FREDAG, true))
    }

    @Test
    fun sakerhetskopiaUtanVeckodagarFarMandagFredag() {
        val inst =
            sakerhetskopiaInstallningTillInstallning(
                SakerhetskopiaInstallning(normaltidMinPerDag = 480, standardRastMin = 30, ingangssaldoMin = 0, arbetsdagarMask = 0),
            )
        assertEquals(ARBETSDAGAR_MANDAG_FREDAG, inst.arbetsdagarMask)
    }
}
