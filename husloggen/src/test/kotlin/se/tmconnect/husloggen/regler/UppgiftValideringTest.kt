package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UppgiftValideringTest {
    @Test
    fun valideraUppgift() {
        assertEquals(UppgiftFel.NAMN_SAKNAS, valideraUppgift("", 12))
        assertEquals(UppgiftFel.NAMN_SAKNAS, valideraUppgift("  ", null))
        assertEquals(UppgiftFel.INTERVALL_SAKNAS, valideraUppgift("Hängrännor", null))
        assertEquals(UppgiftFel.INTERVALL_UTANFOR, valideraUppgift("Hängrännor", 0))
        assertEquals(UppgiftFel.INTERVALL_UTANFOR, valideraUppgift("Hängrännor", 121))
        assertNull(valideraUppgift("Hängrännor", 120))
    }
}
