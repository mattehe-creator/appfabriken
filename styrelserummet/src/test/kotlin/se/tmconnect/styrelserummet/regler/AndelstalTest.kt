package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AndelstalTest {
    @Test
    fun fordelaEfterAndelstal_exempel1() {
        assertEquals(
            listOf(334L, 333L, 333L),
            fordelaEfterAndelstal(1000, listOf(1, 1, 1)),
        )
    }

    @Test
    fun fordelaEfterAndelstal_exempel2() {
        assertEquals(
            listOf(50L, 30L, 20L),
            fordelaEfterAndelstal(100, listOf(50, 30, 20)),
        )
    }

    @Test
    fun fordelaEfterAndelstal_exempel3() {
        assertEquals(
            listOf(3L, 7L),
            fordelaEfterAndelstal(10, listOf(1, 2)),
        )
    }

    @Test
    fun fordelaEfterAndelstal_nollTotal() {
        assertEquals(
            listOf(0L, 0L),
            fordelaEfterAndelstal(0, listOf(5, 5)),
        )
    }

    @Test
    fun fordelaEfterAndelstal_tomLista() {
        assertNull(fordelaEfterAndelstal(100, emptyList()))
    }

    @Test
    fun fordelaEfterAndelstal_negativtTotal() {
        assertNull(fordelaEfterAndelstal(-100, listOf(1, 2)))
    }

    @Test
    fun fordelaEfterAndelstal_negativtAndelstal() {
        assertNull(fordelaEfterAndelstal(100, listOf(1, -1)))
    }

    @Test
    fun fordelaEfterAndelstal_summaNoll() {
        assertNull(fordelaEfterAndelstal(100, listOf(0, 0)))
    }
}
