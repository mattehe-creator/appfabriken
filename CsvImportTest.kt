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
    fun testWithBom() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = "\uFEFF$rubrik\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("Tak plåt", komponent.namn)
    }

    @Test
    fun testUnknownGroup() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;FASAD;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals(Komponentgrupp.FASAD, komponent.grupp)
    }

    @Test
    fun testUnknownGroupDefault() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Garage;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals(Komponentgrupp.OVRIGT, komponent.grupp)
    }

    @Test
    fun testUnknownSecurity() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;okänd\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals(Sakerhet.MEDEL, komponent.sakerhet)
    }

    @Test
    fun testSkipShortRow() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(0, result.size)
    }

    @Test
    fun testSkipInvalidValue() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\nTak plåt;Tak;x;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(0, result.size)
    }

    @Test
    fun testQuotedCellWithSemicolon() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\"\"\"Fönster; trapphus\"\";Fönster och dörrar;10;2000;30;0;k;LAG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("Fönster; trapphus", komponent.namn)
        assertEquals(Komponentgrupp.FONSTER_DORRAR, komponent.grupp)
    }

    @Test
    fun testFormulaCell() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\"=SUMMA\";Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."
        val result = franExportCsv(text)
        assertEquals(1, result.size)
        val komponent = result[0]
        assertEquals("=SUMMA", komponent.namn)
    }

    @Test
    fun testOnlyHeader() {
        val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
        val text = rubrik + "\n"
        val result = franExportCsv(text)
        assertEquals(0, result.size)
    }

    @Test
    fun testEmptyText() {
        val result = franExportCsv("")
        assertEquals(0, result.size)
    }

    @Test
    fun testRoundTrip() {
        val komponentDto = KomponentDto(
            namn = "Hiss",
            grupp = "HISS",
            anskaffningsvarde = 500_000,
            anskaffningsar = 2015,
            nyttjandeperiodAr = 25,
            restvarde = 0,
            kalla = "UH-plan",
            sakerhet = "HOG"
        )
        val csv = tillCsv(listOf(komponentDto), 2026)
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
