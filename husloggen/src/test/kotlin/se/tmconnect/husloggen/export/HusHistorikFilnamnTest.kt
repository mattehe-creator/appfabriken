package se.tmconnect.husloggen.export

import org.junit.Test
import java.time.LocalDate
import org.junit.Assert.assertEquals

class HusHistorikFilnamnTest {

    @Test
    fun testHusHistorikPdfFilnamn() {
        val datum = LocalDate.of(2026, 3, 5)
        val forväntat = "husets-historik-2026-03-05.pdf"
        val faktiskt = husHistorikPdfFilnamn(datum)
        assertEquals(forväntat, faktiskt)
    }

    @Test
    fun testHusHistorikCsvFilnamn() {
        val datum = LocalDate.of(2026, 3, 5)
        val forväntat = "husets-historik-2026-03-05.csv"
        val faktiskt = husHistorikCsvFilnamn(datum)
        assertEquals(forväntat, faktiskt)
    }

    @Test
    fun testHusHistorikPdfFilnamnMedAnnatDatum() {
        val datum = LocalDate.of(2026, 12, 31)
        val forväntat = "husets-historik-2026-12-31.pdf"
        val faktiskt = husHistorikPdfFilnamn(datum)
        assertEquals(forväntat, faktiskt)
    }

    @Test
    fun testHusHistorikCsvFilnamnMedAnnatDatum() {
        val datum = LocalDate.of(2026, 12, 31)
        val forväntat = "husets-historik-2026-12-31.csv"
        val faktiskt = husHistorikCsvFilnamn(datum)
        assertEquals(forväntat, faktiskt)
    }
}
