package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StegvisHojningTest {

    @Test
    fun stegvisHojningExempelFranUppgiften() {
        assertEquals(listOf(4_456_667L, 4_713_333L, 4_970_000L), stegvisHojning(4_200_000, 4_970_000, 3))
        assertEquals(listOf(200L), stegvisHojning(100, 200, 1))
        assertEquals(listOf(101L, 101L), stegvisHojning(100, 101, 2))
        assertEquals(listOf(175L, 150L, 125L, 100L), stegvisHojning(200, 100, 4))
        assertEquals(listOf(99L, 99L), stegvisHojning(100, 99, 2))
        assertNull(stegvisHojning(100, 200, 0))
        assertNull(stegvisHojning(100, 200, 11))
        assertNull(stegvisHojning(-1, 200, 2))
    }
}
