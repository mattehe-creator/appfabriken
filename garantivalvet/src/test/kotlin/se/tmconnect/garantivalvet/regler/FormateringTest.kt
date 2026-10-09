package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.util.Locale
import org.junit.Test
import org.junit.Assert.assertEquals

class FormateringTest {
    
    @Test
    fun testFormateraPrisKr() {
        assertEquals("", formateraPrisKr(null))
        assertEquals("1 299,50 kr", formateraPrisKr(129950L))
        assertEquals("1,00 kr", formateraPrisKr(100L))
        assertEquals("1 299,50 kr", formateraPrisKr(129950L, Locale.forLanguageTag("sv-SE")))
        assertEquals("1,299.50 kr", formateraPrisKr(129950L, Locale.US))
    }
    
    @Test
    fun testFormateraDatum() {
        val datum = LocalDate.of(2026, 3, 15)
        assertEquals("15 mars 2026", formateraDatum(datum, Locale.forLanguageTag("sv-SE")))
    }
}
