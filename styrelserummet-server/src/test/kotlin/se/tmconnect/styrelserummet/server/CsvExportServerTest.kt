package se.tmconnect.styrelserummet.server

import org.junit.Assert
import org.junit.Test

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
        Assert.assertTrue(rader.size >= 2)
        
        // Kontrollera att bokfört värde är 120000 för årsslut 2025
        Assert.assertTrue(rader[1].contains(";120000;"))
        
        // Testa årsslut 2019 (bokfört värde ska vara 300000)
        val csv2019 = tillCsv(listOf(komponent), 2019)
        val rader2019 = csv2019.split("\n")
        Assert.assertTrue(rader2019[1].contains(";300000;"))
        
        // Testa årsslut 2035 (bokfört värde ska vara 0)
        val csv2035 = tillCsv(listOf(komponent), 2035)
        val rader2035 = csv2035.split("\n")
        Assert.assertTrue(rader2035[1].contains(";0;"))
    }
}
