package se.tmconnect.rostfangst.export

import org.junit.Test
import org.junit.Assert.assertEquals

class CsvFaltTest {

    @Test
    fun testNormalText() {
        assertEquals("köp mjölk", csvFalt("köp mjölk"))
    }

    @Test
    fun testEmptyString() {
        assertEquals("", csvFalt(""))
    }

    @Test
    fun testComma() {
        assertEquals("\"a,b\"", csvFalt("a,b"))
    }

    @Test
    fun testQuote() {
        assertEquals("\"säg \\\"hej\\\"\"", csvFalt("säg \"hej\""))
    }

    @Test
    fun testNewline() {
        assertEquals("\"rad1\\nrad2\"", csvFalt("rad1\nrad2"))
    }

    @Test
    fun testCarriageReturn() {
        assertEquals("\"rad1\\rcarriage return\"", csvFalt("rad1\rcarriage return"))
    }

    @Test
    fun testNewlineAndQuote() {
        assertEquals("\"rad1\\n\\\"hej\\\"\"", csvFalt("rad1\n\"hej\""))
    }
}
