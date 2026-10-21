package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class CsvExportGransfallTest {

    @Test
    fun kombineradCitering() {
        val k = komponent(namn = "Fönster; \"special\"\nrad", kalla = "källa")
        val csv = tillCsv(listOf(k), 2025)
        
        // Kontrollera att rubriken finns
        assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        
        // Kontrollera att namnfältet är citerat korrekt med escaped semikolon och radbrytning
        assertTrue(csv.contains("\"Fönster; \"\"special\"\"\nrad\""))
        
        // Kontrollera att utkast-rad finns
        assertTrue(csv.endsWith("UTKAST. Beslutas av förening och revisor.\n"))
    }

    @Test
    fun svenskaTecken() {
        val k = komponent(namn = "Årsredovisning 2025", kalla = "årsredovisning.pdf")
        val csv = tillCsv(listOf(k), 2025)
        
        // Kontrollera att svenska tecken finns kvar oförändrade
        assertTrue(csv.contains("Årsredovisning 2025"))
        assertTrue(csv.contains("årsredovisning.pdf"))
        
        // Kontrollera att rubriken finns
        assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        
        // Kontrollera att utkast-rad finns
        assertTrue(csv.endsWith("UTKAST. Beslutas av förening och revisor.\n"))
    }

    @Test
    fun talkolumner() {
        val k = komponent(namn = "Fönster", kalla = "plan.pdf")
        val csv = tillCsv(listOf(k), 2025)
        
        // Kontrollera att värdena är korrekt formaterade utan tusentalsavgränsare eller håra mellanslag
        assertTrue(csv.contains("1200000;2010;40;0;30000;720000;plan.pdf;HOG"))
        
        // Kontrollera att rubriken finns
        assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        
        // Kontrollera att utkast-rad finns
        assertTrue(csv.endsWith("UTKAST. Beslutas av förening och revisor.\n"))
    }

    @Test
    fun rubrikOchUtkast() {
        val csv = tillCsv(emptyList(), 2025)
        
        // Kontrollera att CSV börjar med exakt samma rubrikrad som i CsvExportTest.tomListaHarRubrikOchUtkastRad
        assertEquals(
            "namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\nUTKAST. Beslutas av förening och revisor.\n",
            csv,
        )
    }

    private fun komponent(namn: String, kalla: String) = Komponent(
        namn = namn,
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = 1_200_000,
        anskaffningsar = 2010,
        nyttjandeperiodAr = 40,
        restvarde = 0,
        kalla = kalla,
        sakerhet = Sakerhet.HOG,
    )
}
