package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AvgiftCsvTest {
    
    @Test
    fun `test both lists not empty`() {
        val result = avgiftCsv(listOf(2_485_000, 2_485_000), listOf(4_456_667, 4_970_000))
        val expected = "Lägenhet;Per år;Per månad\n1;2485000;207083\n2;2485000;207083\n\nÅr;Årsavgifter\n1;4456667\n2;4970000\n"
        assertEquals(expected, result)
    }
    
    @Test
    fun `test only trap list not empty`() {
        val result = avgiftCsv(emptyList(), listOf(100))
        val expected = "År;Årsavgifter\n1;100\n"
        assertEquals(expected, result)
    }
    
    @Test
    fun `test both lists empty`() {
        val result = avgiftCsv(emptyList(), emptyList())
        val expected = ""
        assertEquals(expected, result)
    }
    
    @Test
    fun `test only lag list not empty`() {
        val result = avgiftCsv(listOf(1000000), listOf())
        val expected = "Lägenhet;Per år;Per månad\n1;1000000;41667\n"
        assertEquals(expected, result)
    }
    
    @Test
    fun `test multiple trap values`() {
        val result = avgiftCsv(emptyList(), listOf(100, 200, 300))
        val expected = "År;Årsavgifter\n1;100\n2;200\n3;300\n"
        assertEquals(expected, result)
    }
}
