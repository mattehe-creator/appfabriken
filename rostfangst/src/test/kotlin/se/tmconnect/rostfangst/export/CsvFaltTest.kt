package se.tmconnect.rostfangst.export

import org.junit.Test
import org.junit.Assert.assertEquals

class CsvFaltTest {

    @Test
    fun testNormalText() {
        assertEquals("köp mjölk", csvFalt("köp mjölk"))
    }

    @Test
    fun testTextWithComma() {
        assertEquals("\"a,b\"", csvFalt("a,b"))
    }

    @Test
    fun testTextWithQuote() {
        assertEquals("\"säg \\\"hej\\\"\"", csvFalt("säg \"hej\""))
    }

    @Test
    fun testTextWithNewline() {
        assertEquals("\"rad1\\nrad2\"", csvFalt("rad1\nrad2"))
    }

    @Test
    fun testTextWithCarriageReturn() {
        assertEquals("\"rad1\\rcarriage return\"", csvFalt("rad1\rcarriage return"))
    }

    @Test
    fun testEmptyText() {
        assertEquals("", csvFalt(""))
    }

    @Test
    fun testTextWithBothSpecialCharacters() {
        assertEquals("\"a,b\\n\\r\"", csvFalt("a,b\n\r"))
    }
}
