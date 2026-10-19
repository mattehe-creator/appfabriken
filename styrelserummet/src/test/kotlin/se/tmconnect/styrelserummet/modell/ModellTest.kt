package se.tmconnect.styrelserummet.modell

import org.junit.Assert.assertEquals
import org.junit.Test

class ModellTest {

    @Test
    fun komponentgruppHarSvenskaNamn() {
        assertEquals("Stomme", Komponentgrupp.STOMME.svNamn)
    }

    @Test
    fun nyttjandeperioderCsvSummerarTill100Procent() {
        val stream = javaClass.classLoader.getResourceAsStream("nyttjandeperioder.csv")
            ?: error("Saknar nyttjandeperioder.csv")
        val rader = stream.bufferedReader().readLines().drop(1)
        val summa = rader.sumOf { rad ->
            rad.split(";")[3].toInt()
        }
        assertEquals(100, summa)
    }
}
