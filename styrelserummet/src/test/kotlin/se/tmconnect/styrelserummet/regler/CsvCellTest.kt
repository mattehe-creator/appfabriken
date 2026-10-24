package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvCellTest {
    @Test
    fun formlerSkyddas() {
        assertEquals("'=HYPERLINK(\"x\")".let { "\"" + it.replace("\"", "\"\"") + "\"" }, csvCell("=HYPERLINK(\"x\")"))
        assertEquals("'+46 8 123", csvCell("+46 8 123"))
        assertEquals("'@SUM(A1)", csvCell("@SUM(A1)"))
        assertEquals("'-tak", csvCell("-tak"))
    }

    @Test
    fun talLamnasOrorda() {
        assertEquals("-1200", csvCell("-1200"))
        assertEquals("1200", csvCell("1200"))
        assertEquals("-12,5", csvCell("-12,5").removeSurrounding("\""))
    }

    @Test
    fun vanligTextOchCitat() {
        assertEquals("Tak", csvCell("Tak"))
        assertEquals("\"Tak; plåt\"", csvCell("Tak; plåt"))
        assertEquals("\"Sa \"\"hej\"\"\"", csvCell("Sa \"hej\""))
        assertEquals("", csvCell(""))
    }

    @Test
    fun tillbaka() {
        assertEquals("=SUM(A1)", csvCellTillbaka("'=SUM(A1)"))
        assertEquals("'vanlig", csvCellTillbaka("'vanlig"))
        assertEquals("Tak", csvCellTillbaka("Tak"))
    }

    @Test
    fun komponentlistanSkyddarNamnOchKalla() {
        val k =
            se.tmconnect.styrelserummet.modell.Komponent(
                namn = "=cmd",
                grupp = se.tmconnect.styrelserummet.modell.Komponentgrupp.OVRIGT,
                anskaffningsvarde = 1000,
                anskaffningsar = 2020,
                nyttjandeperiodAr = 10,
                restvarde = 0,
                kalla = "@x",
                sakerhet = se.tmconnect.styrelserummet.modell.Sakerhet.MEDEL,
            )
        val rad = tillCsv(listOf(k), 2025).lines()[1]
        assertEquals("'=cmd", rad.split(";")[0])
        assertEquals("'@x", rad.split(";")[8])
    }
}
