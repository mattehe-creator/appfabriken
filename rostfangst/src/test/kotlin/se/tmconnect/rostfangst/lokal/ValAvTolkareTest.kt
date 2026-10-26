package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class ValAvTolkareTest {

    @Test
    fun testValjTolkning() {
        // ANTHROPIC
        assertEquals(Val.AnvandAnthropic, valjTolkning(TolkningsLage.ANTHROPIC, null))
        assertEquals(Val.AnvandAnthropic, valjTolkning(TolkningsLage.ANTHROPIC, LokalFel.NATFEL))
        assertEquals(Val.AnvandAnthropic, valjTolkning(TolkningsLage.ANTHROPIC, LokalFel.TIMEOUT))
        assertEquals(Val.AnvandAnthropic, valjTolkning(TolkningsLage.ANTHROPIC, LokalFel.OGILTIGT_SVAR))

        // MIN_DATOR
        assertEquals(Val.AnvandDatorn, valjTolkning(TolkningsLage.MIN_DATOR, null))
        assertEquals(Val.Fel(LokalFel.NATFEL), valjTolkning(TolkningsLage.MIN_DATOR, LokalFel.NATFEL))
        assertEquals(Val.Fel(LokalFel.TIMEOUT), valjTolkning(TolkningsLage.MIN_DATOR, LokalFel.TIMEOUT))
        assertEquals(Val.Fel(LokalFel.OGILTIGT_SVAR), valjTolkning(TolkningsLage.MIN_DATOR, LokalFel.OGILTIGT_SVAR))

        // MIN_DATOR_ANNARS_ANTHROPIC
        assertEquals(Val.AnvandDatorn, valjTolkning(TolkningsLage.MIN_DATOR_ANNARS_ANTHROPIC, null))
        assertEquals(Val.AnvandAnthropic, valjTolkning(TolkningsLage.MIN_DATOR_ANNARS_ANTHROPIC, LokalFel.TIMEOUT))
        assertEquals(Val.AnvandAnthropic, valjTolkning(TolkningsLage.MIN_DATOR_ANNARS_ANTHROPIC, LokalFel.NATFEL))
        assertEquals(Val.Fel(LokalFel.OGILTIGT_SVAR), valjTolkning(TolkningsLage.MIN_DATOR_ANNARS_ANTHROPIC, LokalFel.OGILTIGT_SVAR))
    }
}
