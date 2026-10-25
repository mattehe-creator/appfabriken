package se.tmconnect.flextid.regler

import java.time.MonthDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DagManadTest {
    @Test
    fun tolkaManadDagIsoFormat() {
        assertEquals(MonthDay.of(6, 1), tolkaManadDag("--06-01"))
        assertEquals(MonthDay.of(8, 31), tolkaManadDag(" --08-31 "))
    }

    @Test
    fun tolkaManadDagOgiltigInput() {
        assertNull(tolkaManadDag("1/6"))
        assertNull(tolkaManadDag(null))
    }

    @Test
    fun formateraDagManad() {
        assertEquals("31/8", formateraDagManad(MonthDay.of(8, 31)))
        assertEquals("5/1", formateraDagManad(MonthDay.of(1, 5)))
    }

    @Test
    fun formateraOchTolkaDagManadText() {
        val datum = listOf(
            MonthDay.of(6, 1),
            MonthDay.of(2, 29),
            MonthDay.of(12, 31),
        )
        for (md in datum) {
            assertEquals(md, tolkaDagManadText(formateraDagManad(md)))
        }
    }
}
