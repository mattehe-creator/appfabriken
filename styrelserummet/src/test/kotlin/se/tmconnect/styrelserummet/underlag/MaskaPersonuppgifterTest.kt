package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test

class MaskaPersonuppgifterTest {
    @Test
    fun maskaTelefonOchEpost() {
        assertEquals("Ring [telefon]", maskaPersonuppgifter("Ring 070 123 45 67"))
        assertEquals("Mail [e-post]", maskaPersonuppgifter("Mail Styrelse@Min-Brf.se"))
        assertEquals("769612-3456", maskaPersonuppgifter("769612-3456"))
    }

    @Test
    fun maskaPersonnummerVarianter() {
        assertEquals("Styrelseledamot [personnummer]", maskaPersonuppgifter("Styrelseledamot 198501011234"))
        assertEquals("Kontakt [personnummer]", maskaPersonuppgifter("Kontakt 650161-1234"))
        assertEquals("Arvtagare [personnummer]", maskaPersonuppgifter("Arvtagare 050101-1234+"))
        assertEquals("769612-3456", maskaPersonuppgifter("769612-3456"))
    }

    @Test
    fun maskaInteFalskaTrffar() {
        assertEquals(
            "Org 769612-3456, bankgiro 123-4567",
            maskaPersonuppgifter("Org 769612-3456, bankgiro 123-4567"),
        )
        assertEquals(
            "Värde 1 234 567 kr, år 2024, per 2025-12-31, BRF Solen 1",
            maskaPersonuppgifter("Värde 1 234 567 kr, år 2024, per 2025-12-31, BRF Solen 1"),
        )
        assertEquals(
            "Org 769612-3456, kontakt [personnummer], [telefon]",
            maskaPersonuppgifter("Org 769612-3456, kontakt 850709-1234, 070-123 45 67"),
        )
    }
}
