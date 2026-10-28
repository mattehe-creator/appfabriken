package se.tmconnect.styrelserummet.server

import org.junit.Assert.*
import org.junit.Test
import java.util.regex.Pattern

class JobbLankTest {

    @Test
    fun `genereraJobbId ska returnera en sträng med exakt 32 tecken`() {
        val id = genereraJobbId()
        assertEquals(32, id.length)
    }

    @Test
    fun `genereraJobbId ska returnera en sträng som endast innehåller hexadecimala tecken`() {
        val id = genereraJobbId()
        assertTrue(Pattern.matches("[0-9a-f]{32}", id))
    }

    @Test
    fun `genereraJobbId ska returnera olika id vid flera anrop`() {
        val id1 = genereraJobbId()
        val id2 = genereraJobbId()
        assertNotEquals(id1, id2)
    }

    @Test
    fun `genereraJobbId ska returnera 1000 olika id vid 1000 anrop`() {
        val ids = (1..1000).map { genereraJobbId() }.toSet()
        assertEquals(1000, ids.size)
    }
}
