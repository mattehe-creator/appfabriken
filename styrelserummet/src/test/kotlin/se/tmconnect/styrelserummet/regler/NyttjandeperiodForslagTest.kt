package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class NyttjandeperiodForslagTest {

    @Test
    fun forslagForGrupp() {
        val tabell = lasNyttjandeperioderFranResurs()

        assertEquals(50, forslagFor(Komponentgrupp.TAK, tabell))
        assertEquals(50, forslagFor(Komponentgrupp.STAMMAR, tabell))
        assertEquals(null, forslagFor(Komponentgrupp.MARK_OCH_GARAGE, tabell))

        val baraTak = listOf(
            NyttjandeperiodRad(Komponentgrupp.TAK, 30..60, 50, 4),
        )
        assertEquals(null, forslagFor(Komponentgrupp.STAMMAR, baraTak))
    }
}
