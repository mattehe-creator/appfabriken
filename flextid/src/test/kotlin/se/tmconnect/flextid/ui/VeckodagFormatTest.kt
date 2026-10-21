package se.tmconnect.flextid.ui

import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class VeckodagFormatTest {
    @Test
    fun svenskaOchEngelska() {
        val d = LocalDate.of(2026, 10, 12)
        assertEquals("Måndag 12 oktober", formateraVeckodagOchDatum(d, Locale.forLanguageTag("sv-SE")))
        assertEquals("Monday 12 October", formateraVeckodagOchDatum(d, Locale.UK))
    }
}
