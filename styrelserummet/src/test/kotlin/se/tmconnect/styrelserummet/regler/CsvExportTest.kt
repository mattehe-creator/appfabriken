package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

class CsvExportTest {

    @Test
    fun rubrikOchHeltalUtanTusentalsavgransare() {
        val k = komponent(namn = "Fönster", kalla = "plan.pdf")
        val csv = tillCsv(listOf(k), 2025)

        assertTrue(csv.startsWith("namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n"))
        assertTrue(csv.contains("Fönster;Stomme;1200000;2010;40;0;30000;720000;plan.pdf;HOG\n"))
        assertTrue(csv.endsWith("$UTKAST_RAD\n"))
    }

    @Test
    fun citerarSemikolonINamn() {
        val k = komponent(namn = "Test; AB", kalla = "källa")
        val csv = tillCsv(listOf(k), 2025)
        assertTrue(csv.contains("\"Test; AB\""))
    }

    @Test
    fun citerarOchEscaparCitatteckenIKalla() {
        val k = komponent(namn = "Komponent", kalla = "text med \"citat\"")
        val csv = tillCsv(listOf(k), 2025)
        assertTrue(csv.contains("\"text med \"\"citat\"\"\""))
    }

    @Test
    fun citerarRadbrytningINamn() {
        val k = komponent(namn = "Rad\nbryt", kalla = "källa")
        val csv = tillCsv(listOf(k), 2025)
        assertTrue(csv.contains("\"Rad\nbryt\""))
    }

    @Test
    fun tomListaHarRubrikOchUtkastRad() {
        val csv = tillCsv(emptyList(), 2025)
        assertEquals(
            "namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n$UTKAST_RAD\n",
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
