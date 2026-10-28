package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.rostfangst.regler.ForslagTyp

class HaKonfigTest {
    @Test
    fun konfigNormaliserasOchTrimmas() {
        assertEquals(
            HaKonfig("http://ha.local:8123", "abc", "todo.att_gora"),
            haKonfig(" http://ha.local:8123/ ", " abc ", " todo.att_gora "),
        )
    }

    @Test
    fun adressUtanHttpGerNull() {
        assertNull(haKonfig("ha.local", "abc", ""))
    }

    @Test
    fun tomTokenGerNull() {
        assertNull(haKonfig("http://ha.local:8123", "  ", ""))
    }

    @Test
    fun tomTodoEntityArTillaten() {
        assertEquals(HaKonfig("http://ha.local", "t", ""), haKonfig("http://ha.local", "t", "  "))
    }

    @Test
    fun attGoraUtanTodoGerBaraHandelse() {
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.ATT_GORA, HaKonfig("http://a", "t", "")))
    }

    @Test
    fun attGoraMedTodoGerHandelseOchTodo() {
        assertEquals(
            listOf(HaSteg.HANDELSE, HaSteg.TODO),
            haSteg(ForslagTyp.ATT_GORA, HaKonfig("http://a", "t", "todo.x")),
        )
    }

    @Test
    fun hemGerAssist() {
        assertEquals(listOf(HaSteg.ASSIST), haSteg(ForslagTyp.HEM, HaKonfig("http://a", "t", "")))
    }

    @Test
    fun utanKonfigGesInga() {
        assertEquals(emptyList<HaSteg>(), haSteg(ForslagTyp.KALENDER, null))
    }

    @Test
    fun anteckningGerInga() {
        assertEquals(emptyList<HaSteg>(), haSteg(ForslagTyp.ANTECKNING, HaKonfig("http://a", "t", "todo.x")))
    }

    @Test
    fun kalenderPaminnelseOchFragaGerHandelse() {
        val k = HaKonfig("http://a", "t", "todo.x")
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.KALENDER, k))
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.PAMINNELSE, k))
        assertEquals(listOf(HaSteg.HANDELSE), haSteg(ForslagTyp.FRAGA, k))
    }
}
