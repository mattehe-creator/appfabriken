package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelDataAtgarderGrupp1Test {

    @Test
    fun grupp1TackerTakFasadFonsterStammar() {
        Assert.assertEquals(12, EXEMPEL_ATGARDER.size)

        val yttertak = EXEMPEL_ATGARDER.first { it.namn == "Yttertak" }
        Assert.assertEquals(2032, yttertak.ar)
        Assert.assertEquals(5_000_000L, yttertak.kostnadKronor)

        val fasadputs = EXEMPEL_ATGARDER.first { it.namn == "Fasadputs" }
        Assert.assertEquals(2036, fasadputs.ar)
        Assert.assertEquals(4_000_000L, fasadputs.kostnadKronor)

        val fonster = EXEMPEL_ATGARDER.first { it.namn == "Fönster" }
        Assert.assertEquals(2028, fonster.ar)
        Assert.assertEquals(3_500_000L, fonster.kostnadKronor)

        val stammar = EXEMPEL_ATGARDER.first { it.namn == "Stammar vatten och avlopp" }
        Assert.assertEquals(2030, stammar.ar)
        Assert.assertEquals(3_000_000L, stammar.kostnadKronor)

        EXEMPEL_ATGARDER.forEach { atgard ->
            Assert.assertNotNull(atgard.kostnadKronor)
        }
    }
}
