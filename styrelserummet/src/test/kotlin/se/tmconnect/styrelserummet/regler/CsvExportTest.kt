package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class CsvExportTest {
    @Test
    fun citatteckenVidSemikolonOchRadbrytning() {
        val k = Komponent(
            namn = "Rad; med \"citat\"\noch radbrytning",
            grupp = Komponentgrupp.TAK.name,
            anskaffningsvarde = 1000,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 20,
            kalla = "plan",
            sakerhet = Sakerhet.MEDEL.name,
        )
        val csv = tillCsv(listOf(k), 2025)
        assertTrue(csv.contains("Rad; med \"\"citat\"\""))
        assertTrue(csv.contains("radbrytning"))
        assertTrue(csv.contains("UTKAST. Beslutas av förening och revisor."))
    }
}
