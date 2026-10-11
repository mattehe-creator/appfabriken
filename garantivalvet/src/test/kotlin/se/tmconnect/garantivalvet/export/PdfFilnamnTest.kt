package se.tmconnect.garantivalvet.export

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PdfFilnamnTest {
    @Test
    fun standardFilnamn() {
        assertEquals(
            "reklamation-Laptop-2024-03-15.pdf",
            reklamationsPdfFilnamn("Laptop", LocalDate.of(2024, 3, 15)),
        )
    }

    @Test
    fun otillatnaTeckenErsatts() {
        assertEquals(
            "reklamation-TV-55-2024-01-01.pdf",
            reklamationsPdfFilnamn("TV/55\"", LocalDate.of(2024, 1, 1)),
        )
    }

    @Test
    fun tomtVadBlirKop() {
        assertEquals(
            "reklamation-kop-2025-06-01.pdf",
            reklamationsPdfFilnamn("  ", LocalDate.of(2025, 6, 1)),
        )
    }
}
