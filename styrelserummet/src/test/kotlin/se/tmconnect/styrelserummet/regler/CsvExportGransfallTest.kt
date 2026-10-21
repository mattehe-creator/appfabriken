package se.tmconnect.styrelserummet.regler

import org.junit.Assert
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
        Assert.assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        
        // Kontrollera att namnfältet är citerat korrekt med escaped semikolon och radbrytning
        Assert.assertTrue(csv.contains("\"Fönster; \"\"special\"\"\nrad\""))
        
        // Kontrollera att utkast-rad finns
        Assert.assertTrue(csv.endsWith("UTKAST. Beslutas av förening och revisor.\n"))
    }

    @Test
    fun svenskaTecken() {
        val k = komponent(namn = "Årsredovisning 2025", kalla = "årsredovisning.pdf")
        val csv = tillCsv(listOf(k), 2025)
        
        // Kontrollera att svenska tecken finns kvar oförändrade
        Assert.assertTrue(csv.contains("Årsredovisning 2025"))
        Assert.assertTrue(csv.contains("årsredovisning.pdf"))
        
        // Kontrollera att rubriken finns
        Assert.assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        
        // Kontrollera att utkast-rad finns
        Assert.assertTrue(csv.endsWith("UTKAST. Beslutas av förening och revisor.\n"))
    }

    @Test
    fun talkolumner() {
        val k = komponent(namn = "Fönster", kalla = "plan.pdf")
        val csv = tillCsv(listOf(k), 2025)
        
        // Kontrollera att värdena är korrekt formaterade utan tusentalsavgränsare eller hårt mellanslag
        Assert.assertTrue(csv.contains("1200000;2010;40;0;30000;720000"))
        
        // Kontrollera att inga håra mellanslag (U+00A0) eller tusentalsavgränsare finns
        Assert.assertFalse(csv.contains("1\u00A0200\u00A0000")) // hårt mellanslag
        Assert.assertFalse(csv.contains("1 200 000")) // vanligt mellanslag
        
        // Kontrollera att rubriken finns
        Assert.assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        
        // Kontrollera att utkast-rad finns
        Assert.assertTrue(csv.endsWith("UTKAST. Beslutas av förening och revisor.\n"))
    }

    @Test
    fun rubrikOchUtkast() {
        val csv = tillCsv(emptyList(), 2025)
        
        // Kontrollera att CSV börjar med exakt samma rubrikrad som i CsvExportTest.tomListaHarRubrikOchUtkastRad
        val forvantadRubrik = "namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"
        Assert.assertTrue("CSV börjar inte med förväntad rubrik. Fick: ${csv.take(100)}", csv.startsWith(forvantadRubrik))
        
        // Kontrollera att det endast finns två rader (rubrik + utkast-rad)
        val rader = csv.split("\n")
        Assert.assertEquals(2, rader.size)
        
        // Kontrollera att sista raden är utkast-rad
        Assert.assertEquals("UTKAST. Beslutas av förening och revisor.", rader[1])
    }

    @Test
    fun tomLista() {
        val csv = tillCsv(emptyList(), 2025)
        
        // Kontrollera att resultatet är exakt som i CsvExportTest.tomListaHarRubrikOchUtkastRad
        val forvantad = "namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\nUTKAST. Beslutas av förening och revisor.\n"
        Assert.assertEquals(forvantad, csv)
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
