package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MatareKostnadTest {
    @Test
    fun forbrukningKostnadOreExempel() {
        assertEquals(150_000L, forbrukningKostnadOre(1_000_000, 150))
        assertEquals(333L, forbrukningKostnadOre(2_500, 133))
        assertEquals(0L, forbrukningKostnadOre(1, 100))
        assertEquals(0L, forbrukningKostnadOre(0, 200))
        assertNull(forbrukningKostnadOre(-1_000, 100))
        assertNull(forbrukningKostnadOre(1_000, -1))
    }
}
