package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class FormateringTest {

    @Test
    fun kronor() {
        assertEquals("1\u00A0234\u00A0567 kr", kronor(1_234_567))
        assertEquals("0 kr", kronor(0))
        assertEquals("\u2212500 kr", kronor(-500))
    }
}
