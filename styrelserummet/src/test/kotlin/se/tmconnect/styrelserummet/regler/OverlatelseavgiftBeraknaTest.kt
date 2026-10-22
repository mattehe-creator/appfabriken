package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class OverlatelseavgiftBeraknaTest {

    private val referensData: Map<String, StammaRad> = lasStammaData(
        listOf(
            "typ;ar;varde;enhet;kalla",
            "referens_overlatelse;0;25;promille_av_pbb;STAMMA.md",
            "referens_pantsattning;0;10;promille_av_pbb;STAMMA.md",
        ),
    )

    @Test
    fun avgiftFranPromille59200Promille25() {
        assertEquals(1480L, avgiftFranPromille(59200, 25))
    }

    @Test
    fun avgiftFranPromille58800Promille10() {
        assertEquals(588L, avgiftFranPromille(58800, 10))
    }

    @Test
    fun hogstaAndrahandsavgift59200() {
        assertEquals(5920L, hogstaAndrahandsavgift(59200))
    }

    @Test
    fun avgiftFranPromille59201Promille25AvrundasNedat() {
        assertEquals(1480L, avgiftFranPromille(59201, 25))
    }

    @Test
    fun hogstaOverlatelseavgiftFranReferensData() {
        assertEquals(1480L, hogstaOverlatelseavgift(59200, referensData))
    }

    @Test
    fun hogstaPantsattningsavgiftFranReferensData() {
        assertEquals(588L, hogstaPantsattningsavgift(58800, referensData))
    }
}
