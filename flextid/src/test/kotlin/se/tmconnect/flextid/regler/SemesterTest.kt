package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SemesterTest {
    
    @Test
    fun `test semesterår 2026-10-21 start 4`() {
        val result = semesterArFor(LocalDate.of(2026, 10, 21), 4)
        assertEquals(LocalDate.of(2026, 4, 1), result.fran)
        assertEquals(LocalDate.of(2027, 3, 31), result.till)
    }
    
    @Test
    fun `test semesterår 2026-03-31 start 4`() {
        val result = semesterArFor(LocalDate.of(2026, 3, 31), 4)
        assertEquals(LocalDate.of(2025, 4, 1), result.fran)
        assertEquals(LocalDate.of(2026, 3, 31), result.till)
    }
    
    @Test
    fun `test semesterår 2026-04-01 start 4`() {
        val result = semesterArFor(LocalDate.of(2026, 4, 1), 4)
        assertEquals(LocalDate.of(2026, 4, 1), result.fran)
        assertEquals(LocalDate.of(2027, 3, 31), result.till)
    }
    
    @Test
    fun `test semesterår 2026-10-21 start 1`() {
        val result = semesterArFor(LocalDate.of(2026, 10, 21), 1)
        assertEquals(LocalDate.of(2026, 1, 1), result.fran)
        assertEquals(LocalDate.of(2026, 12, 31), result.till)
    }
    
    @Test
    fun `test semesterår 2028-02-29 start 4`() {
        val result = semesterArFor(LocalDate.of(2028, 2, 29), 4)
        assertEquals(LocalDate.of(2027, 4, 1), result.fran)
        assertEquals(LocalDate.of(2028, 3, 31), result.till)
    }
    
    @Test
    fun `test semesterår med felaktig startmanad`() {
        assertThrows<IllegalArgumentException> {
            semesterArFor(LocalDate.of(2026, 10, 21), 13)
        }
        
        assertThrows<IllegalArgumentException> {
            semesterArFor(LocalDate.of(2026, 10, 21), 0)
        }
    }
    
    @Test
    fun `test semesterår med standardstartmanad`() {
        val result = semesterArFor(LocalDate.of(2026, 10, 21))
        assertEquals(LocalDate.of(2026, 4, 1), result.fran)
        assertEquals(LocalDate.of(2027, 3, 31), result.till)
    }
}
