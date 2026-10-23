package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.regler.stamAv

class ExempelTexterStamAvTest {
    @Test
    fun planSummaStammerMedByggnadEnligtStamAv() {
        val komponenter = exempelKomponenterFranPlan()
        assertEquals(13, komponenter.size)
        assertEquals(27_000_000L, komponenter.sumOf { it.anskaffningsvarde })
        
        val av = stamAv(komponenter, BYGGNADENS_VARDE)
        assertTrue(av.stammer)
        assertEquals(summaAtgarderMedKostnad(), av.summa)
    }
}
