package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class ModellnamnTest {

    @Test
    fun testGiltigaNamn() {
        assertTrue(giltigtModellnamn("qwen3:8b"))
        assertTrue(giltigtModellnamn("llama3.1:8b-instruct-q4_K_M"))
        assertTrue(giltigtModellnamn("hf.co/user/model:Q4_K_M"))
        assertTrue(giltigtModellnamn("  qwen3:8b  "))
        assertTrue(giltigtModellnamn("a".repeat(100)))
    }

    @Test
    fun testOgiltigaNamn() {
        assertFalse(giltigtModellnamn(""))
        assertFalse(giltigtModellnamn("   "))
        assertFalse(giltigtModellnamn("qwen 3"))
        assertFalse(giltigtModellnamn("modell@1"))
        assertFalse(giltigtModellnamn("mödell"))
        assertFalse(giltigtModellnamn("a".repeat(101)))
    }
}
