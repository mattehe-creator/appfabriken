package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class FordelaRestTest {
    @Test
    fun ingenRest_treGrupper() {
        assertEquals(
            mapOf(
                Komponentgrupp.STOMME to 34L,
                Komponentgrupp.FASAD to 33L,
                Komponentgrupp.TAK to 33L,
            ),
            fordela(
                100,
                listOf(
                    Komponentgrupp.STOMME to 34,
                    Komponentgrupp.FASAD to 33,
                    Komponentgrupp.TAK to 33,
                ),
            ),
        )
    }

    @Test
    fun halvaHalva_tusen() {
        assertEquals(
            mapOf(Komponentgrupp.STOMME to 500L, Komponentgrupp.FASAD to 500L),
            fordela(1000, listOf(Komponentgrupp.STOMME to 50, Komponentgrupp.FASAD to 50)),
        )
    }

    @Test
    fun restEnTillForsta_vidFemtioFifty() {
        assertEquals(
            mapOf(Komponentgrupp.STOMME to 51L, Komponentgrupp.FASAD to 50L),
            fordela(101, listOf(Komponentgrupp.STOMME to 50, Komponentgrupp.FASAD to 50)),
        )
    }

    @Test
    fun restEnTillForsta_narAllaLikadanaBelopp() {
        assertEquals(
            mapOf(
                Komponentgrupp.STOMME to 4L,
                Komponentgrupp.FASAD to 3L,
                Komponentgrupp.TAK to 3L,
            ),
            fordela(
                10,
                listOf(
                    Komponentgrupp.STOMME to 33,
                    Komponentgrupp.FASAD to 33,
                    Komponentgrupp.TAK to 34,
                ),
            ),
        )
    }

    @Test
    fun restTillStorsta_stommeForst() {
        assertEquals(
            mapOf(Komponentgrupp.STOMME to 630L, Komponentgrupp.FASAD to 369L),
            fordela(999, listOf(Komponentgrupp.STOMME to 63, Komponentgrupp.FASAD to 37)),
        )
    }

    @Test
    fun restTillStorsta_stommeSist() {
        assertEquals(
            mapOf(Komponentgrupp.FASAD to 369L, Komponentgrupp.STOMME to 630L),
            fordela(999, listOf(Komponentgrupp.FASAD to 37, Komponentgrupp.STOMME to 63)),
        )
    }

    @Test
    fun nollProcentGrupp() {
        assertEquals(
            mapOf(Komponentgrupp.STOMME to 7L, Komponentgrupp.MARK_OCH_GARAGE to 0L),
            fordela(
                7,
                listOf(Komponentgrupp.STOMME to 100, Komponentgrupp.MARK_OCH_GARAGE to 0),
            ),
        )
    }

    @Test
    fun nollTotal() {
        assertEquals(
            mapOf(Komponentgrupp.STOMME to 0L, Komponentgrupp.TAK to 0L),
            fordela(0, listOf(Komponentgrupp.STOMME to 60, Komponentgrupp.TAK to 40)),
        )
    }

    @Test
    fun andelarSummerarInteTill100() {
        assertThrows(IllegalArgumentException::class.java) {
            fordela(100, listOf(Komponentgrupp.STOMME to 50, Komponentgrupp.TAK to 40))
        }
    }
}
