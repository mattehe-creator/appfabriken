package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class AvgiftCsvTest {

    @Test
    fun badaListorGerLagenhetOchTrappaEnligtExempel() {
        val result = avgiftCsv(listOf(2_485_000, 2_485_000), listOf(4_456_667, 4_970_000))
        val expected =
            "Lägenhet;Per år;Per månad\n1;2485000;207083\n2;2485000;207083\n\nÅr;Årsavgifter\n1;4456667\n2;4970000\n"
        assertEquals(expected, result)
    }

    @Test
    fun baraTrappaEnligtExempel() {
        val result = avgiftCsv(emptyList(), listOf(100))
        assertEquals("År;Årsavgifter\n1;100\n", result)
    }

    @Test
    fun badaTommaGerTomStrang() {
        assertEquals("", avgiftCsv(emptyList(), emptyList()))
    }

    @Test
    fun baraLagenhetGerManadsbeloppFranFormeln() {
        val result = avgiftCsv(listOf(1_000_000), emptyList())
        assertEquals("Lägenhet;Per år;Per månad\n1;1000000;83333\n", result)
    }

    @Test
    fun fleraTrappstegNumreras() {
        val result = avgiftCsv(emptyList(), listOf(100, 200, 300))
        assertEquals("År;Årsavgifter\n1;100\n2;200\n3;300\n", result)
    }
}
