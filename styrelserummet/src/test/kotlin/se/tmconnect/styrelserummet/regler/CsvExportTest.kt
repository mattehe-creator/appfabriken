package se.tmconnect.styrelserummet.regler

import org.junit.Test
import org.junit.Assert.assertEquals

class CsvExportTest {
    @Test
    fun tomListaHarRubrikOchUtkastRad() {
        val csv = tillCsv(emptyList(), 2025)
        assertEquals(
            "namn;grupp;anskaffningsvärde;anskaffningsår;nyttjandeperiod;restvärde;avskrivning året;bokfört värde;källa;säkerhet\n$UTKAST_RAD\n",
            csv,
        )
    }
}
