package se.tmconnect.rostfangst.ha

import org.junit.Test
import se.tmconnect.rostfangst.regler.ForslagTyp
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HaKonfigTest {
    
    @Test
    fun testHaKonfig() {
        // Giltig konfiguration
        val result = haKonfig(" http://ha.local:8123/ ", " abc ", " todo.att_gora ")
        assertEquals("http://ha.local:8123", result?.adress)
        assertEquals("abc", result?.token)
        assertEquals("todo.att_gora", result?.todoEntity)
        
        // Ogiltig adress (saknar http)
        val result2 = haKonfig("ha.local", "abc", "")
        assertNull(result2)
        
        // Ogiltig token (tom)
        val result3 = haKonfig("http://ha.local:8123", "  ", "")
        assertNull(result3)
    }
    
    @Test
    fun testHaSteg() {
        val konfig = HaKonfig("http://a", "t", "")
        
        // Null konfiguration
        assertEquals(emptyList(), haSteg(ForslagTyp.ATT_GORA, null))
        
        // ANTECKNING
        assertEquals(emptyList(), haSteg(ForslagTyp.ANTECKNING, konfig))
        
        // HEM
        assertEquals(listOf(HaSteg.ASSIST), haSteg(ForslagTyp.HEM, konfig))
        
        // ATT_GORA utan todoEntity
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.ATT_GORA, konfig))
        
        // ATT_GORA med todoEntity
        val konfigMedTodo = HaKonfig("http://a", "t", "todo.x")
        assertEquals(listOf(HaSteg.HANDELSE, HaSteg.TODO), haSteg(ForslagTyp.ATT_GORA, konfigMedTodo))
        
        // KALENDER
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.KALENDER, konfig))
        
        // PAMINNELSE
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.PAMINNELSE, konfig))
        
        // FRAGA
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.FRAGA, konfig))
    }
}
