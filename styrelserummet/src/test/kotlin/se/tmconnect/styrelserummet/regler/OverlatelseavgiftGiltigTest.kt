package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlatelseavgiftGiltigTest {

    private val standarddata: Map<String, StammaRad> = lasStammaData(
        listOf(
            "typ;ar;varde;enhet;kalla",
            "referens_overlatelse;0;25;promille_av_pbb;STAMMA.md",
            "referens_pantsattning;0;10;promille_av_pbb;STAMMA.md",
        ),
    )

    private val pbb = 59200L

    @Test
    fun giltigOverlatelse1480GerNullFelmeddelande() {
        assertTrue(avgiftarGiltig(1480, pbb, Avgiftstyp.OVERLATELSE, standarddata))
        assertNull(avgiftFelmeddelande(1480, pbb, Avgiftstyp.OVERLATELSE, standarddata))
    }

    @Test
    fun ogiltigOverlatelse2000FelmeddelandeNamnerGransOchTyp() {
        assertFalse(avgiftarGiltig(2000, pbb, Avgiftstyp.OVERLATELSE, standarddata))
        val msg = checkNotNull(avgiftFelmeddelande(2000, pbb, Avgiftstyp.OVERLATELSE, standarddata))
        assertTrue(msg.contains("1480"))
        assertTrue(msg.lowercase().contains("överlåtelse"))
    }

    @Test
    fun ogiltigAndrahand6000OverstigerGrans5920() {
        assertFalse(avgiftarGiltig(6000, pbb, Avgiftstyp.ANDRAHAND, standarddata))
    }
}
