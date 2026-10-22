package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class StammotidplanKallelseTest {

    @Test
    fun testTidigasteKallelsedag() {
        val stammodatum = LocalDate.of(2026, 5, 15)
        val expected = LocalDate.of(2026, 4, 3)
        val actual = tidigasteKallelsedag(stammodatum)
        assertEquals(expected, actual)
    }

    @Test
    fun testSenasteKallelsedagMedFyraVeckor() {
        val stammodatum = LocalDate.of(2026, 5, 15)
        val expected = LocalDate.of(2026, 4, 17)
        val actual = senasteKallelsedag(stammodatum, 4)
        assertEquals(expected, actual)
    }

    @Test
    fun testSenasteKallelsedagMedTvåVeckor() {
        val stammodatum = LocalDate.of(2026, 5, 15)
        val expected = LocalDate.of(2026, 5, 1)
        val actual = senasteKallelsedag(stammodatum, 2)
        assertEquals(expected, actual)
    }

    @Test
    fun testSenasteKallelsedagMedOgiltigtAntalVeckor() {
        val stammodatum = LocalDate.of(2026, 5, 15)
        try {
            senasteKallelsedag(stammodatum, 1)
            fail("Expected IllegalArgumentException was not thrown")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
        try {
            senasteKallelsedag(stammodatum, 7)
            fail("Expected IllegalArgumentException was not thrown")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
}
