package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MatareTolkningTest {
    @Test
    fun test TolkaMatareVardeTillTusendel() {
        assertEquals(12_345_000L, tolkaMatareVardeTillTusendel("12345"))
        assertEquals(12_345_600L, tolkaMatareVardeTillTusendel("12345,6"))
        assertEquals(1_234_567L, tolkaMatareVardeTillTusendel("1 234,567"))
        assertEquals(50L, tolkaMatareVardeTillTusendel("0.05"))
        assertNull(tolkaMatareVardeTillTusendel("1,2345"))
        assertNull(tolkaMatareVardeTillTusendel("-3"))
        assertNull(tolkaMatareVardeTillTusendel(""))
        assertNull(tolkaMatareVardeTillTusendel("abc"))
        assertNull(tolkaMatareVardeTillTusendel("1000000000"))
    }

    @Test
    fun test MatareVardeTillFaltText() {
        assertEquals("12345,6", matareVardeTillFaltText(12_345_600L))
        assertEquals("0,05", matareVardeTillFaltText(50L))
        assertEquals("12345", matareVardeTillFaltText(12_345_000L))
    }
}
