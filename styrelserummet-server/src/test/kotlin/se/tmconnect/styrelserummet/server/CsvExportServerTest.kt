package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

class CsvExportServerTest {

    @Test
    fun testTillCsv() {
        val komponent = KomponentDto(
            namn = "Testkomponent",
            grupp = "STOMME",
            anskaffningsvarde = 300000,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 10,
            restvarde = 0,
            kalla = "Testkälla",
            sakerhet = "MEDEL"
        )
        
        val csv = tillCsv(listOf(komponent), 2025)
        val rader = csv.split("\n")
        assertTrue(rader.size >= 2)

        assertTrue(rader[1].contains(";120000;"))
        assertEquals(UTKAST_RAD, rader.last())

        val csv2019 = tillCsv(listOf(komponent), 2019)
        assertTrue(csv2019.split("\n")[1].contains(";300000;"))

        val csv2035 = tillCsv(listOf(komponent), 2035)
        assertTrue(csv2035.split("\n")[1].contains(";0;"))
    }
}
