package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test

class MaskaPersonuppgifterTest {
    @Test
    fun maskaPersonnummerVarianter() {
        assertEquals("Styrelseledamot [personnummer]", maskaPersonuppgifter("Styrelseledamot 198501011234"))
        assertEquals("Kontakt [personnummer]", maskaPersonuppgifter("Kontakt 650161-1234"))
        assertEquals("Arvtagare [personnummer]", maskaPersonuppgifter("Arvtagare 050101-1234+"))
        assertEquals("769612-3456", maskaPersonuppgifter("769612-3456"))
    }
}
