package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostValideringTest {
    private val idag = LocalDate.of(2026, 10, 16)

    @Test
    fun valideraPostExempel() {
        assertEquals(PostFel.VAD_SAKNAS, valideraPost("", idag, idag, ""))
        assertEquals(
            PostFel.DATUM_I_FRAMTIDEN,
            valideraPost("Målade fasaden", idag.plusDays(1), idag, ""),
        )
        assertEquals(PostFel.BELOPP_OGILTIGT, valideraPost("Målade fasaden", idag, idag, "abc"))
        assertEquals(PostFel.BELOPP_OGILTIGT, valideraPost("Målade fasaden", idag, idag, "-5"))
        assertNull(valideraPost("Målade fasaden", idag, idag, ""))
        assertNull(valideraPost("Målade fasaden", idag, idag, "12500"))
    }

    @Test
    fun rotRutAvdragValidering() {
        assertEquals(PostFel.AVDRAG_OGILTIGT, valideraPost("X", idag, idag, "", "1000"))
        assertEquals(PostFel.AVDRAG_FOR_STORT, valideraPost("X", idag, idag, "5000", "5001"))
        assertNull(valideraPost("X", idag, idag, "10000", "3000"))
    }
}
