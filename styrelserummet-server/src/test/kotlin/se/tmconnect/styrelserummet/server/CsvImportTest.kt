package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertEquals
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class CsvImportTest {

    @Test
    fun testBasicImport() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("Tak plåt", komponent.namn)
        assertEquals(Komponentgrupp.TAK, komponent.grupp)
        assertEquals(1_200_000, komponent.anskaffningsvarde)
        assertEquals(2010, komponent.anskaffningsar)
        assertEquals(40, komponent.nyttjandeperiodAr)
        assertEquals(0, komponent.restvarde)
        assertEquals("Underhållsplan s. 4", komponent.kalla)
        assertEquals(Sakerhet.HOG, komponent.sakerhet)
    }

    @Test
    fun testImportWithBom() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = "\uFEFF$rubrik\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("Tak plåt", komponent.namn)
    }

    @Test
    fun testImportUnknownGroup() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Garage;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals(Komponentgrupp.OVRIGT, komponent.grupp)
    }

    @Test
    fun testImportUnknownSecurity() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;okänd\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals(Sakerhet.MEDEL, komponent.sakerhet)
    }

    @Test
    fun testImportShortRow() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;1 200 000;2010;40\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(0, result.size)
    }

    @Test
    fun testImportInvalidValue() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;x;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(0, result.size)
    }

    @Test
    fun testImportQuotedCell() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\n\"Fönster; trapphus\";Fönster och dörrar;10;2000;30;0;k;LAG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("Fönster; trapphus", komponent.namn)
        assertEquals(Komponentgrupp.FONSTER_DORRAR, komponent.grupp)
    }

    @Test
    fun testImportCellWithEquals() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\n'=SUMMA;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("=SUMMA", komponent.namn)
    }

    @Test
    fun testImportOnlyHeader() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(0, result.size)
    }

    @Test
    fun testImportEmptyString() {
        val result = franExportCsv("")
        assertEquals(0, result.size)
    }

    @Test
    fun testRoundTrip() {
        val dto = KomponentDto("Hiss", "HISS", 500_000, 2015, 25, 0, "UH-plan", "HOG")
        val csv = tillCsv(listOf(dto), 2026)
        val result = franExportCsv(csv)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("Hiss", komponent.namn)
        assertEquals(Komponentgrupp.HISS, komponent.grupp)
        assertEquals(500_000, komponent.anskaffningsvarde)
        assertEquals(2015, komponent.anskaffningsar)
        assertEquals(25, komponent.nyttjandeperiodAr)
        assertEquals("UH-plan", komponent.kalla)
        assertEquals(Sakerhet.HOG, komponent.sakerhet)
    }
}
