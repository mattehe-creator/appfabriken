package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals
import java.util.regex.Pattern

class JobbLankTest {
    
    @Test
    fun testGenereraJobbIdLength() {
        val jobId = genereraJobbId()
        assertEquals("Jobb-ID ska vara exakt 32 tecken långt", 32, jobId.length)
    }
    
    @Test
    fun testGenereraJobbIdFormat() {
        val jobId = genereraJobbId()
        val regex = Regex("[0-9a-f]{32}")
        assertTrue("Jobb-ID ska endast innehålla små hexadecimala tecken", regex.matches(jobId))
    }
    
    @Test
    fun testGenereraJobbIdUniqueness() {
        val jobId1 = genereraJobbId()
        val jobId2 = genereraJobbId()
        assertNotEquals("Två anrop till genereraJobbId ska ge olika id:n", jobId1, jobId2)
    }
    
    @Test
    fun testGenereraJobbIdManyUnique() {
        val jobIds = (1..1000).map { genereraJobbId() }.toSet()
        assertEquals("1000 anrop ska ge 1000 unika id:n", 1000, jobIds.size)
    }
}
