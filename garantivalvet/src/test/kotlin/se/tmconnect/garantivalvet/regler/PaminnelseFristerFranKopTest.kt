package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop

class PaminnelseFristerFranKopTest {
    private val idag = LocalDate.of(2026, 10, 13)

    private fun kop(
        reklamerad: LocalDate? = null,
        utfall: String? = null,
    ) = Kop(
        id = 7,
        vad = "TV",
        kopdatum = LocalDate.of(2025, 1, 1),
        garantiManader = 24,
        reklamerad = reklamerad,
        reklamationUtfall = utfall,
    )

    @Test
    fun svarFristFjortonDagarEfterReklamationBaraUtanUtfall() {
        val rekl = idag.minusDays(5)
        val medSvar = fristerFranKop(kop(reklamerad = rekl)).single { it.typ == FristTyp.SVAR }
        assertEquals(rekl.plusDays(SVAR_FORVANTAD_DAGAR.toLong()), medSvar.slutdatum)
        assertNull(fristerFranKop(kop(reklamerad = rekl, utfall = "AVSLAG")).find { it.typ == FristTyp.SVAR })
        assertNull(fristerFranKop(kop(reklamerad = null)).find { it.typ == FristTyp.SVAR })
    }

    @Test
    fun arnForsvinnerVidLostEllerAnmalt() {
        val oppet = kop(reklamerad = idag, utfall = null)
        assertTrue(fristerFranKop(oppet).any { it.typ == FristTyp.ARN })
        assertNull(fristerFranKop(kop(reklamerad = idag, utfall = "LOST")).find { it.typ == FristTyp.ARN })
        assertNull(fristerFranKop(kop(reklamerad = idag, utfall = "ARN_ANMALD")).find { it.typ == FristTyp.ARN })
        assertTrue(fristerFranKop(kop(reklamerad = idag, utfall = "AVSLAG")).any { it.typ == FristTyp.ARN })
    }
}
