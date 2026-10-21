package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class SemesterTest {
    @Test
    fun exempel20261021Start4() {
        val result = semesterArFor(LocalDate.of(2026, 10, 21), 4)
        assertEquals(LocalDate.of(2026, 4, 1), result.fran)
        assertEquals(LocalDate.of(2027, 3, 31), result.till)
    }

    @Test
    fun exempel20260331Start4() {
        val result = semesterArFor(LocalDate.of(2026, 3, 31), 4)
        assertEquals(LocalDate.of(2025, 4, 1), result.fran)
        assertEquals(LocalDate.of(2026, 3, 31), result.till)
    }

    @Test
    fun exempel20260401Start4() {
        val result = semesterArFor(LocalDate.of(2026, 4, 1), 4)
        assertEquals(LocalDate.of(2026, 4, 1), result.fran)
        assertEquals(LocalDate.of(2027, 3, 31), result.till)
    }

    @Test
    fun exempel20261021Start1() {
        val result = semesterArFor(LocalDate.of(2026, 10, 21), 1)
        assertEquals(LocalDate.of(2026, 1, 1), result.fran)
        assertEquals(LocalDate.of(2026, 12, 31), result.till)
    }

    @Test
    fun exempel20280229Start4() {
        val result = semesterArFor(LocalDate.of(2028, 2, 29), 4)
        assertEquals(LocalDate.of(2027, 4, 1), result.fran)
        assertEquals(LocalDate.of(2028, 3, 31), result.till)
    }

    @Test(expected = IllegalArgumentException::class)
    fun start13GerException() {
        semesterArFor(LocalDate.of(2026, 10, 21), 13)
    }

    @Test(expected = IllegalArgumentException::class)
    fun start0GerException() {
        semesterArFor(LocalDate.of(2026, 10, 21), 0)
    }

    @Test
    fun standardStartmanad() {
        val result = semesterArFor(LocalDate.of(2026, 10, 21))
        assertEquals(LocalDate.of(2026, 4, 1), result.fran)
        assertEquals(LocalDate.of(2027, 3, 31), result.till)
    }
}
